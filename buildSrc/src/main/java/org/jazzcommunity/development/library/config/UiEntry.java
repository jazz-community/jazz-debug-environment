package org.jazzcommunity.development.library.config;

import java.io.File;
import org.jazzcommunity.development.library.DetectOperatingSystem;

class UiEntry implements IniEntry {

  private final File directory;

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
  public String toString() {
    return "\tUiEntry {\n"
        + "\t\tdirectory = "
        + directory
        + "\n\t\tini line = "
        + getIniLine()
        + "\n\t}";
  }
}
