package org.jazzcommunity.development.library.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileResolver {
  private static final Logger logger = LoggerFactory.getLogger("FileResolver");

  private FileResolver() {}

  public static Stream<Path> findInDirectory(Path directory, String endsWith) {
    try {
      return Files.find(directory, 1, (p, b) -> p.getFileName().toString().endsWith(endsWith));
    } catch (IOException e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      logger.info(
          "File ending with '{}' in '{}' not found. Ignoring this entry.", directory, endsWith);
      return null;
    }
  }
}
