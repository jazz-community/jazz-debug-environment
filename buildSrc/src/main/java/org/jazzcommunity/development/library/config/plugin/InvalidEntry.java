package org.jazzcommunity.development.library.config.plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Null object class
class InvalidEntry implements IniEntry {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final File directory;

  public InvalidEntry(File directory) {
    this.directory = directory;
  }

  @Override
  public String getIniLine() {
    logger.error("Invalid user workspace entry in {}", directory);
    return "";
  }

  @Override
  public String getPropertiesLine() {
    return "";
  }

  @Override
  public boolean needsPropertyEntry() {
    return false;
  }

  @Override
  public Stream<Path> getZip() {
    logger.error("Invalid user workspace entry in {}", directory);
    return Stream.empty();
  }

  @Override
  public String toString() {
    return "\tInvalidEntry{\n" + "\t\tdirectory=" + directory + "\n\t}";
  }
}
