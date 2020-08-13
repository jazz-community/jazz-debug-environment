package org.jazzcommunity.development.library.config;

import com.google.common.io.CharSource;
import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jazzcommunity.development.library.DetectOperatingSystem;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.config.plugin.IniEntry;
import org.jazzcommunity.development.library.config.plugin.PluginEntryFactory;
import org.jazzcommunity.development.library.file.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {
  private static final Logger logger = LoggerFactory.getLogger("ConfigReader");

  public ConfigReader() {}

  public static String[] windowsTerminal() {
    return readConfig(FileTools.toAbsolute("jde/user/windows_terminal_emulator.cfg"))
        .findFirst()
        .map(s -> s.split(" "))
        .orElseGet(() -> new String[] {"cmd", "/K", "start", "powershell", "./run_jetty.ps1"});
  }

  public static String[] linuxTerminal() {
    return readConfig(FileTools.toAbsolute("jde/user/linux_terminal_emulator.cfg"))
        .findFirst()
        .map(s -> s.split(" "))
        .orElseGet(() -> new String[] {"gnome-terminal", "--", "./run_jetty.sh"});
  }

  public static String javaPath() {
    return readConfig(FileTools.toAbsolute("jde/user/java_command.cfg"))
        .findFirst()
        .orElseGet(ConfigReader::defaultJava);
  }

  /**
   * This returns a list because we need to have a possible null-check when writing to the jtwig
   * templates. Otherwise, this method would be a lot cleaner.
   *
   * <p>TODO: Move empty check to template
   */
  public static List<String> runtimeParameters() {
    List<String> lines =
        readConfig(FileTools.toAbsolute("jde/user/run_time_parameters.cfg"))
            .collect(Collectors.toList());

    return !lines.isEmpty() ? lines : null;
  }

  public static Stream<String> flattenConfigs(String directory) {
    return flattenConfigs(FileTools.toAbsolute(directory));
  }

  public static Stream<String> flattenConfigs(File directory) {
    File[] files = directory.listFiles();
    return Arrays.stream(files)
        .map(f -> Files.asCharSource(f, StandardCharsets.UTF_8))
        .flatMap(FileReader::read)
        .filter(ConfigReader::isConfigLine);
  }

  public static Stream<IniEntry> userConfiguration() {
    return flattenConfigs(FileTools.toAbsolute("jde/user/workspaces"))
        .filter(ConfigReader::isConfigLine)
        .map(File::new)
        .map(PluginEntryFactory::getEntry);
  }

  public static Stream<String> sdkFiles(String version) {
    return FileReader.read(
        FileTools.toAbsolute(String.format("tool/sdk_files/sdk_files_%s.cfg", version)));
  }

  public static Stream<String> readConfig(File path) {
    if (!path.exists()) {
      logger.error("File {} does not exist", path);
    }

    CharSource source = Files.asCharSource(path, StandardCharsets.UTF_8);
    return FileReader.read(source).filter(ConfigReader::isConfigLine);
  }

  private static String defaultJava() {
    if (DetectOperatingSystem.isWindows()) {
      return "jre\\bin\\java.exe";
    }

    if (DetectOperatingSystem.isLinux()) {
      return "jre/bin/java";
    }

    throw new RuntimeException("Unknown operating system");
  }

  private static boolean isConfigLine(String in) {
    return !in.trim().startsWith("#") && !in.trim().isEmpty();
  }
}
