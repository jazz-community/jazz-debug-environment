package org.jazzcommunity.development.library.file;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FileWriter {
  private FileWriter() {}

  public static void writeFile(File destination, Stream<String> content) throws IOException {
    Files.asCharSink(destination, StandardCharsets.UTF_8).writeLines(content);
  }

  public static void appendFile(File destination, Stream<String> content) throws IOException {
    Files.asCharSink(destination, StandardCharsets.UTF_8, FileWriteMode.APPEND).writeLines(content);
  }

  public static void appendFile(File destination, String line) throws IOException {
    Files.asCharSink(destination, StandardCharsets.UTF_8, FileWriteMode.APPEND)
        .write(line + System.lineSeparator());
  }
}
