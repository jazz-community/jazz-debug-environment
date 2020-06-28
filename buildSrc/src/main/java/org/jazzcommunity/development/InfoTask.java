package org.jazzcommunity.development;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.RuntimeDetector;
import org.jazzcommunity.development.library.config.ConfigReader;

public class InfoTask extends DefaultTask {
  /**
   * Print available versions, and possibly missing files. For every version, there must be an sdk,
   * server and database file available.
   */
  @TaskAction
  public void versions() {
    if (!FileTools.exists("jde")) {
      System.out.println(
          "'jde' folder not found. Run bootstrap to start using the jazz development environment.");
      return;
    }
    System.out.println("Configured Java executable:");
    System.out.println(String.format("\t%s", ConfigReader.javaPath()));
    System.out.println("Available SDK versions:");
    printFiles("jde/sdks");
    System.out.println("Available Servers / JRE versions:");
    printFiles("jde/servers");
    System.out.println("Available databases:");
    printFiles("jde/dbs");
    System.out.println("Available configurations:");
    printConfigurations();
    System.out.println("Available P2 Repository URLs:");
    printP2Repos();
    System.out.println("Available drop-ins:");
    printFiles("jde/dropins");
    System.out.println("Set up run times: ");
    RuntimeDetector.getRuntimes().forEach(System.out::println);
    System.out.println("Available plugins:");
    ConfigReader.userConfiguration().forEach(System.out::println);
    System.out.println("Known JTS locations::");
    ConfigReader.readConfig(FileTools.toAbsolute("jde/user/jts_locations.cfg"))
        .map(s -> "\t" + s)
        .forEach(System.out::println);
  }

  private void printFiles(String path) {
    File directory = FileTools.toAbsolute(path);
    Arrays.stream(Objects.requireNonNull(directory.listFiles()))
        .forEach(file -> System.out.println("\t" + file.getName()));
  }

  private void printP2Repos() {
    File directory = FileTools.toAbsolute("jde/p2repo");
    if (directory.listFiles() != null) {
      Arrays.stream(Objects.requireNonNull(directory.listFiles()))
          .forEach(r -> System.out.println(String.format("\tfile:%s", r.getAbsolutePath())));
    }
  }

  private void printConfigurations() {
    File[] files = FileTools.toAbsolute("tool/sdk_files").listFiles();
    Arrays.stream(Objects.requireNonNull(files))
        .map(FileTools::extractVersion)
        .distinct()
        .map(SdkPair::new)
        .map(s -> "\t" + s)
        .forEach(System.out::println);
  }

  private static class SdkPair {
    private final String files;
    private final String config;

    SdkPair(String version) {
      this.files = formatConfigLine("files", version);
      this.config = formatConfigLine("config", version);
    }

    private static String formatConfigLine(String file, String version) {
      return FileTools.exists(String.format("tool/sdk_files/sdk_%s_%s", file, version))
          ? String.format("sdk_%s_%s", file, version)
          : String.format("missing for %s", version.replace(".cfg", ""));
    }

    @Override
    public String toString() {
      return "sdk file list: " + files + ", \tsdk runtime configuration: " + config;
    }
  }
}
