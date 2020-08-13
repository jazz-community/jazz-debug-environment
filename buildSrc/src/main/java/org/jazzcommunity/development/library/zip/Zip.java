package org.jazzcommunity.development.library.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zip {

  private static final Logger logger = LoggerFactory.getLogger(Zip.class);

  /** Largely taken from https://gist.github.com/cassiuscai/cec0c8e418ce265d4227fef56d874aa6 */
  private static final int BUFFER_SIZE = 1 << 12;

  public static void extractWithErrorHandling(Path from, Path to) {
    try {
      Zip.extract(from.toFile(), to.toFile());
    } catch (IOException e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      e.printStackTrace();
      logger.error(
          "Failed to extract '{}' to temporary directory '{}': '{}'", from, to, e.getMessage());
    }
  }

  // these are just wrappers to enable logging for extracted files
  public static void extract(File from, File to) throws IOException {
    extract(from, to, "");
  }

  public static void extract(File from, File to, String subfolder) throws IOException {
    logger.info("Decompress {} to {}", from, to);
    Zip.decompress(from, to, subfolder);
  }

  // this also needs refactoring badly...
  public static void decompress(File source, File target, String subfolder) throws IOException {
    ZipInputStream stream =
        new ZipInputStream(new BufferedInputStream(new FileInputStream(source)));

    ZipEntry entry;
    while ((entry = stream.getNextEntry()) != null) {
      if (entry.getName().startsWith(subfolder)) {
        // ok, this is really funky, but trust me.
        String name = entry.getName().replace(subfolder, "");
        if (entry.isDirectory()) {
          makeDirectories(target, name);
        } else {
          String directory = directoryPart(name);
          if (directory != null) {
            makeDirectories(target, directory);
          }
          extractFile(stream, target, name);
        }
      }
    }
    stream.close();
  }

  private static void makeDirectories(File dir, String path) {
    File file = new File(dir, path);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  private static String directoryPart(String name) {
    int s = name.lastIndexOf(File.separatorChar);
    return s == -1 ? null : name.substring(0, s);
  }

  private static void extractFile(InputStream inputStream, File outDir, String name)
      throws IOException {
    int count;
    byte[] buffer = new byte[BUFFER_SIZE];
    File destination = new File(outDir, name);
    BufferedOutputStream out =
        new BufferedOutputStream(new FileOutputStream(destination), BUFFER_SIZE);
    while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
      out.write(buffer, 0, count);
    }
    out.close();
  }
}
