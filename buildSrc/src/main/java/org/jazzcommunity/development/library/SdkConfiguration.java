package org.jazzcommunity.development.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdkConfiguration {
  private static final Logger logger = LoggerFactory.getLogger("SdkConfiguration");
  private final String osgi;
  private final String configurator;
  private final String launcher;

  public SdkConfiguration(String osgi, String configurator, String launcher) {
    this.osgi = osgi;
    this.configurator = configurator;
    this.launcher = launcher;
  }

  public String getOsgi() {
    return osgi;
  }

  public String getConfigurator() {
    return configurator;
  }

  public String getLauncher() {
    return launcher;
  }

  public static SdkConfiguration fromFile(File file) {
    try {
      Properties config = new Properties();
      FileInputStream stream = new FileInputStream(file);
      config.load(stream);
      String osgi = config.getProperty("osgi");
      String configurator = config.getProperty("configurator");
      String launcher = config.getProperty("launcher");
      return new SdkConfiguration(osgi, configurator, launcher);
    } catch (IOException e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      logger.error("Failed to locate sdk configuration at {}", file);
      // return null object
      return new SdkConfiguration("", "", "");
    }
  }

  public void toFile(File destination) throws IOException {
    Properties config = new Properties();
    config.setProperty("osgi", this.osgi);
    config.setProperty("configurator", this.configurator);
    config.setProperty("launcher", this.launcher);
    FileOutputStream out = new FileOutputStream(destination);
    config.store(out, "Sdk Configuration locations");
  }
}
