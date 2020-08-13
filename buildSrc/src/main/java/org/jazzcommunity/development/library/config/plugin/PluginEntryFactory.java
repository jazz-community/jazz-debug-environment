package org.jazzcommunity.development.library.config.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jazzcommunity.development.library.config.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginEntryFactory {
  private static final Logger logger = LoggerFactory.getLogger("PluginEntryFactory");

  private PluginEntryFactory() {}

  private static boolean isSubModule(String path, File directory) {
    try {
      return directory.getCanonicalPath().contains(path)
          && directory.getCanonicalPath().length() > path.length();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static IniEntry getEntry(File directory) {
    File[] files = directory.listFiles();

    if (!directory.isDirectory() || files == null) {
      return new InvalidEntry(directory);
    }

    List<String> parentCandidates =
        ConfigReader.flattenConfigs("jde/user/workspaces")
            .map(entry -> entry + File.separator)
            .filter(path -> isSubModule(path, directory))
            .collect(Collectors.toList());

    // find out if this plugin is a "child" of another one
    if (parentCandidates.size() > 0) {
      // show warning for possibly wrong folder hierarchy
      if (parentCandidates.size() > 1) {
        logger.warn(
            "More than one possible parent project found for plugin in '{}'. This is possibly due to a wrong folder hierarchy in plugin projects.",
            directory);
      }
      String parent = parentCandidates.get(0);
      return new SubPluginEntry(directory, parent);
    }

    if (Arrays.stream(files).anyMatch(f -> f.getName().equals("META-INF"))) {
      return new UiEntry(directory);
    }

    if (Arrays.stream(files).anyMatch(f -> f.getName().equals("plugin"))) {
      return new ServiceEntry(directory);
    }

    logger.error(
        "The plugin in '{}' seems to not conform to a supported plugin setup. Closely follow the plugin setup instructions. If the problem persists, please open an issue on github.",
        directory);
    throw new RuntimeException("Invalid file configuration. Fatal.");
  }
}
