package org.jazzcommunity.development.library;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileTools {
  private FileTools() {}

  public static void copyAll(File f, File t) {
    Arrays.stream(Objects.requireNonNull(f.listFiles())).forEach(file -> copyFile(file, t));
  }

  public static void copyAll(String from, String to) {
    File f = toAbsolute(from);
    File t = toAbsolute(to);
    copyAll(f, t);
  }

  public static void createDirectories(String[] directories) {
    Arrays.stream(directories).map(FileTools::toAbsolute).forEach(FileTools::makeDirectory);
  }

  public static boolean exists(String path) {
    File file = toAbsolute(path);
    return file.exists();
  }

  public static boolean isEmpty(String path) {
    return getFiles(path).length == 0;
  }

  public static File[] getFiles(String path) {
    File file = toAbsolute(path);
    return file.listFiles();
  }

  public static File toAbsolute(String dir) {
    // TODO: This should use Path functionality to build the new file descriptor
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

    VersionComparator comp = new VersionComparator();
    return Arrays.stream(files).max(comp).get();
  }

  public static void copyConfigs(String destination) {
    String source = "tool/configs/";

    System.out.println(
        String.format("Copying configuration files from %s to %s", source, destination));

    FileTools.makeDirectory(FileTools.toAbsolute(destination));
    FileTools.copyAll(source, destination);
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
      logger.debug(e.toString());
    }
  }

  public static void backupFile(File from) {
    String name =
        String.format(
            "backup/%s_%s.backup",
            from.getName(), new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));

    if (!exists("backup")) {
      createDirectories(new String[] {"backup"});
    }

    try {
      File destination = toAbsolute(name);
      Files.copy(from.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      Logger logger = LoggerFactory.getLogger("FileTools.backupFile");
      logger.error(String.format("Failed to backup: %s", e.getMessage()));
      if (logger.isTraceEnabled()) {
        e.printStackTrace();
      }
    }
  }

  public static void deleteFolder(File folder) {
    try {
      MoreFiles.deleteRecursively(folder.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
    } catch (IOException e) {
      Logger logger = LoggerFactory.getLogger("FileTools.deleteFolder");
      logger.error(
          String.format(
              "Failed to delete folder %s: %s", folder.getAbsolutePath(), e.getMessage()));
      logger.info(e.toString());
    }
  }

  public static Boolean versionAvailable(String dir, String version) {
    boolean exists = FileTools.byVersion(dir, version).exists();

    if (!exists) {
      System.out.println(
          String.format("Missing file for version %s in directory %s", version, dir));
    }

    return exists;
  }

  public static String folderVersion(String path) {
    return new File(path).getName();
  }

  public static String extractVersion(File file) {
    // TODO: fix these regex, it's pretty much just look that it works so far
    Pattern pattern =
        file.getName().endsWith("zip")
            ? Pattern.compile("[-_]([0-9].*).zip")
            : Pattern.compile("([0-9].*)$");
    Matcher matcher = pattern.matcher(file.getName());
    // we only need exactly one match, so no need to loop
    matcher.find();
    // this must be the one match we're looking for, otherwise input is crap
    return matcher.group(1);
  }
}
