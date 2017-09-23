
import java.io.IOException;
import java.util.*;
import java.io.File;
import java.io.FilenameFilter;
import java.time.Month;
import java.time.format.TextStyle;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Tag;

class GPhotosSyncer {

    private File rootPath;
    private File yearPath;
    private File monthPath;
    private File dayPath;
    private Locale locale = new Locale("cs");
    private ArrayList<File> invalidPaths = new ArrayList<File>();


    public GPhotosSyncer() {
    }


    public File getRootPath() {
        return rootPath;
    }
    public File getYearPath() {
        return yearPath;
    }
    public File getMonthPath() {
        return monthPath;
    }
    public File getDayPath() {
        return dayPath;
    }
    public Locale getLocale() { return locale; }


    public void sync() {
        if (this.getDayPath() != null) {
            this.processDayDir(this.getDayPath());
            return;
        }
        if (this.getMonthPath() != null) {
            this.processMonthDir(this.getMonthPath());
            return;
        }
        if (this.getYearPath() != null) {
            this.processYearDir(this.getYearPath());
            return;
        }

        System.out.println("Starting to scan the root path: " + this.getRootPath());

        Calendar now = Calendar.getInstance();
        String thisYearString = String.valueOf(now.get(Calendar.YEAR));

        String[] directoriesArray = this.getRootPath().list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                File f = new File(current, name);
                return f.isDirectory() &&
                        f.getName().compareTo("1900") >= 0 &&
                        f.getName().compareTo(thisYearString) <= 0;
            }
        });
        List<String> yearDirectories = Arrays.asList(directoriesArray);
        Collections.sort(yearDirectories);
        Collections.reverse(yearDirectories);

        yearDirectories.forEach(name -> this.processYearDir(new File(this.getRootPath(), name)));
    }


    private void processYearDir(File yearDir) {
        String year = yearDir.getName();
        System.out.println("Processing the year: " + year);

        ArrayList<String> possibleMonthNames = new ArrayList<String>();
        for (int i=1; i < 12; i++) {
            String monthNum = String.format("%02d", i);
            String monthName = Month.of(i).getDisplayName(TextStyle.FULL_STANDALONE, this.getLocale());
            possibleMonthNames.add(monthNum);
            possibleMonthNames.add(monthNum + " - " + monthName);
            possibleMonthNames.add(monthName + " " + year);
        }

        // list all subfolders and pick the right ones for months
        String[] directoriesArray = yearDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                File f = new File(current, name);
                if (f.isDirectory() && possibleMonthNames.contains(f.getName().toLowerCase()))
                    return true;
                System.out.println("Non valid " + (f.isDirectory() ? "directory" : "file") + ": " + f.getName());
                return false;
            }
        });

        List<String> monthDirectories = Arrays.asList(directoriesArray);
        Collections.sort(monthDirectories);
        Collections.reverse(monthDirectories);

        monthDirectories.forEach(name -> this.processMonthDir(new File(yearDir, name)));
    }


    private void processMonthDir(File monthDir) {
        String month = monthDir.getName();
        System.out.println("Processing the month: " + month);

        // list all subfolders and pick the right ones for days
        String[] directoriesArray = monthDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                File f = new File(current, name);
                if (f.isDirectory() && (
                        f.getName().matches("[0123]?\\d(\\.?\\s+\\w+.*)?")) || f.getName().matches("\\d{8}"))
                    return true;
                if (!f.getName().matches("CLIP\\d*"))
                    System.out.println("Non valid " + (f.isDirectory() ? "directory" : "file") + ": " + f.getName());
                return false;
            }
        });

        List<String> dayDirectories = Arrays.asList(directoriesArray);
        Collections.sort(dayDirectories);
        Collections.reverse(dayDirectories);

        dayDirectories.forEach(name -> this.processDayDir(new File(monthDir, name)));

    }


    private void processDayDir(File dayDir) {
        File[] files = dayDir.listFiles();
        for (int i=0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                System.out.println("Processing day subdirectory: " + f.getName());
                continue;
            }
            Metadata metadata;
            try {
                metadata = ImageMetadataReader.readMetadata(files[i]);
            } catch (ImageProcessingException e) {
                System.out.println("Probably not image file: " + f.getName());
                return;
            } catch (IOException e) {
                System.out.println("File reading error: " + f.getName());
                return;
            }

            for (Directory directory : metadata.getDirectories()) {
                if (directory.getName().equals("IPTC")) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagName().equals("Keywords") &&
                                tag.getDescription().matches(".*\\btop\\b.*"))
                            System.out.println("TOP image: " + f.getName() + " - " + tag.getDescription());
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        GPhotosSyncer syncer = new GPhotosSyncer();
        syncer.sync();
    }
}