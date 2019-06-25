package org.jazzcommunity.development.library;

import java.util.Arrays;

public final class VersionChecker {
  private VersionChecker() {}

  private static final String[] REQUIRED_FILES = {
    "jde/servers", "jde/sdks", "jde/dbs", "tool/sdk_files"
  };

  public static Boolean canSetup(String version) {
    return Arrays.stream(REQUIRED_FILES).allMatch(d -> FileTools.versionAvailable(d, version));
  }
}
