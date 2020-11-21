package cz.niwi.photoarchiveprocessor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Processor for copying photos to the target directory.
 */
public class GPhotoArchiveProcessor extends PhotoArchiveProcessor {

    HashMap<String, Integer> processedPhotos = new HashMap<>();

    /**
     * Constructor
     * @param rootPath
     */
    public GPhotoArchiveProcessor(String rootPath) {
        this(rootPath, null);
    }

    /**
     * Constructor
     * @param rootPath
     * @param presetDirectoryDateMarker
     */
    public GPhotoArchiveProcessor(String rootPath, DateMarker presetDirectoryDateMarker) {
        this(rootPath, presetDirectoryDateMarker, false);
    }

    /**
     * Constructor
     * @param rootPath
     * @param presetDirectoryDateMarker
     * @param processExactPath
     */
    public GPhotoArchiveProcessor(String rootPath, DateMarker presetDirectoryDateMarker, boolean processExactPath) {
        super(rootPath, presetDirectoryDateMarker, processExactPath);
    }


    /**
     * Process single file.
     * @param file
     * @param dateMarker
     * @param isDaySubdir
     * @return
     */
    protected boolean processFile(File file, DateMarker dateMarker, boolean isDaySubdir) {

        boolean matched = false;
        if (PhotoArchiveProcessor.fileHasTag(file, "top")) { // TODO hardcoded
            try {
                if (this.performTopPhotoAction(file, dateMarker, isDaySubdir)) {
                    this.processedPhotos.merge("top", 1, Integer::sum);
                    matched = true;
                }
            } catch (IOException e) {
                System.out.println("IO Error: " + e);
            }
        }
        /*if (PhotoArchiveProcessor.fileHasTag(file, "manerov")) { // TODO hardcoded
            if (this.performManerovPhotoAction(file, dateMarker, isDaySubdir)) {
                this.processedPhotos.merge("manerov", 1, Integer::sum);
                matched = true;
            }
        }
        if (this.performAllPhotoAction(file, dateMarker, isDaySubdir)) {
            this.processedPhotos.merge("all", 1, Integer::sum);
            matched = true;
        }*/

        return matched;
    }


    /**
     * Copies the photo to the target directory.
     * @param file
     * @param dateMarker
     * @param isDaySubdir
     * @return
     */
    protected boolean performTopPhotoAction(File file, DateMarker dateMarker, boolean isDaySubdir) throws IOException {
        return true;
    }
}
