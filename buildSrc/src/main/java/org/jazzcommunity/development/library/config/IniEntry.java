package org.jazzcommunity.development.library.config;

import java.io.File;
import java.util.Arrays;

/**
 * Class for handling plugin directory lookup. Should decide if the plugin is a UI plugin or a
 * "service" type plugin. That will determine which kind of entry is written, and if we need a
 * properties entry.
 */
public interface IniEntry {

  static IniEntry getEntry(File directory) {
    File[] files = directory.listFiles();

    if (!directory.isDirectory() || files == null) {
      return new InvalidEntry(directory);
    }

    // the matching logic here is still pretty funky
    if (Arrays.stream(files).anyMatch(f -> f.getName().equals("META-INF"))) {
      return new UiEntry(directory);
    }

    if (Arrays.stream(files).anyMatch(f -> f.getName().equals("plugin"))) {
      return new ServiceEntry(directory);
    }

    // TODO: add more details to exception message
    throw new RuntimeException("Invalid file configuration. Fatal.");
  }

  String getIniLine();

  String getPropertiesLine();

  boolean needsPropertyEntry();

  /** Null-object class */
  class InvalidEntry implements IniEntry {

    private final File directory;

    // this should really use a logger, but oh well.
    public InvalidEntry(File directory) {
      this.directory = directory;
    }

    @Override
    public String getIniLine() {
      System.out.println(String.format("Invalid user workspace entry in %s", directory));
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
    public String toString() {
      return "\tInvalidEntry{\n" + "\t\tdirectory=" + directory + "\n\t}";
    }
  }
}
