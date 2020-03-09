package org.jazzcommunity.development;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.FileTools;

public class BootStrapTask extends DefaultTask {
  private static String[] directories = {
    "jde",
    "jde/dbs",
    "jde/dev/dropins",
    "jde/dev/projects",
    "jde/dev/projects/configs",
    "jde/dev/projects/launches",
    "jde/dev/projects/tests",
    "jde/dev/release/config",
    "jde/dev/release/db",
    "jde/logs",
    "jde/runtime",
    "jde/sdks",
    "jde/servers",
    "jde/user",
    "jde/user/log4j",
    "jde/user/workspaces"
  };

  @TaskAction
  public void bootstrap() {
    if (FileTools.exists("jde")) {
      System.out.println("ABORT!");
      System.out.println("Jazz Debug Environment already exists.");
      return;
    }

    FileTools.createDirectories(directories);
    FileTools.copyAll("tool/db_presets", "jde/dbs");
    FileTools.copyAll("tool/projects/launches", "jde/dev/projects/launches");
    FileTools.copyAll("tool/projects/tests", "jde/dev/projects/tests");
    FileTools.copyAll("tool/configs", "jde/dev/projects/configs");
    FileTools.copyAll("tool/log4j", "jde/user/log4j");
    FileTools.copyFile("tool/templates/run_time_parameters.cfg", "jde/user");
    FileTools.copyFile("tool/templates/linux_terminal_emulator.cfg", "jde/user");
    FileTools.copyFile("tool/templates/jts_locations.cfg", "jde/user/");
    FileTools.copyFile("tool/templates/eclipse-proxy.ini", "jde/user/");
    FileTools.copyFile("tool/templates/java_command.cfg", "jde/user/");
    FileTools.copyFile("tool/templates/workspaces_template.cfg", "jde/user/workspaces");
  }
}
