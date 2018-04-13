package com.trivia.core.resources;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
public class FileManager {
    public final static Path PATH_DATA = Paths.get(System.getProperty("jboss.server.data.dir"));
}
