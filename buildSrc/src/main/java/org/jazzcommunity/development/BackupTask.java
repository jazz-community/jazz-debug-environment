package org.jazzcommunity.development;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.zip.SevenZip;

public class BackupTask extends DefaultTask {
  private String mode;

  private static String[] SLIM_FILTER = {"sdks", "servers", "sdk", "jre"};

  /**
   * Create a zip file of the complete jde folder, either for backing up or giving to someone else
   */
  @TaskAction
  public void backup() throws IOException {
    if (!FileTools.exists("backup")) {
      FileTools.createDirectories(new String[] {"backup"});
    }

    String filename =
        String.format(
            "backup/backup_%s_%s_%s.7z",
            System.getProperty("os.name"),
            System.getProperty("user.name"),
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));

    switch (mode) {
      case "full":
        System.out.println("Doing full back up");
        SevenZip.compress(filename, FileTools.toAbsolute("jde"));
        break;
      case "slim":
        System.out.println("Doing slim back up");
        SevenZip.compress(filename, SLIM_FILTER, FileTools.toAbsolute("jde"));
        break;
      case "user":
        System.out.print("Backing up user files");
        SevenZip.compress(filename, FileTools.toAbsolute("jde/user"));
        break;
      default:
        System.out.println("No backup option selected, aborting.");
        break;
    }
  }

  @Input
  public String getMode() {
    return mode;
  }

  @Option(option = "mode", description = "Sets which backup mode to use.")
  public void setMode(String mode) {
    this.mode = mode;
  }
}
