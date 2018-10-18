package org.jazzcommunity.development;

import org.gradle.api.tasks.Exec;
import org.jazzcommunity.development.library.RuntimeDetector;
import org.jazzcommunity.development.library.config.ConfigWriter;

@SuppressWarnings("unchecked")
public class RunTask extends Exec {

  @Override
  protected void exec() {
    RuntimeDetector.get(getProject()).ifPresent(ConfigWriter::prepareConfigurations);
    super.exec();
  }
}
