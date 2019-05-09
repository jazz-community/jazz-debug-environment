package org.jazzcommunity.development;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.FileTools;

public class UpgradeTask extends DefaultTask {
  @TaskAction
  public void upgrade() throws Exception {
    // this task has to update the jde folder with all new possible files from the tool folder
    // should practically be the bootstrap task, but without overwriting existing user files.
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/dropins"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/launches"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/launches/configs"));
    FileTools.copyAll("tool/db_presets", "jde/dbs");
    FileTools.copyAll("tool/launches", "jde/dev/launches");
    FileTools.copyAll("tool/launches/configs", "jde/dev/launches/configs");
  }
}
