package org.jazzcommunity.development.ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent.EventType;
import org.jazzcommunity.development.library.config.LicenseFormatter;
import org.jazzcommunity.development.library.net.Credentials;
import org.jazzcommunity.development.library.net.UrlProvider;

public class CredentialDialog extends JDialog {

  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      // Intentionally left blank for now, the only negative effect when this occurs is ugly styling
      // of the dialog. Functionality will remain the same.
    }
  }

  // container panels
  private final JScrollPane licensePane = new JScrollPane();
  private final JPanel acceptPanel = new JPanel();
  private final JPanel credentialsPanel = new JPanel();
  private final JPanel buttonPanel = new JPanel();

  // license information text
  private final JTextPane licenseText = new JTextPane();

  // license accepting
  private final JCheckBox licenseCheckBox = new JCheckBox();
  private final JLabel licenseLabel = new JLabel("I agree to the terms of both licenses.");

  // input fields
  private final JLabel userLabel = new JLabel("Username:");
  private final JLabel pwLabel = new JLabel("Password:");
  private final JTextField userField = new JTextField(10);
  private final JPasswordField pwField = new JPasswordField(10);

  // buttons
  private final JButton downloadButton = new JButton("Download");
  private final JButton cancelButton = new JButton("Cancel");

  public CredentialDialog(UrlProvider provider) throws HeadlessException, URISyntaxException {
    setAlwaysOnTop(true);
    setModalityType(ModalityType.TOOLKIT_MODAL);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setResizable(false);

    setTitle("Jazz.net User Credentials");
    setPreferredSize(new Dimension(870, 450));
    // Use box layout to put each panel in a single column, row by row
    getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

    // add all panels in the desired order
    getContentPane().add(licensePane);
    getContentPane().add(acceptPanel);
    getContentPane().add(credentialsPanel);
    getContentPane().add(buttonPanel);

    // scrollable license text field
    licensePane.setViewportView(licenseText);
    licensePane.setPreferredSize(new Dimension(870, 450));
    licenseText.setEditable(false);
    licenseText.setContentType("text/html");
    licenseText.setText(LicenseFormatter.formatText(provider));
    // make urls clickable
    licenseText.addHyperlinkListener(
        e -> {
          if (e.getEventType() == EventType.ACTIVATED) {
            try {
              Desktop.getDesktop().browse(e.getURL().toURI());
            } catch (IOException | URISyntaxException ex) {
              ex.printStackTrace();
            }
          }
        });

    // panel for accepting the license
    acceptPanel.add(licenseCheckBox);
    acceptPanel.add(licenseLabel);

    // panel with credential input
    credentialsPanel.add(userLabel);
    credentialsPanel.add(userField);
    credentialsPanel.add(pwLabel);
    credentialsPanel.add(pwField);

    // buttons
    buttonPanel.add(downloadButton);
    buttonPanel.add(cancelButton);

    // set mnemonics for easier UI navigation
    downloadButton.setMnemonic('D');
    cancelButton.setMnemonic('C');
    licenseLabel.setDisplayedMnemonic('a');
    licenseCheckBox.setMnemonic('A');

    // set default button (action called when enter is pressed with focus on window)
    JRootPane rootPane = SwingUtilities.getRootPane(downloadButton);
    rootPane.setDefaultButton(downloadButton);

    // this needs to be called last, because... well, because magic
    pack();
    // set user name field as focused component (so that you can start typing right away). Has to be
    // called after pack because the window needs to have been materialized at this point.
    userField.requestFocusInWindow();
    // center on screen
    setLocationRelativeTo(null);
  }

  // this will have to return the entire connection information
  public Credentials getCredentials() {
    return new Credentials(userField.getText(), pwField.getPassword());
  }

  public boolean licenseAccepted() {
    return licenseCheckBox.isSelected();
  }

  public void onDownloadPressed(ActionListener listener) {
    downloadButton.addActionListener(listener);
  }

  public void onCancelPressed(ActionListener listener) {
    cancelButton.addActionListener(listener);
  }
}
