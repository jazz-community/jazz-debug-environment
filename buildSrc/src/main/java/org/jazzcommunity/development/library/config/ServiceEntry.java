package org.jazzcommunity.development.library.config;

import com.google.common.io.CharSource;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jazzcommunity.development.library.DetectOperatingSystem;

class ServiceEntry implements IniEntry {

  private final File directory;

  public ServiceEntry(File directory) {
    this.directory = directory;
  }

  @Override
  public String getIniLine() {
    // TODO: duplicated code
    String formatted = directory.getPath();

    if (DetectOperatingSystem.isWindows()) {
      formatted = formatted.replaceAll("\\\\", "/");
      formatted = formatted.replaceAll(":", "\\\\:");
    }

    return String.format("reference\\:file\\:%s/plugin@start", formatted);
  }

  @Override
  public String getPropertiesLine() {
    File manifest = new File(directory, "plugin/META-INF/MANIFEST.MF");
    return read(manifest)
        .filter(l -> l.startsWith("Bundle-SymbolicName"))
        .findFirst()
        .map(line -> String.format("%s=target/classes,target/dependency", toSymbolicName(line)))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format(
                        "Missing Bundle-SymbolicName in %s. Malformed manifest", directory)));
  }

  @Override
  public boolean needsPropertyEntry() {
    return true;
  }

  // TODO: duplicate code, this is pretty much the same as in config reader
  private Stream<String> read(File manifest) {
    CharSource source = Files.asCharSource(manifest, Charset.forName("UTF-8"));

    try {
      return source.lines();
    } catch (IOException e) {
      System.out.println(
          String.format(
              "Manifest file %s not found. Cannot load %s as jazz plugin.", manifest, directory));
      return Stream.empty();
    }
  }

  @Override
  public String toString() {
    return "\tServiceEntry {\n"
        + "\t\tdirectory = "
        + directory
        + "\n\t\tini line = "
        + getIniLine()
        + "\n\t\tproperties line = "
        + getPropertiesLine()
        + "\n\t}";
  }

  private static String toSymbolicName(String line) {
    Pattern pattern = Pattern.compile("Bundle-SymbolicName:(.*);.*");
    Matcher matcher = pattern.matcher(line);
    matcher.find();
    return matcher.group(1).trim();
  }
}
