package org.jazzcommunity.development;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.FileTools;

public class BootStrapTask extends DefaultTask {
  private static String[] directories = {
    "jde", "jde/user", "jde/runtime", "jde/servers", "jde/sdks", "jde/dbs", "jde/user/workspaces"
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
    FileTools.copyFile("tool/templates/run_time_parameters.cfg", "jde/user");
    FileTools.copyFile("tool/templates/linux_terminal_emulator.cfg", "jde/user");
    FileTools.copyFile("tool/templates/workspaces_template.cfg", "jde/user/workspaces");
  }
}
