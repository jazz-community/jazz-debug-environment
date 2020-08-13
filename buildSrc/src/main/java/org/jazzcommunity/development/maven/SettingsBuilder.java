package org.jazzcommunity.development.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.jazzcommunity.development.gen.ObjectFactory;
import org.jazzcommunity.development.gen.Profile;
import org.jazzcommunity.development.gen.Repository;
import org.jazzcommunity.development.gen.Settings;
import org.jazzcommunity.development.gen.Settings.ActiveProfiles;
import org.jazzcommunity.development.gen.Settings.Profiles;
import org.jazzcommunity.development.library.FileTools;

/**
 * Builder class for creating new maven settings files, or working with existing files, changing
 * only required settings.
 *
 * <p>This does some whacky state management in the current implementation, so use with extra care.
 * If you get weird errors, it's probably going wrong in here somewhere.
 */
public class SettingsBuilder {

  private final ObjectFactory factory;
  private final JAXBElement<Settings> settings;
  private Profile currentProfile;

  public SettingsBuilder() {
    factory = new ObjectFactory();
    settings = factory.createSettings(new Settings());
  }

  public SettingsBuilder(File m2) throws JAXBException, FileNotFoundException, XMLStreamException {
    XMLEventReader reader =
        XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(m2));
    JAXBContext context = JAXBContext.newInstance(Settings.class);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    JAXBElement<Settings> payload = unmarshaller.unmarshal(reader, Settings.class);
    factory = new ObjectFactory();
    settings = factory.createSettings(payload.getValue());
  }

  // This kind of breaks encapsulation, but I'm just going to keep this for a bit
  public SettingsBuilder addAllRepositories() {
    Settings value = settings.getValue();

    for (File repo : FileTools.getFiles("jde/p2repo")) {
      this.addProfile(FileTools.folderVersion(repo.getAbsolutePath())).addRepository(repo);
    }

    settings.setValue(value);
    return this;
  }

  public SettingsBuilder addProfiles() {
    Settings value = settings.getValue();
    value.setProfiles(factory.createSettingsProfiles());
    settings.setValue(value);
    return this;
  }

  public SettingsBuilder addProfile(String version) {
    Settings value = settings.getValue();
    currentProfile = factory.createProfile();
    String id = String.format("jde-sdk-%s", version);
    currentProfile.setId(id);
    value.getProfiles().getProfile().add(currentProfile);
    currentProfile.setRepositories(factory.createProfileRepositories());
    settings.setValue(value);
    return this;
  }

  public SettingsBuilder addRepository(File repo) {
    Settings value = settings.getValue();
    Repository repository = factory.createRepository();
    repository.setId(String.format("repo-%s", currentProfile.getId()));
    repository.setLayout("p2");
    repository.setUrl(String.format("file:%s", repo.getAbsolutePath()));
    currentProfile.getRepositories().getRepository().add(repository);
    settings.setValue(value);
    return this;
  }

  /**
   * Removes all profiles associated with the jazz development environment
   *
   * @return Current builder instance
   */
  public SettingsBuilder clearProfiles() {
    Settings value = settings.getValue();
    settings.setValue(value);
    Profiles filtered = factory.createSettingsProfiles();
    for (Profile profile : value.getProfiles().getProfile()) {
      if (!profile.getId().startsWith("jde")) {
        filtered.getProfile().add(profile);
      }
    }
    value.setProfiles(filtered);
    return this;
  }

  /**
   * Just set the newest one active at this point...
   *
   * @return Current Builder instance
   */
  public SettingsBuilder setActive(String version) {
    Settings value = settings.getValue();

    if (value.getActiveProfiles() == null) {
      value.setActiveProfiles(factory.createSettingsActiveProfiles());
    }

    // get the profile that matches the supplied version
    Profile profile =
        value
            .getProfiles()
            .getProfile()
            .stream()
            .filter(p -> p.getId().endsWith(version))
            .findFirst()
            .get();

    // This removes all active jde entries so that a fresh one can be added. Other profiles not
    // managed by jde are taken into account and ignored
    List<String> filtered =
        value
            .getActiveProfiles()
            .getActiveProfile()
            .stream()
            .filter(p -> !p.startsWith("jde"))
            .distinct()
            .collect(Collectors.toList());

    ActiveProfiles active = value.getActiveProfiles();
    active.getActiveProfile().clear();
    active.getActiveProfile().addAll(filtered);
    active.getActiveProfile().add(profile.getId());
    value.setActiveProfiles(active);
    settings.setValue(value);
    return this;
  }

  public void marshal(OutputStream output) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(Settings.class);
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.marshal(settings, output);
  }
}
