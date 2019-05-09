package org.jazzcommunity.development.library;

import java.io.File;
import java.util.Comparator;

public class VersionComparator implements Comparator<File> {

  @Override
  public int compare(File f1, File f2) {
    if (f1 == null) {
      return -1;
    }

    if (f2 == null) {
      return 1;
    }

    String[] version1 = FileTools.extractVersion(f1).split("\\.");
    String[] version2 = FileTools.extractVersion(f2).split("\\.");
    int length = Math.max(version1.length, version2.length);

    for (int i = 0; i < length; i++) {
      int v1 = i < version1.length ? Integer.parseInt(version1[i]) : 0;
      int v2 = i < version2.length ? Integer.parseInt(version2[i]) : 0;

      if (v1 < v2) {
        return -1;
      }

      if (v1 > v2) {
        return 1;
      }
    }

    return 0;
  }
}
