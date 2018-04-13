package com.trivia.core.resources;

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
    public final static Path PATH_DATA_IMAGES = Paths.get(PATH_DATA + "/images");
    public static String IMAGE_FILE_PREFIX = "img_";

    public static enum AllowedImageTypes {

    }

    public static Path saveImageAndGetPath(InputStream inputStream) throws IOException {
        Path filePath = Files.createTempFile(PATH_DATA_IMAGES, IMAGE_FILE_PREFIX, ".jpg");
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.getFileName();
    }
}
