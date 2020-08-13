package org.jazzcommunity.development.library;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runtime {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final File folder;
  private final String version;
  private final SdkConfiguration configuration;

  public Runtime(File folder) {
    this.folder = folder;
    this.version = folder.getName();
    String path = String.format("tool/sdk_files/sdk_config_%s.cfg", version);
    configuration = SdkConfiguration.fromFile(new File(path));
  }

  public String getVersion() {
    return version;
  }

  public String getOsgi() {
    return configuration.getOsgi();
  }

  public String getConfigurator() {
    return configuration.getConfigurator();
  }

  public String getLauncherPath() {
    return String.format("sdk/plugins/%s", configuration.getLauncher());
  }

  @Override
  public String toString() {
    return "\tRuntime {\n"
        + "\t\tversion = "
        + version
        + ",\n"
        + "\t\tfolder = "
        + folder
        + ",\n"
        + "\t\tosgi = "
        + configuration.getOsgi()
        + ",\n"
        + "\t\tconfigurator = "
        + getConfigurator()
        + ",\n"
        + "\t\tlauncher = "
        + getLauncherPath()
        + "\n\t}";
  }
}
