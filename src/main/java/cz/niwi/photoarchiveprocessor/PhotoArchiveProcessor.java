package cz.niwi.photoarchiveprocessor;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Tag;

class PhotoArchiveProcessor {

    private File rootPath;
    private DateMarker presetDirectoryDateMarker;
    private boolean processExactPath;
    private DirectoryDateParser directoryDateParser = new NiwiDirectoryDateParser();
    private ArrayList<File> invalidPaths = new ArrayList<File>();

    public PhotoArchiveProcessor(String rootPath) {
        this(rootPath, null);
    }

    public PhotoArchiveProcessor(String rootPath, DateMarker presetDirectoryDateMarker) {
        this(rootPath, presetDirectoryDateMarker, false);
    }

    public PhotoArchiveProcessor(String rootPath, DateMarker presetDirectoryDateMarker, boolean processExactPath) {
        if (processExactPath && presetDirectoryDateMarker == null)
            throw new InvalidParameterException("Exact path must be set with pre-set date marker only!");
        this.rootPath = new File(rootPath);
        this.presetDirectoryDateMarker = presetDirectoryDateMarker;
        this.processExactPath = processExactPath;
    }

    public File getRootPath() { return rootPath; }
    public DirectoryDateParser getDirectoryDateParser() { return directoryDateParser; }
    private String getTag() { return "alterna"; }
    private Path getTargetPath() {return Paths.get("/tmp/gphs"); }


    public void sync() {
        if (this.processExactPath) {
            if (this.presetDirectoryDateMarker.hasDay())
                this.processDayDir(this.getRootPath(), this.presetDirectoryDateMarker, false);
            else if (this.presetDirectoryDateMarker.hasMonth())
                this.processMonthDir(this.getRootPath(), this.presetDirectoryDateMarker);
            else
                this.processYearDir(this.getRootPath(), this.presetDirectoryDateMarker);
            return;
        }

        System.out.println("Starting to scan the root path: " + this.getRootPath());

        List<String> dirItemsNames = Arrays.asList(this.getRootPath().list());
        Collections.sort(dirItemsNames);

        boolean yearFound = false;
        for(String yearDirName : dirItemsNames) {
            File f = new File(this.getRootPath(), yearDirName);
            if (!f.isDirectory())
                continue;
            short year = this.getDirectoryDateParser().parseYear(yearDirName);
            if (year != 0 &&
                    (this.presetDirectoryDateMarker == null || this.presetDirectoryDateMarker.getYear() == year)) {
                yearFound = true;
                this.processYearDir(f, new DateMarker(year));
            }
        }
        if (this.presetDirectoryDateMarker != null && !yearFound)
            System.err.println("Preset year not found.");
    }


    private void processYearDir(File yearDir, DateMarker dateMarker) {
        System.out.println("Processing the year: " + dateMarker.getYear());

        List<String> dirItemsNames = Arrays.asList(yearDir.list());
        Collections.sort(dirItemsNames);

        boolean monthFound = false;
        for(String monthDirName : dirItemsNames) {
            File f = new File(yearDir, monthDirName);
            if (!f.isDirectory())
                continue;
            byte month = this.getDirectoryDateParser().parseMonth(monthDirName);
            if (month != 0) {
                 if (this.presetDirectoryDateMarker == null || this.presetDirectoryDateMarker.getMonth() == month) {
                    monthFound = true;
                    this.processMonthDir(f, dateMarker.cloneWithMonth(month));
                }
            }
            else
                System.out.println("Non valid " + (f.isDirectory() ? "directory" : "file") + ": " + f.getName());
        }
        if (this.presetDirectoryDateMarker != null && !monthFound)
            System.err.println("Preset month not found.");
    }


    private void processMonthDir(File monthDir, DateMarker dateMarker) {
        System.out.println("Processing the month: " + dateMarker.getMonth());

        List<String> dirItemsNames = Arrays.asList(monthDir.list());
        Collections.sort(dirItemsNames);

        boolean dayFound = false;
        for(String dayDirName : dirItemsNames) {
            File f = new File(monthDir, dayDirName);
            if (!f.isDirectory())
                continue;
            byte day = this.getDirectoryDateParser().parseDay(dayDirName);
            if (day != 0) {
                if (this.presetDirectoryDateMarker == null || this.presetDirectoryDateMarker.getDay() == day) {
                    dayFound = true;
                    this.processDayDir(f, dateMarker.cloneWithDay(day), false);
                }
            }
            else
                System.out.println("Non valid " + (f.isDirectory() ? "directory" : "file") + ": " + f.getName());
        }
        if (this.presetDirectoryDateMarker != null && !dayFound)
            System.err.println("Preset day not found.");
    }

    private void processDayDir(File dayDir, DateMarker dateMarker, boolean isSpecialDir) {
        File[] files = dayDir.listFiles();
        int matched = 0;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                //System.out.println("Processing day subdirectory: " + f.getName());
                this.processDayDir(f, dateMarker, true);
                continue;
            }
            if (this.processFile(f, dateMarker))
                matched++;
        }
        if (matched > 0)
            System.out.println("MATCHED " + matched);
    }

    private boolean processFile(File f, DateMarker dateMarker) {

        boolean isMatch = false;
        // Skip all except JPEG files
        String filenameLowercase = f.getName().toLowerCase();
        if (!filenameLowercase.endsWith(".jpg") && !filenameLowercase.endsWith(".jpeg"))
            return false;

        Metadata metadata;
        try {
            metadata = ImageMetadataReader.readMetadata(f);
        } catch (ImageProcessingException e) {
            System.out.println("Probably not image file: " + f.getName());
            return false;
        } catch (IOException e) {
            System.out.println("File reading error: " + f.getName());
            return false;
        }

        for (Directory metadataDir : metadata.getDirectories()) {
            if (metadataDir.getName().equals("IPTC")) {
                for (Tag tag : metadataDir.getTags()) {
                    if (tag.getTagName().equals("Keywords") &&
                            tag.getDescription().matches(".*\\b"+ this.getTag() +"\\b.*")) {
                        System.out.println("MATCHED " + this.getTag() + " image: " + f.getName() + " - " + tag.getDescription());
                        isMatch = true;
                    }
                }
            }
        }
        return isMatch;
    }

    public static void main(String[] args) {
        // TODO testing value - should be obtained as program parameter
        String testRootPath = "/my/src/path";
        PhotoArchiveProcessor syncer = new PhotoArchiveProcessor(testRootPath);
        syncer.sync();
    }
}