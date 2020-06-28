package org.jazzcommunity.development.library.config.plugin;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IniEntry {
  String getIniLine();

  String getPropertiesLine();

  boolean needsPropertyEntry();

  Stream<Path> getZip();
}
