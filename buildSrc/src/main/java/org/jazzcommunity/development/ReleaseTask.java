package org.jazzcommunity.development;

import com.google.common.io.CharSink;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.config.ConfigReader;

public class ReleaseTask extends DefaultTask {
  private boolean deploy;

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
    // delete all files except the result.
    FileTools.deleteFolder(FileTools.toAbsolute("jde/dev/initialize"));
    FileTools.deleteFolder(FileTools.toAbsolute("jde/dev/db"));
    FileTools.deleteFolder(FileTools.toAbsolute("jde/dev/logs"));
    FileTools.deleteFolder(FileTools.toAbsolute("jde/projects/tests"));
    FileTools.toAbsolute("jde/projects/configs/scr.xml").delete();
    FileTools.toAbsolute("jde/projects/configs/services.xml").delete();

    if (FileTools.exists("logs")) {
      FileTools.deleteFolder(FileTools.toAbsolute("logs"));
    }

    // TODO: This could definitely do with some cleaning up...
    if (deploy) {
      // copy files to be deployed
      FileTools.copyAll("jde/dev/release/config/", "tool/sdk_files");
      FileTools.copyAll("jde/dev/release/db/", "tool/db_presets");
      FileTools.copyAll("jde/dev/release/db/", "jde/dbs");
      // delete what has just been deployed
      FileTools.deleteFolder(FileTools.toAbsolute("jde/dev/release/config"));
      FileTools.deleteFolder(FileTools.toAbsolute("jde/dev/release/db"));
      // recreate the release goal locations
      FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/release/config"));
      FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/release/db"));
    }
  }

  private static boolean isBundleFile(Path path) {
    return path.endsWith("bundles.info")
        && path.toString().contains("Launch Development Environment");
  }

  private void writeSdkFiles(Path path, String version) {
    // do some regex magic
    // create the sdk files config
    Stream<String> lines =
        ConfigReader.readLines(path.toFile())
            .filter(l -> !l.startsWith("#"))
            .map(l -> l.replaceAll(".*\\/plugins\\/", ""))
            .map(l -> l.replaceAll(",[0-9],.*$", "@start"));

    File destination =
        FileTools.toAbsolute(String.format("jde/dev/release/config/sdk_files_%s.cfg", version));
    CharSink file = com.google.common.io.Files.asCharSink(destination, Charset.forName("UTF-8"));

    try {
      file.writeLines(lines);
    } catch (IOException e) {
      System.out.println(String.format("Writing sdk configuration failed: %s", e.getMessage()));
    }
  }
}
