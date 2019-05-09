package org.jazzcommunity.development.library.config;

import com.google.common.io.CharSource;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jazzcommunity.development.library.FileTools;

public class ConfigReader {
  public ConfigReader() {}

  public static String[] terminalEmulator() {
    CharSource charSource =
        Files.asCharSource(
            FileTools.toAbsolute("jde/user/linux_terminal_emulator.cfg"), Charset.forName("UTF-8"));

    return filter(read(charSource))
        .findFirst()
        .map(s -> s.trim())
        .map(s -> s.split(" "))
        .orElseGet(() -> new String[] {"gnome-terminal", "--", "./run_jetty.sh"});
  }

  /**
   * This returns a list because we need to have a possible null-check when writing to the jtwig
   * templates. Otherwise, this method would be a lot cleaner.
   *
   * <p>TODO: Move empty check to template
   */
  public static List<String> runtimeParameters() {
    CharSource source =
        Files.asCharSource(
            FileTools.toAbsolute("jde/user/run_time_parameters.cfg"), Charset.forName("UTF-8"));

    List<String> lines = filter(read(source)).map(String::trim).collect(Collectors.toList());

    if (lines.isEmpty()) {
      return null;
    }

    return lines;
  }

  public static Stream<IniEntry> userConfiguration() {
    File[] files = FileTools.getFiles("jde/user/workspaces");
    return Arrays.stream(files)
        .map(f -> Files.asCharSource(f, Charset.forName("UTF-8")))
        .map(ConfigReader::read)
        .map(ConfigReader::filter)
        .flatMap(Function.identity())
        .map(File::new)
        .map(IniEntry::getEntry);
  }

  public static Stream<String> sdkFiles(String version) {
    CharSource source =
        Files.asCharSource(
            FileTools.toAbsolute(String.format("tool/sdk_files/sdk_files_%s.cfg", version)),
            Charset.forName("UTF-8"));
    return read(source);
  }

  public static Stream<String> readLines(File path) {
    if (!path.exists()) {
      System.out.println(String.format("File %s does not exist", path));
    }

    CharSource source = Files.asCharSource(path, Charset.forName("UTF-8"));
    return read(source);
  }

  // TODO: duplicate code, same as in service entry. Extract both functions to something
  // TODO: like "readVerboseError". Could be combined with the call to charsource
  private static Stream<String> read(CharSource source) {
    try {
      return source.lines();
    } catch (IOException e) {
      System.out.println(
          String.format("Error occurred reading configuration files: %s", e.getMessage()));
      return Stream.empty();
    }
  }

  // TODO: Make this a predicate for chaining the stream instead of passing the stream
  private static Stream<String> filter(Stream<String> in) {
    return in.filter(l -> !l.startsWith("#")).filter(l -> !l.isEmpty());
  }
}
