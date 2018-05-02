package com.trivia.core.utility;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
public final class ImageUtil extends FileUtil {
    public final static Path IMAGE_DIR = Paths.get(SERVER_DIR + "/data/images");
    public static String IMAGE_FILE_PREFIX = "img_";
    public static int IMAGE_MAX_SIZE = 1024 * 1024 * 5;
    private static String IMAGE_FORMAT = "png";

    private ImageUtil() {}

    public static Path saveImageAndGetPath(String fileName, InputStream inputStream) throws IOException {
        validateImageFormat(fileName);

        ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);

//        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
//        while (imageReaders.hasNext()) {
//            ImageReader reader = imageReaders.next();
//            reader.getFormatName();
//        }

        //OutputStream os = new FileOutputStream(outputFile)

        BufferedImage bufferedImage = ImageIO.read(imageInputStream);
        Path filePath = Files.createTempFile(IMAGE_DIR, IMAGE_FILE_PREFIX, IMAGE_FORMAT);
        ImageIO.write(bufferedImage, IMAGE_FORMAT, filePath.toFile());

        inputStream.close();

        return filePath.getFileName();
    }

    private static void validateImageFormat(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());

        if (!Arrays.asList(ImageIO.getReaderFileSuffixes()).contains(suffix)) {
            throw new IllegalArgumentException(String.format("The %s image format is not supported.", suffix));
        }
    }
}