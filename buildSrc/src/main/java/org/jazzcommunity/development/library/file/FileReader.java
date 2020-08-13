package org.jazzcommunity.development.library.file;

import com.google.common.io.CharSource;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader {
  private static final Logger logger = LoggerFactory.getLogger("FileReader");

  private FileReader() {}

  public static Stream<String> raw(File path) {
    if (!path.exists()) {
      logger.error("File {} does not exist", path);
    }

    CharSource source = Files.asCharSource(path, StandardCharsets.UTF_8);
    return raw(source);
  }

  public static Stream<String> raw(CharSource source) {
    try {
      return source.lines();
    } catch (IOException e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      logger.error("Error occurred reading files: {}", e.getMessage());
      return Stream.empty();
    }
  }

  public static Stream<String> read(File path) {
    return raw(path).map(String::trim);
  }

  public static Stream<String> read(CharSource source) {
    return raw(source).map(String::trim);
  }
}
