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
    System.out.println("Available SDK versions:");
    printFiles("jde/sdks");
    System.out.println("Available Servers / JRE versions:");
    printFiles("jde/servers");
    System.out.println("Available databases:");
    printFiles("jde/dbs");
    System.out.println("Available configurations:");
    printFiles("tool/sdk_files");
    System.out.println("Available P2 Repository URLs:");
    printP2Repos("jde/p2repo");
    System.out.println("Set up run times: ");
    RuntimeDetector.getRuntimes().forEach(System.out::println);
    System.out.println("Available plugins:");
    ConfigReader.userConfiguration().forEach(System.out::println);
  }

  private static void printFiles(String path) {
    File directory = FileTools.toAbsolute(path);
    Arrays.stream(directory.listFiles()).forEach(file -> System.out.println("\t" + file.getName()));
  }

  private static void printP2Repos(String path) {
    File directory = FileTools.toAbsolute(path);
    if (directory.listFiles() != null) {
      Arrays.stream(Objects.requireNonNull(directory.listFiles()))
          .forEach(r -> System.out.println(String.format("\tfile:%s", r.getAbsolutePath())));
    }
  }
}
