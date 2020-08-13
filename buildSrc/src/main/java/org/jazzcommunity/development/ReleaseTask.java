package org.jazzcommunity.development;

import com.google.common.io.CharSink;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Properties;
import java.util.stream.Stream;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.SdkConfiguration;
import org.jazzcommunity.development.library.VersionComparator;
import org.jazzcommunity.development.library.config.ConfigReader;
import org.jazzcommunity.development.library.file.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleaseTask extends DefaultTask {
  private boolean deploy;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Option(
      option = "deploy",
      description =
          "Deploy a new release to the tools folder for a pull-request. Default is false.")
  public void setDeploy(boolean deploy) {
    this.deploy = deploy;
  }

  @TaskAction
  public void release() throws Exception {
    String releaseVersion = FileTools.newestVersion("jde/dev/initialize");

    Path workspaces =
        Paths.get(String.format("%s/.goomph/ide-workspaces", System.getProperty("user.home")));

    Files.walk(workspaces)
        .filter(ReleaseTask::isBundleFile)
        .forEach(f -> writeSdkFiles(f, releaseVersion));

    writeSdkConfiguration(workspaces, releaseVersion);

    // Remove the stray logs that is created when the dev environment is launched
    // This is easier than monkey-patching the log4j config just for this one use-case
    if (FileTools.exists("logs")) {
      FileTools.deleteFolder(FileTools.toAbsolute("logs"));
    }

    if (deploy) {
      FileTools.copyAll("jde/dev/release/config/", "tool/sdk_files");
      FileTools.copyAll("jde/dev/release/db/", "tool/db_presets");
      FileTools.copyAll("jde/dev/release/db/", "jde/dbs");
    }
  }

  private void writeSdkFiles(Path path, String version) {
    // do some regex magic to create the sdk files config
    Stream<String> lines =
        ConfigReader.readConfig(path.toFile())
            .map(l -> l.replaceAll(".*\\/plugins\\/", ""))
            .map(l -> l.replaceAll(",-?[0-9],.*$", "@start"));

    File destination =
        FileTools.toAbsolute(String.format("jde/dev/release/config/sdk_files_%s.cfg", version));
    CharSink file = com.google.common.io.Files.asCharSink(destination, StandardCharsets.UTF_8);

    try {
      file.writeLines(lines);
    } catch (IOException e) {
      logger.error("Writing sdk configuration failed: {}", e.getMessage());
    }
  }

  private void writeSdkConfiguration(Path workspaces, String releaseVersion) throws IOException {
    Properties config = readConfig(workspaces);
    String configurator =
        new File(config.getProperty("osgi.bundles")).getName().replaceAll("@.*$", "");
    String osgi = new File(config.getProperty("osgi.framework")).getName();
    String launcher = getLauncherPath(workspaces);
    String path = String.format("jde/dev/release/config/sdk_config_%s.cfg", releaseVersion);
    new SdkConfiguration(osgi, configurator, launcher).toFile(FileTools.toAbsolute(path));
  }

  private static boolean isLaunchFile(Path path) {
    return path.toString().contains("Launch Development Environment");
  }

  private static boolean isBundleFile(Path path) {
    return path.endsWith("bundles.info")
        && path.toString().contains("Launch Development Environment");
  }

  // extract to property reader or something like that
  private Properties readConfig(Path workspaces) throws IOException {
    Path path =
        Files.walk(workspaces)
            .filter(ReleaseTask::isLaunchFile)
            .filter(p -> p.getFileName().endsWith("config.ini"))
            .findFirst()
            .orElseThrow(
                () -> new RuntimeException(String.format("No config.ini found in %s", workspaces)));

    FileInputStream stream = new FileInputStream(path.toFile());
    Properties config = new Properties();
    config.load(stream);
    return config;
  }

  private String getLauncherPath(Path workspaces) throws IOException {
    Path path =
        Files.walk(workspaces)
            .filter(p -> p.getFileName().endsWith("SavedExternalPluginList.txt"))
            .findFirst()
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("SavedExternalPluginList.txt not found in %s", workspaces)));

    String launcherPath =
        FileReader.read(path.toFile())
            .filter(s -> s.contains("org.eclipse.equinox.launcher_"))
            // sort in reverse order to get the newest launcher version
            .max(Comparator.comparing(Object::toString, VersionComparator::compareVersion))
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("No equinox launcher found in %s", workspaces)));

    return new File(launcherPath).getName();
  }
}
