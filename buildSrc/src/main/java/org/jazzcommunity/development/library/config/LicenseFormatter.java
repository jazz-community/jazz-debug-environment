package org.jazzcommunity.development.library.config;

import java.net.URISyntaxException;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.net.UrlProvider;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class LicenseFormatter {
  private LicenseFormatter() {}

  public static String formatText(UrlProvider provider) throws URISyntaxException {
    JtwigTemplate template =
        JtwigTemplate.fileTemplate(FileTools.toAbsolute("tool/templates/downloads/license.twig"));

    JtwigModel model =
        JtwigModel.newModel()
            .with("sdkLicenseUrl", provider.getSdkLicenseUrl())
            .with("serverLicenseUrl", provider.getServerLicenseUrl())
            .with("sdkUrl", provider.getSdkUrl())
            .with("serverUrl", provider.getServerUrl());
    return template.render(model);
  }
}
