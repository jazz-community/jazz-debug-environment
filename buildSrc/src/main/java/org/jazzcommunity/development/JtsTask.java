package org.jazzcommunity.development;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jazzcommunity.development.library.DetectOperatingSystem;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.file.FileReader;
import org.jazzcommunity.development.library.file.FileWriter;
import org.jazzcommunity.development.library.zip.Zip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JtsTask extends DefaultTask {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private String server;

  @TaskAction
  public void setupJts() throws IOException, InterruptedException {
    String version = server.isEmpty() ? FileTools.newestVersion("jde/servers") : server;
    System.out.println(String.format("Server Version %s selected for JTS setup", version));

    String location = String.format("jde/integration/%s", version);

    // this will also have to check the locations file
    if (FileTools.exists(location)) {
      logger.error("Server for {} already setup. Doing nothing", version);
      return;
    }

    // first, unzip the correct version
    Zip.extract(FileTools.byVersion("jde/servers", version), FileTools.toAbsolute(location));

    // if linux, we have to make everything executable and set ulimit
    if (DetectOperatingSystem.isLinux()) {
      // set x flag
      ProcessBuilder chmod = new ProcessBuilder("chmod", "-R", "+x", location);
      Process chmodProc = chmod.start();
      watch(chmodProc);
      // write ulimit -n 4096 to server.startup.sh (I don't know since when this is necessary,
      // but we have to consider it now)
      writeUlimit(location);
    }

    if (DetectOperatingSystem.isWindows()) {
      // find a clean way to wait for the several set up steps in the liberty batch file
      File liberty = FileTools.toAbsolute(location + "/server/liberty.server.bat");
      // for a reason I can't really explain, making xcopy output verbose file src/dst seems to
      // actually do the trick.
      List<String> output =
          FileReader.read(liberty).map(this::verboseXcopy).collect(Collectors.toList());
      // we have to convert to list and back so that the stream is consumed before we overwrite the
      // actual source file
      FileWriter.writeFile(liberty, output.stream());
    }

    // then skip the entire setup up stuff with responses files
    // this part is going to be fairly tricky, and also OS dependant
    // It seems that paths have to be absolute for this to work. Which is too bad, because that
    // will be a whole lot of extra work
    if (DetectOperatingSystem.isLinux()) {
      File serverLocation = FileTools.toAbsolute(location + "/server");
      ProcessBuilder start = new ProcessBuilder("./server.startup");
      start.directory(serverLocation);
      start.inheritIO();
      watch(start.start());

      ProcessBuilder jts =
          new ProcessBuilder(
              "./repotools-jts.sh", "-setup", "parametersFile=../../setup/responses-jts");
      jts.directory(serverLocation);
      watch(jts.start());

      ProcessBuilder ccm =
          new ProcessBuilder(
              "./repotools-ccm.sh",
              "-setup",
              "parametersFile=../../setup/responses-ccm",
              "includeLifecycleProjectStep=true");
      ccm.directory(serverLocation);
      watch(ccm.start());

      ProcessBuilder stop = new ProcessBuilder("./server.shutdown");
      stop.directory(serverLocation);
      watch(stop.start());
    }

    // so this is super ugly and just copy paste, but all I have to do is make the executable names
    // somewhat generic
    if (DetectOperatingSystem.isWindows()) {
      File serverLocation = FileTools.toAbsolute(location + "/server");
      // executable resolution is different in windows. Setting the directory is not enough.
      ProcessBuilder start = new ProcessBuilder(serverLocation + "/server.startup.bat");
      start.directory(serverLocation);
      start.inheritIO();
      watch(start.start());

      ProcessBuilder jts =
          new ProcessBuilder(
              serverLocation + "/repotools-jts.bat",
              "-setup",
              "parametersFile=../../setup/responses-jts");
      jts.directory(serverLocation);
      watch(jts.start());

      ProcessBuilder ccm =
          new ProcessBuilder(
              serverLocation + "/repotools-ccm.bat",
              "-setup",
              "parametersFile=../../setup/responses-ccm",
              "includeLifecycleProjectStep=true");
      ccm.directory(serverLocation);
      watch(ccm.start());

      ProcessBuilder stop = new ProcessBuilder(serverLocation + "/server.shutdown.bat");
      stop.directory(serverLocation);
      watch(stop.start());
    }

    // write unpacked server into locations file
    File locations = FileTools.toAbsolute("jde/user/jts_locations.cfg");
    FileWriter.appendFile(locations, FileTools.toAbsolute(location).getAbsolutePath());
  }

  @Option(
      option = "server",
      description = "Which version to set up a jts server for. Default is latest.")
  public void setServer(String server) {
    this.server = server;
  }

  private void writeUlimit(String location) throws IOException {
    File startup = FileTools.toAbsolute(location + "/server/server.startup");
    List<String> lines = FileReader.raw(startup).collect(Collectors.toList());
    lines.add(1, "ulimit -n 4096");
    FileWriter.writeFile(startup, lines.stream());
  }

  // output stdout of forked process
  private void watch(Process process) throws InterruptedException {
    new Thread(
            () -> {
              BufferedReader input =
                  new BufferedReader(new InputStreamReader(process.getInputStream()));
              try {
                for (String line; (line = input.readLine()) != null; ) {
                  // always print this to show server setup information
                  System.out.println(line);
                }
              } catch (Exception e) {
                logger.error(e.getMessage());
              }
            })
        .start();
    // we need to block this execution, because everything has to happen in order and not in
    // parallel
    process.waitFor();
  }

  private String verboseXcopy(String line) {
    return line.replace("xcopy", "xcopy /f");
  }
}
