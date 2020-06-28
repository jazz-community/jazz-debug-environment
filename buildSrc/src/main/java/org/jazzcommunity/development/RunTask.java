package org.jazzcommunity.development;

import org.gradle.api.tasks.Exec;
import org.jazzcommunity.development.library.RuntimeDetector;
import org.jazzcommunity.development.library.config.ConfigWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTask extends Exec {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  protected void exec() {
    RuntimeDetector.get(getProject()).ifPresent(ConfigWriter::prepareConfigurations);
    try {
      super.exec();
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }

      logger.info(
          "This error is probably a false-positive, and occurs during task resolution when the run task is initialized: {}",
          e.getMessage());
    }
  }
}
