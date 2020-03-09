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
    // 1. Extract the newest sdk
    String version = sdk.isEmpty() ? FileTools.newestVersion("jde/sdks") : sdk;
    String sdkTarget = String.format("jde/dev/initialize/%s/sdk", version);

    if (!FileTools.exists(sdkTarget) || FileTools.isEmpty(sdkTarget)) {
      Zip.extract(FileTools.byVersion("jde/sdks", version), FileTools.toAbsolute(sdkTarget));
    }

    // 2. create the project files for the extracted projects so that it can be imported
    logger.info("Copy project files");
    File project = FileTools.toAbsolute("tool/projects/tests");
    File destination = FileTools.toAbsolute("jde/dev/projects/tests");
    Files.walk(project.toPath())
        .forEach(f -> copy(f, destination.toPath().resolve(project.toPath().relativize(f))));

    // 3. create proper test project structure
    Files.walk(Paths.get(FileTools.toAbsolute("jde/dev/initialize").getAbsolutePath()))
        .filter(InitializeTask::filterTestProject)
        .forEach(
            z -> {
              Path d = Paths.get(FileTools.toAbsolute("jde/dev/projects/tests").getAbsolutePath());
              // copy the entire project
              try {
                logger.info("Copy source project");
                // TODO: skip 1 because the root folder already exists. Should find a prettier
                // solution to this
                Files.walk(z).skip(1).forEach(path -> copy(path, d.resolve(z.relativize(path))));
              } catch (IOException e) {
                logger.warn(e.toString());
              }
              // extract project source
              Path zip = Paths.get(z.toAbsolutePath().toString(), "src.zip");
              try {
                logger.info("Unpack source ");
                Zip.extract(
                    zip.toAbsolutePath().toFile(),
                    FileTools.toAbsolute("jde/dev/projects/tests/src"));
              } catch (Exception e) {
                logger.warn(e.toString());
              }
            });

    // TODO: Document where to find the necessary jar
    // 4. Check if dropins have been provided
    // https://jazz.net/wiki/bin/view/Main/FeatureBasedLaunches
    if (FileTools.isEmpty("jde/dev/dropins")) {
      logger.error(
          "Feature based launches haven't been placed in the dropins folder. "
              + "You will not be able to run the custom launches. "
              + "See documentation for details. "
              + "Exiting.");
      return;
    }

    // 5. If not done yet, extract the configs from the server distribution
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

    // 6. Copy the custom log4j configuration file
    FileTools.copyFile("jde/user/log4j/log4j.properties", "jde/dev/projects/configs/");
  }

  private static boolean filterTestProject(Path p) {
    return p.getFileName().toString().startsWith("com.ibm.team.common.tests.utils_")
        && !p.getFileName().toString().endsWith(".jar");
  }

  private void copy(Path from, Path to) {
    try {
      logger.debug(String.format("Copying from %s to %s", from, to));
      Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      logger.warn(e.toString());
    }
  }
}
