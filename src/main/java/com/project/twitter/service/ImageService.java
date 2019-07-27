package com.project.twitter.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import static com.project.twitter.common.GeneralUtil.getFileExtension;

@Service("imageService")
public class ImageService {
    private static final Logger log = Logger.getLogger(ImageService.class);

    public static byte[] ImageToByte(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = stream.read(buf)) != -1; ) {
                byteStream.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
            log.error("Exception", ex);
        }
        finally {
            byteStream.close();
        }
        return byteStream.toByteArray();
    }

    public static String generateUUIDName(FileItem fileItem) {
        UUID uuid = UUID.randomUUID();
        String UUIDName = uuid.toString();
        String extension = getFileExtension(fileItem);
        return UUIDName + extension;
    }
}