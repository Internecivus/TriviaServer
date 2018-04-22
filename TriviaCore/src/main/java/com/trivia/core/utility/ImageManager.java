package com.trivia.core.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
public class ImageManager extends FileManager {
    public final static Path IMAGE_DIR = Paths.get(SERVER_DIR + "/data/images");
    public static String IMAGE_FILE_PREFIX = "img_";

    public static enum AllowedImageTypes {

    }

    public static Path saveImageAndGetPath(String fileName, InputStream inputStream) throws IOException {
        Path filePath = Files.createTempFile(IMAGE_DIR, IMAGE_FILE_PREFIX, fileName.substring(fileName.lastIndexOf('.'), fileName.length()));
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.getFileName();
    }
}
