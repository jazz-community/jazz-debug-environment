package org.jazzcommunity.development.library;

import java.io.File;

/**
 * Data class representing a run time configuration. This might include other functionality later
 * on, such as including other user configuration.
 *
 * <p>TODO: Improve error handling & logging...
 */
public class Runtime {

  private final File folder;
  private final String version;
  private final File osgi;
  private final File configurator;
  private final File launcher;

  public Runtime(File folder) {
    this.folder = folder;
    this.version = folder.getName();
    // TODO: This is certainly not the best way to deal with these configurations...
    this.osgi =
        new File(String.format("jde/runtime/%s/sdk/plugins", version))
            .listFiles((file, s) -> s.startsWith("org.eclipse.osgi_") && !s.contains("R"))[0];
    this.configurator =
        new File(String.format("jde/runtime/%s/sdk/plugins", version))
            .listFiles(
                (file, s) ->
                    s.startsWith("org.eclipse.equinox.simpleconfigurator_") && !s.contains("R"))[0];
    this.launcher =
        new File(String.format("jde/runtime/%s/sdk/plugins", version))
            .listFiles(
                (file, s) -> s.startsWith("org.eclipse.equinox.launcher_") && !s.contains("R"))[0];
  }

  public File getFolder() {
    return folder;
  }

  public String getVersion() {
    return version;
  }

  public File getOsgi() {
    return osgi;
  }

  public String getOsgiPath() {
    return String.format("sdk/plugins/%s", osgi.getName());
  }

  public String getConfigurator() {
    return configurator.getName();
  }

  public File getLauncher() {
    return launcher;
  }

  public String getLauncherPath() {
    return String.format("sdk/plugins/%s", launcher.getName());
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
        + getOsgiPath()
        + ",\n"
        + "\t\tconfigurator = "
        + getConfigurator()
        + ",\n"
        + "\t\tlauncher = "
        + getLauncherPath()
        + "\n\t}";
  }
}
