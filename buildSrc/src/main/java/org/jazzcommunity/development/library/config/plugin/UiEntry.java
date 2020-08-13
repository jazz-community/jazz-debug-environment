package org.jazzcommunity.development.library.config.plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.jazzcommunity.development.library.DetectOperatingSystem;
import org.jazzcommunity.development.library.file.FileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UiEntry implements IniEntry {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  protected final File directory;

  public UiEntry(File directory) {
    this.directory = directory;
  }

  @Override
  public String getIniLine() {
    // TODO: duplicated code
    String formatted = directory.getPath();

    if (DetectOperatingSystem.isWindows()) {
      formatted = formatted.replaceAll("\\\\", "/");
      formatted = formatted.replaceAll(":", "\\\\:");
    }

    return String.format("reference\\:file\\:%s@start", formatted);
  }

  @Override
  public String getPropertiesLine() {
    // UI plugins don't require an entry in the .properties file.
    return "";
  }

  @Override
  public boolean needsPropertyEntry() {
    return false;
  }

  @Override
  public Stream<Path> getZip() {
    return FileResolver.findInDirectory(directory.toPath(), ".zip");
  }

  @Override
  public String toString() {
    return "\tUiEntry{\n"
        + "\t\tdirectory="
        + directory
        + ",\n \t\tiniLine='"
        + getIniLine()
        + '\''
        + "\n\t}";
  }
}
