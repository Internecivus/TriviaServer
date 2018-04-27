package com.trivia.core.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */

public class FileUtil {
    public final static Path SERVER_DIR = Paths.get(System.getProperty("jboss.server.base.dir"));
    public final static Charset CHARSET_DEFAULT = Charset.forName("UTF-8");
}
