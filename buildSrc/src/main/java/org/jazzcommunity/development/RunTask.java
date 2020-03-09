package org.jazzcommunity.development;

import org.gradle.api.tasks.Exec;
import org.jazzcommunity.development.library.RuntimeDetector;
import org.jazzcommunity.development.library.config.ConfigWriter;

@SuppressWarnings("unchecked")
public class RunTask extends Exec {

  @Override
  protected void exec() {
    RuntimeDetector.get(getProject()).ifPresent(ConfigWriter::prepareConfigurations);
    try {
      super.exec();
    } catch (Exception e) {
      // TODO: Only log this when actually tracing / debugging
      // This is one possible fix for the java task not finding the executable, even though
      // it's actually there. Have to do some extra digging on how to launch properly.
      // e.printStackTrace();
      // The problem seems to revolve around how the executable and parameters are set in the run
      // task. Maybe I can somehow fix this problem with changes to ConfigReader.
    }
  }
}
