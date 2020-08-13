package org.jazzcommunity.development.library;

import org.gradle.internal.os.OperatingSystem;

public class DetectOperatingSystem {
  public static boolean isWindows() {
    return OperatingSystem.current().isWindows();
  }

  public static boolean isLinux() {
    return OperatingSystem.current().isLinux();
  }
}
