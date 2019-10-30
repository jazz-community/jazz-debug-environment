package org.jazzcommunity.development;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.zip.Zip;

public class InitializeTask extends DefaultTask {

  @TaskAction
  public void initializeNewVersion() throws Exception {
    // Extract the newest sdk
    // TODO: Make this configurable like other tasks
    String version = FileTools.newestVersion("jde/sdks");
    Zip.extract(
        FileTools.byVersion("jde/sdks", version),
        FileTools.toAbsolute(String.format("jde/dev/initialize/%s/sdk", version)));

    // 2. Deploy the content of the dropins folder
    // TODO: Document where to find the necessary jar
    // https://jazz.net/wiki/bin/view/Main/FeatureBasedLaunches
    FileTools.copyAll("jde/dev/dropins", "build/oomph-ide/dropins");

    // 3. If not done yet, extract the configs from the server distribution
    String scr = "jde/dev/launches/configs/scr.xml";
    String services = "jde/dev/launches/configs/services.xml";
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

    // 4. Copy the custom log4j configuration file
    FileTools.copyFile("jde/user/log4j/log4j.properties", "jde/dev/launches/configs/");
  }
}
