package org.jazzcommunity.development;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.zip.Zip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializeTask extends DefaultTask {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private String sdk;

  @Option(option = "sdk", description = "Which SDK version to initialize. Default is latest.")
  public void setSdk(String sdk) {
    this.sdk = sdk;
  }

  @TaskAction
  public void initializeNewVersion() throws Exception {
    // 0 Only continue if there isn't already a target workspace available
    if (FileTools.exists("jde/dev/initialize") && !FileTools.isEmpty("jde/dev/initialize")) {
      String initialized = FileTools.newestVersion("jde/dev/initialize");
      System.out.println(
          String.format(
              "Version %s previously initialized. Skipping initialize task. If you wish to initialize a different version, run ideClean and initialize.",
              initialized));
      return;
    }

    // 1. Create directory structure and copy development files. (previously part of bootstrap task)
    logger.info("Create directories");
    FileTools.createDirectories(
        new String[] {
          "jde/dev/dropins",
          "jde/dev/projects",
          "jde/dev/projects/configs",
          "jde/dev/projects/launches",
          "jde/dev/projects/tests",
          "jde/dev/release/config",
          "jde/dev/release/db"
        });

    FileTools.copyAll("tool/projects/launches", "jde/dev/projects/launches");
    FileTools.copyAll("tool/projects/tests", "jde/dev/projects/tests");
    FileTools.copyAll("tool/configs", "jde/dev/projects/configs");

    // 2. Check if dropins have been provided from
    // https://jazz.net/wiki/bin/view/Main/FeatureBasedLaunches
    if (FileTools.isEmpty("jde/dev/dropins")) {
      logger.error(
          "Feature based launches haven't been placed in the dropins folder. "
              + "You will not be able to run the custom launches. "
              + "See documentation for details. "
              + "Exiting.");
      throw new RuntimeException("Dropins missing!");
    }

    // 3. Extract the newest sdk
    logger.info("Extract sdk");
    String version = sdk.isEmpty() ? FileTools.newestVersion("jde/sdks") : sdk;
    String sdkTarget = String.format("jde/dev/initialize/%s/sdk", version);

    if (!FileTools.exists(sdkTarget) || FileTools.isEmpty(sdkTarget)) {
      Zip.extract(FileTools.byVersion("jde/sdks", version), FileTools.toAbsolute(sdkTarget));
    }

    // 4. create the project files for the extracted projects so that it can be imported
    logger.info("Copy project files");
    File project = FileTools.toAbsolute("tool/projects/tests");
    File destination = FileTools.toAbsolute("jde/dev/projects/tests");
    Files.walk(project.toPath())
        .forEach(f -> copy(f, destination.toPath().resolve(project.toPath().relativize(f))));

    // 5. create proper test project structure
    Files.walk(Paths.get(FileTools.toAbsolute("jde/dev/initialize").getAbsolutePath()))
        .filter(InitializeTask::filterTestProject)
        .forEach(this::unpackProject);

    // 6. If not done yet, extract the configs from the server distribution
    logger.info("Extract server configuration");
    String scr = "jde/dev/projects/configs/scr.xml";
    String services = "jde/dev/projects/configs/services.xml";
    if (!FileTools.exists(scr) && !FileTools.exists(services)) {
      Zip.extract(
          FileTools.byVersion("jde/servers", version),
          FileTools.toAbsolute(scr),
          "server/conf/ccm/scr.xml");
      Zip.extract(
          FileTools.byVersion("jde/servers", version),
          FileTools.toAbsolute(services),
          "server/conf/ccm/services.xml");
    }

    // 7. Copy the custom log4j configuration file
    logger.info("Copy log4j configuration");
    FileTools.copyFile("jde/user/log4j/log4j.properties", "jde/dev/projects/configs/");
  }

  private void unpackProject(Path testProject) {
    Path sink = Paths.get(FileTools.toAbsolute("jde/dev/projects/tests").getAbsolutePath());
    // copy the entire project
    try {
      logger.info("Copy source project");
      Files.walk(testProject)
          .skip(1)
          .forEach(path -> copy(path, sink.resolve(testProject.relativize(path))));
    } catch (IOException e) {
      logger.warn(e.toString());
    }
    // extract project source
    Path zip = Paths.get(testProject.toAbsolutePath().toString(), "src.zip");
    try {
      logger.info("Unpack source ");
      Zip.extract(
          zip.toAbsolutePath().toFile(), FileTools.toAbsolute("jde/dev/projects/tests/src"));
    } catch (Exception e) {
      logger.warn(e.toString());
    }
  }

  private static boolean filterTestProject(Path p) {
    return p.getFileName().toString().startsWith("com.ibm.team.common.tests.utils_")
        && !p.getFileName().toString().endsWith(".jar");
  }

  private void copy(Path from, Path to) {
    try {
      logger.info("Copying from {} to {}", from, to);
      Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      logger.info(e.toString());
    }
  }
}
