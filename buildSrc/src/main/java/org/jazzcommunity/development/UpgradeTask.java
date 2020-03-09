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
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/projects"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/projects/configs"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/release"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/release/db"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/release/config"));
    FileTools.copyAll("tool/db_presets", "jde/dbs");
    FileTools.copyAll("tool/projects/launches", "jde/projects/launches");
    FileTools.copyAll("tool/configs", "jde/dev/projects/configs");

    if (!FileTools.exists("tool/user/eclipse-proxy.ini")) {
      FileTools.copyFile("tool/templates/eclipse-proxy.ini", "jde/user");
    }

    if (!FileTools.exists("tool/user/java_command.cfg")) {
      FileTools.copyFile("tool/templates/java_command.cfg", "jde/user");
    }
  }
}
