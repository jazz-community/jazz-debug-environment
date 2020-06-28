package org.jazzcommunity.development;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.FileTools;

public class UpgradeTask extends DefaultTask {
  @TaskAction
  public void upgrade() {
    // this task has to update the jde folder with all new possible files from the tool folder
    // should practically be the bootstrap task, but without overwriting existing user files.
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/projects"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/projects/launches"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dev/projects/tests"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/dropins"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/integration"));
    FileTools.makeDirectory(FileTools.toAbsolute("jde/integration/setup"));
    FileTools.copyAll("tool/db_presets", "jde/dbs");
    FileTools.copyAll("tool/projects/launches", "jde/projects/launches");
    FileTools.copyAll("tool/integration", "jde/integration/setup");

    if (!FileTools.exists("jde/user/eclipse-proxy.ini")) {
      FileTools.copyFile("tool/templates/eclipse-proxy.ini", "jde/user");
    }

    if (!FileTools.exists("jde/user/java_command.cfg")) {
      FileTools.copyFile("tool/templates/java_command.cfg", "jde/user");
    }

    if (!FileTools.exists("jde/user/downloads")) {
      FileTools.makeDirectory(FileTools.toAbsolute("jde/user/downloads"));
      FileTools.copyAll("tool/templates/downloads", "jde/user/downloads");
    }

    if (!FileTools.exists("jde/user/log4j")) {
      FileTools.makeDirectory(FileTools.toAbsolute("jde/user/log4j"));
      FileTools.copyAll("tool/log4j", "jde/user/log4j");
    }

    if (!FileTools.exists("jde/user/windows_terminal_emulator.cfg")) {
      FileTools.copyFile("tool/templates/windows_terminal_emulator.cfg", "jde/user");
    }

    if (!FileTools.exists("jde/user/jts_locations.cfg")) {
      FileTools.copyFile("tool/templates/jts_locations.cfg", "jde/user");
    }
  }
}
