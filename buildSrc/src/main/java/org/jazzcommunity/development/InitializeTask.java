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
    FileTools.copyAll("jde/dev/dropins", "build/oomph-ide/dropins");
  }
}
