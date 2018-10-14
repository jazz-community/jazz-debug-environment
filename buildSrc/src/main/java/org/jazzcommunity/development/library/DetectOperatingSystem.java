package org.jazzcommunity.development.library;

public class DetectOperatingSystem {
  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

  public static boolean isLinux() {
    return System.getProperty("os.name").toLowerCase().contains("linux");
  }
}
