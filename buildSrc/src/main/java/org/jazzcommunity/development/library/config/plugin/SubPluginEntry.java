package org.jazzcommunity.development.library.config.plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SubPluginEntry extends UiEntry {

  private final String parent;

  public SubPluginEntry(File directory, String parent) {
    super(directory);
    this.parent = parent;
  }

  @Override
  public boolean needsPropertyEntry() {
    return false;
  }

  @Override
  public Stream<Path> getZip() {
    // sub modules will never support separate zips
    return Stream.empty();
  }

  @Override
  public String toString() {
    return "\tSubPluginEntry{\n"
        + "\t\tdirectory="
        + directory
        + ",\n \t\tiniLine='"
        + getIniLine()
        + '\''
        + ",\n \t\tparent='"
        + parent
        + '\''
        + "\n\t}";
  }
}
