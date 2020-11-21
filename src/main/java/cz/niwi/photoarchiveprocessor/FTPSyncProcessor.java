package cz.niwi.photoarchiveprocessor;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Processor for copying photos to the target directory.
 */
public class FTPSyncProcessor extends PhotoArchiveProcessor {

    HashMap<String, Integer> processedPhotos = new HashMap<>();

    /**
     * Constructor
     * @param rootPath
     */
    public FTPSyncProcessor(String rootPath) {
        this(rootPath, null);
    }

    /**
     * Constructor
     * @param rootPath
     * @param presetDirectoryDateMarker
     */
    public FTPSyncProcessor(String rootPath, DateMarker presetDirectoryDateMarker) {
        this(rootPath, presetDirectoryDateMarker, false);
    }

    /**
     * Constructor
     * @param rootPath
     * @param presetDirectoryDateMarker
     * @param processExactPath
     */
    public FTPSyncProcessor(String rootPath, DateMarker presetDirectoryDateMarker, boolean processExactPath) {
        super(rootPath, presetDirectoryDateMarker, processExactPath);

        String SFTPHOST = "10.20.30.40";
        int SFTPPORT = 22;
        String SFTPUSER = "username";
        String SFTPPASS = "password";
        String SFTPWORKINGDIR = "/export/home/kodehelp/";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            File f = new File(FILETOTRANSFER);
            channelSftp.put(new FileInputStream(f), f.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Process single file.
     * @param file
     * @param dateMarker
     * @param isDaySubdir
     * @return
     */
    protected boolean processFile(File file, DateMarker dateMarker, boolean isDaySubdir) {

        boolean processed = false;

        // Skip all except JPEG images and MP4 videos
        if (!PhotoArchiveProcessor.fileHasExtension(file, Arrays.asList("jpg", "jpeg", "mp4"))) {
            System.out.println("Not supported file: " + file.getName());
            return false;
        }


    }
}