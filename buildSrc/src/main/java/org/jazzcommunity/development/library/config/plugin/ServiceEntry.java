package org.jazzcommunity.development.library.config.plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jazzcommunity.development.library.DetectOperatingSystem;
import org.jazzcommunity.development.library.file.FileReader;
import org.jazzcommunity.development.library.file.FileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServiceEntry implements IniEntry {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
    return FileReader.read(manifest)
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

  @Override
  public Stream<Path> getZip() {
    Path path = Paths.get(directory.getAbsolutePath(), "/update-site/target");
    return FileResolver.findInDirectory(path, ".zip");
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
