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
    return compareVersion(version1, version2);
  }

  public static int compareVersion(String version1, String version2) {
    if (version1 == null) {
      return -1;
    }

    if (version2 == null) {
      return 1;
    }
    return compareVersion(version1.split("\\."), version2.split("\\."));
  }

  private static int compareVersion(String[] version1, String[] version2) {
    int length = Math.max(version1.length, version2.length);

    for (int i = 0; i < length; i++) {
      int v1 = i < version1.length ? parse(version1[i]) : 0;
      int v2 = i < version2.length ? parse(version2[i]) : 0;

      if (v1 < v2) {
        return -1;
      }

      if (v1 > v2) {
        return 1;
      }
    }

    return 0;
  }

  private static int parse(String v) {
    try {
      return Integer.parseInt(v);
    } catch (Exception e) {
      return -1;
    }
  }
}
