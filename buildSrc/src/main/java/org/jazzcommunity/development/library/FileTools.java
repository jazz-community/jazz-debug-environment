package org.jazzcommunity.development.library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileTools {
  private FileTools() {}

  public static void copyAll(String from, String to) {
    File f = toAbsolute(from);
    File t = toAbsolute(to);
    Arrays.stream(Objects.requireNonNull(f.listFiles())).forEach(file -> copyFile(file, t));
  }

  public static void createDirectories(String[] directories) {
    Arrays.stream(directories).map(FileTools::toAbsolute).forEach(FileTools::makeDirectory);
  }

  public static boolean exists(String path) {
    File file = toAbsolute(path);
    return file.exists();
  }

  public static File[] getFiles(String path) {
    File file = toAbsolute(path);
    return file.listFiles();
  }

  public static File toAbsolute(String dir) {
    return new File(String.format("%s/%s", System.getProperty("user.dir"), dir));
  }

  public static File byVersion(String dir, String version) {
    return Arrays.stream(getFiles(dir))
        .filter(file -> file.getName().contains(version))
        .findFirst()
        .orElse(new File("File that doesn't exist"));
  }

  public static String newestVersion(String dir) {
    // ugly workaround, but when this happens, there isn't anything there.
    if (getFiles(dir) == null || getFiles(dir).length <= 0) {
      return null;
    }

    File file = newestFile(dir);
    if (file.isDirectory()) {
      return file.getName();
    }
    return extractVersion(file);
  }

  public static File newestFile(String dir) {
    File[] files = getFiles(dir);

    if (files == null) {
      throw new RuntimeException(String.format("No files available in %s.", dir));
    }

    return Arrays.stream(files).min(Comparator.reverseOrder()).get();
  }

  public static void makeDirectory(File path) {
    try {
      Files.createDirectories(path.toPath());
    } catch (IOException e) {
      Logger logger = LoggerFactory.getLogger("FileTools.makeDirectory");
      logger.error(String.format("Could not create %s because %s", path, e.getMessage()));
    }
  }

  public static void setExecutable(String filepath) {
    setExecutable(toAbsolute(filepath));
  }

  public static void setExecutable(File file) {
    if (System.getProperty("os.name").contains("inux")) {
      file.setExecutable(true);
    }
  }

  public static void copyFile(String from, String to) {
    copyFile(toAbsolute(from), toAbsolute(to));
  }

  public static void copyFile(File from, File to) {
    try {
      File destination = new File(to, from.getName());
      Files.copy(from.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      Logger logger = LoggerFactory.getLogger("FileTools.copyFile");
      logger.error(String.format("Failed to copy files: %s", e.getMessage()));
      if (logger.isTraceEnabled()) {
        e.printStackTrace();
      }
    }
  }

  private static String extractVersion(File file) {
    Pattern pattern = Pattern.compile("-([0-9].*).zip$");
    Matcher matcher = pattern.matcher(file.getName());
    // we only need exactly one match, so no need to loop
    matcher.find();
    // this must be the one match we're looking for, otherwise input is crap
    return matcher.group(1);
  }
}
