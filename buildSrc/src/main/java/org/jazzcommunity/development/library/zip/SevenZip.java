package org.jazzcommunity.development.library.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.jazzcommunity.development.library.FileTools;

/**
 * This is modelled after
 * https://memorynotfound.com/java-7z-seven-zip-example-compress-decompress-file/
 */
public class SevenZip {

  private static final int BUFFER_SIZE = 1 << 10;

  private SevenZip() {}

  public static void compress(String name, String[] filter, File... files) throws IOException {
    try (SevenZOutputFile out = new SevenZOutputFile(FileTools.toAbsolute(name))) {
      Arrays.stream(files).forEach(file -> toArchive(out, file, "", filter));
    }
  }

  public static void compress(String name, File... files) throws IOException {
    compress(name, new String[] {""}, files);
  }

  public static void decompress(File file, File to) throws IOException {
    // this needs some serious cleaning up
    SevenZFile from = new SevenZFile(file);
    SevenZArchiveEntry entry;
    while ((entry = from.getNextEntry()) != null) {
      if (entry.isDirectory()) {
        continue;
      }

      File current = new File(to, entry.getName());
      File parent = current.getParentFile();

      if (!parent.exists()) {
        parent.mkdirs();
      }

      FileOutputStream out = new FileOutputStream(current);
      byte[] buffer = new byte[(int) entry.getSize()];
      from.read(buffer, 0, buffer.length);
      out.write(buffer);
      out.close();
    }
  }

  private static void toArchive(
      SevenZOutputFile out, File file, String directory, String[] filter) {
    try {
      final String name =
          directory.equals("")
              ? file.getName()
              : String.format("%s%s%s", directory, File.separator, file.getName());

      if (file.isFile()) {
        SevenZArchiveEntry entry = out.createArchiveEntry(file, name);
        out.putArchiveEntry(entry);

        // extract this to stream lib function
        FileInputStream stream = new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count;
        while ((count = stream.read(buffer)) > 0) {
          out.write(buffer, 0, count);
        }

        out.closeArchiveEntry();
        // this next condition is super ugly, but will work for the current filtering
      } else if (file.isDirectory()
          && Arrays.stream(filter).noneMatch(f -> file.getName().equals(f))) {
        Arrays.stream(Objects.requireNonNull(file.listFiles()))
            .forEach(content -> toArchive(out, content, name, filter));
      } else {
        // this could also occur when the file content is something weird, I guess a symlink
        // would mess this up, for example.
        System.out.println(String.format("Skipped %s because of backup mode", file.getName()));
      }
    } catch (Exception e) {
      // not sure yet how and where to handle these properly
      e.printStackTrace();
    }
  }
}
