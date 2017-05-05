
Jazz Debug Environment
======================

The goal of this project is to provide you with a ready-to-go environment for extending your jazz applications.

Prerequisites
-------------
 - Rational Team Concert Server SDK, download from [Jazz.net](https://jazz.net/downloads/rational-team-concert/) (make sure that you choose the right version)
 - IBM JRE (Java Runtime Environment by IBM)

Installation
------------
 1. Click the 'Clone or download' button on GitHub, then do one of the following:
  - Download & extract the ZIP file to your desired location
  - Copy the clone URL, navigate to the desired location on your machine and use `git clone https://github.com/jazz-community/jazz-debug-environment.git` to get this repos content
 4. Extract the RTC Server SDK into the 'sdk' folder.
 5. Extract/Copy the IBM JRE into the 'jre' folder.
 6. Extract one of the database presets which are available within the 'db_presets' folder into the 'db' folder
 7. Verify that your folder structure looks like the following:

```
├── jazz-debug-environment/
│   ├── conf/
│   ├── db_presets/
│   │   ├── ....
│   ├── db/
│   │   ├── conf/
│   ├── jre/
│   │   ├── bin/
│   │   ├── docs/
│   │   ├── lib/
│   │   ├── properties/
│   │   ├── ....
│   ├── sdk/
│   │   ├── features/
│   │   ├── license/
│   │   ├── plugins/
```

Start the Server
----------------

 - If you have changed your configuration, or running this for the first time, do the following:
	 - run the `generate_jetty_config.ps1` script which can be found in the 'conf/jetty' folder
 - Start the server by running `click_launch.ps1`
 - Open [https://localhost:7443/jazz](https://localhost:7443/jazz)


Hints
-----
### Execute ps1 files on double-click
The con of using ps1 files is that they can't be executed automatically on double click by default. So if you want to make ps1 files behave in the same way as conventional command files, modify the following registry key:

> HKEY_CLASSES_ROOT\Microsoft.PowerShellScript.1\Shell\open\command

change the default value to the following:

> "C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe" -noLogo -ExecutionPolicy unrestricted -file "%1"

[Source](http://stackoverflow.com/a/20623597)

Upgrade Debug Environment
-------------------------
We will try to release a new version of the Debug Environment as soon as possible, so that you can only pull the latest changes from GitHub. If you cannot wait or if you would like to contribute to our project by providing it with an upgrade package, head over to the [Upgrade Guide](https://github.com/jazz-community/jazz-debug-environment/blob/master/UPGRADE.MD)

Using the scripts for IntelliJ (Community Edition) launch configurations
------------------------------------------------------------------------

This project comes with batch files intended for debugging the jetty launch configuration as a remote server from within IntelliJ community edition. This section explains how to use the provided files for setting up debug launch configurations that automatically launch and attach to an instance of jetty.

If you are using the Ultimate Edition of IntelliJ, you can just setup a standard jetty launch configuration and don't need to rely on any of the files mentioned in this section.

#### Setup debug configuration

 1. Open 'Run -> Edit Configurations'
 2. Press '+' and select 'Remote'
 3. Choose a name for your debug configuration
 4. Select 'Single instance only'
 5. Set the port to 9999

In the next steps, we will set up the provided scripts to launch jetty and ensure that only one instance is running at any given time.

 6. Add before launch
![alt tag](https://github.com/innerjoin/jazz-debug-environment/blob/master/screenshots/add_before_launch.png)
 7. Select 'Run external tool' from the drop down
 8. Create a new tool by pressing '+'
 9. Call this external tool 'Run Jetty'. This will be used to launch a new instance of jetty from the repository.
 10. Adjust the settings according to the screenshot. **Make sure to adjust the path to point to your repository location**
![alt tag](https://github.com/innerjoin/jazz-debug-environment/blob/master/screenshots/start_jetty_config.png)
 11. Create another external tool, this time calling it 'Stop Jetty'. This will be used to kill any running debugging instance of jazz running in jetty. As before, mimic the settings in the screenshot. **Make sure to adjust the path to point to your repository location**
![alt tag](https://github.com/innerjoin/jazz-debug-environment/blob/master/screenshots/stop_jetty_config.png)
 12. Confirm that your setup resembles the screenshot. Make sure that the 'Stop Jetty' tool runs before the 'Run Jetty' tool. This can be adjusted with the arrows above the list view.
![alt tag](https://github.com/innerjoin/jazz-debug-environment/blob/master/screenshots/completed_debug_configuration.png)
 13. To run jetty from IntelliJ, click 'Run -> Debug... -> <Name of your debug configuration>'
