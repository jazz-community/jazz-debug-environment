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
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.config.ConfigReader;

public class FinalizeTask extends DefaultTask {
  @TaskAction
  public void finalizeRuntime() throws Exception {
    Path workspaces =
        Paths.get(String.format("%s/.goomph/ide-workspaces", System.getProperty("user.home")));

    Files.walk(workspaces).filter(FinalizeTask::isBundleFile).forEach(FinalizeTask::writeSdkFiles);

    // delete all files except the result.
    FileTools.deleteFolder(FileTools.toAbsolute("jde/dev/initialize"));
    FileTools.deleteFolder(FileTools.toAbsolute("jde/dev/db"));
  }

  private static boolean isBundleFile(Path path) {
    return path.endsWith("bundles.info")
        && path.toString().contains("Launch Development Environment");
  }

  private static void writeSdkFiles(Path path) {
    // do some regex magic
    // create the sdk files config
    Stream<String> lines =
        ConfigReader.readLines(path.toFile())
            .filter(l -> !l.startsWith("#"))
            .map(l -> l.replaceAll(".*\\/plugins\\/", ""))
            .map(l -> l.replaceAll(",[0-9],.*$", "@start"));

    String newestVersion = FileTools.newestVersion("jde/sdks");
    File destination =
        FileTools.toAbsolute(String.format("jde/dev/sdk_files_%s.cfg", newestVersion));
    CharSink file = com.google.common.io.Files.asCharSink(destination, Charset.forName("UTF-8"));

    try {
      file.writeLines(lines);
    } catch (IOException e) {
      System.out.println(String.format("Writing sdk configuration failed: %s", e.getMessage()));
    }
  }
}
