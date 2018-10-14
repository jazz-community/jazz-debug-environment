package org.jazzcommunity.development.library.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {

  /** Largely taken from https://gist.github.com/cassiuscai/cec0c8e418ce265d4227fef56d874aa6 */
  private static final int BUFFER_SIZE = 1 << 12;

  public static void decompress(File source, File target) throws IOException {
    decompress(source, target, "");
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
    int count = -1;
    byte buffer[] = new byte[BUFFER_SIZE];
    BufferedOutputStream out =
        new BufferedOutputStream(new FileOutputStream(new File(outDir, name)), BUFFER_SIZE);
    while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
      out.write(buffer, 0, count);
    }
    out.close();
  }
}