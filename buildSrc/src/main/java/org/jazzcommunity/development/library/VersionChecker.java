package org.jazzcommunity.development.library;

import java.util.Arrays;

public final class VersionChecker {
  private VersionChecker() {}

  private static final String[] REQUIRED_FILES = {
    "jde/servers", "jde/sdks", "jde/dbs", "tool/sdk_files"
  };

  public static Boolean canSetup(String version) {
    return Arrays.stream(REQUIRED_FILES).allMatch(d -> check(d, version));
  }

  // Extracted to function for explicit side-effect. Easiest way to deal with user
  // feedback here... I know that this could do with a cleaner solution.
  private static Boolean check(String dir, String version) {
    boolean exists = FileTools.byVersion(dir, version).exists();

    if (!exists) {
      System.out.println(
          String.format("Missing file for version %s in directory %s", version, dir));
    }

    return exists;
  }
}
