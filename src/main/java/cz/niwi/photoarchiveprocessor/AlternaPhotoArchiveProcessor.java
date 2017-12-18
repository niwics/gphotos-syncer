package cz.niwi.photoarchiveprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processor for copying photos to the target directory.
 */
public class AlternaPhotoArchiveProcessor extends PhotoArchiveProcessor {

    /**
     * Directory to which copy the photo files.
     */
    private Path targetPath;

    /**
     * Constructor
     * @param rootPath
     */
    public AlternaPhotoArchiveProcessor(String rootPath) {
        this(rootPath, null);
    }

    /**
     * Constructor
     * @param rootPath
     * @param presetDirectoryDateMarker
     */
    public AlternaPhotoArchiveProcessor(String rootPath, DateMarker presetDirectoryDateMarker) {
        this(rootPath, presetDirectoryDateMarker, false);
    }

    /**
     * Constructor
     * @param rootPath
     * @param presetDirectoryDateMarker
     * @param processExactPath
     */
    public AlternaPhotoArchiveProcessor(String rootPath, DateMarker presetDirectoryDateMarker, boolean processExactPath) {
        super(rootPath, presetDirectoryDateMarker, processExactPath);
        this.setImageTag("alterna");
    }

    protected Path getTargetPath() {return this.targetPath; }

    /**
     * Sets the target path directory and forces its existency.
     * @param pathString
     * @return
     */
    protected void setTargetPath(String pathString) {
        Path path = Paths.get(pathString);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            }
            catch (IOException e) {
                System.err.println("Could not create target directory: " + e.getMessage());
            }

        }
        this.targetPath = path;
    }

    /**
     * Copies the photo to the target directory.
     * @param file
     * @param dateMarker
     * @param isDaySubdir
     * @return
     */
    protected boolean performPhotoAction(File file, DateMarker dateMarker, boolean isDaySubdir) {

        File dayDir = isDaySubdir ? file.getParentFile().getParentFile() : file.getParentFile();
        String targetDirName = dateMarker.getIsoDate();
        if (this.parseDayTitle(dayDir.getName()) != "")
            targetDirName += " " + this.parseDayTitle(dayDir.getName());
        if (isDaySubdir)
            targetDirName += "/" + file.getParentFile().getName();
        File targetDir = new File(this.getTargetPath().toString(), targetDirName);
        targetDir.mkdirs();

        // copy the file
        File targetFile = new File(targetDir, file.getName());
        if (!targetFile.exists()) {
            try {
                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException e) {
                System.err.println("Error while copying the file " + file.getPath() + " to: " + targetFile.getPath());
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Extracts the day title (description) from the day directory name.
     * Example name format: "12. středa   This is day title"
     * @param directoryName
     * @return
     */
    private static String parseDayTitle(String directoryName) {
        Pattern pattern = Pattern.compile("[0123]?\\d\\.?\\s(pondělí|úterý|středa|čtvrtek|pátek|sobota|neděle)\\s+(\\w+.*)", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = pattern.matcher(directoryName);
        if (m.matches() && m.group(2) != null)
            return m.group(2);
        return "";
    }
}
