# Jazz Debug Environment
The goal of this project is to provide you with a ready-to-go environment for extending your jazz applications.

## Limitations
Please note that this approach currently **works only for RTC 6.0.3**. See [GitHub Issue](https://github.com/jazz-community/jazz-debug-environment/issues/4) and [jazz.net Enhancement](https://jazz.net/jazz/web/projects/Rational%20Team%20Concert#action=com.ibm.team.workitem.viewWorkItem&id=438754) for more information.

## Prerequisites
 - Rational Team Concert Server SDK, download from [Jazz.net](https://jazz.net/downloads/rational-team-concert/) (make sure that you choose the right version)
 - IBM JRE (Java Runtime Environment by IBM). Can be extracted from a download that contains the JTS (eg. JTS-CCM-keys). Downloads can also be found on [Jazz.net](https://jazz.net/downloads/rational-team-concert/).

## Installation
 1. Click the 'Clone or download' button on GitHub, then do one of the following:
    - Download & extract the ZIP file to your desired location
    - Copy the clone URL, navigate to the desired location on your machine and use `git clone https://github.com/jazz-community/jazz-debug-environment.git` to get this repos content
 2. Extract the RTC Server SDK into the 'sdk' folder.
 3. Extract/Copy the IBM JRE into the 'jre' folder.
 4. Extract one of the database presets which are available within the 'db_presets' folder into the 'db' folder
 5. Verify that your folder structure looks like the following:

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
## Configure Extensions for Debugging
In order to be able to debug your extensions, modify the `my_workspaces.cfg`, stored within `conf\jetty\user_configs` to contain your extensions. The file content could look like the following (whereas lines starting with a **`#`** are ignored):
```
C\:/myWork/com.siemens.bt.jazz.ui.WorkItemBulkMover@start
D\:/myOtherWork/com.siemens.bt.jazz.rtc.workitemeditor.presentation.statushistory@start
# C\:/somewhere-else/rtc-create-child-item-plugin@start
```

## Check out correct branch
The master branch is meant for working with Windows. For working with bash under Linux, check out the linux branch `git checkout linux`.

## Start the Server
 - If you have changed your configuration, or are running this for the first time, do the following:
    * Windows: run the `generate_jetty_config.ps1` script which can be found in the `conf/jetty` folder
    * Linux: run the `generate_jetty_config.sh` script in the `conf/jetty`
 - Start the server 
    * Windows: by double-clicking `click_launch.ps1` or running `run_jetty.ps1`
    * Linux: by running `run_jetty.sh`
 - Open [https://localhost:7443/jazz](https://localhost:7443/jazz) in a browser of your choice
 - When prompted for credentials, type **ADMIN** as your User ID and use the same (**ADMIN**) as password
 - Start debugging your Jazz extensions

![Example service debugging](https://github.com/jazz-community/jazz-debug-environment/blob/master/docs/images/debugging-services.png)

## Hints
### Execute ps1 files on double-click
The con of using ps1 files is that they can't be executed automatically on double click by default. So if you want to make ps1 files behave in the same way as conventional command files, modify the following registry key:

> HKEY_CLASSES_ROOT\Microsoft.PowerShellScript.1\Shell\open\command

change the default value to the following:

> "C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe" -noLogo -ExecutionPolicy unrestricted -file "%1"

[Source](http://stackoverflow.com/a/20623597)

## Upgrade Debug Environment
We will try to release a new version of the Debug Environment as soon as possible, so that you can only pull the latest changes from GitHub. If you cannot wait or if you would like to contribute to our project by providing it with an upgrade package, head over to the [Upgrade Guide](https://github.com/jazz-community/jazz-debug-environment/blob/master/UPGRADE.MD)

## Using the scripts for IntelliJ (Community Edition)
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
![alt tag](https://github.com/jazz-community/jazz-debug-environment/blob/master/docs/images/add_before_launch.png)
 7. Select 'Run external tool' from the drop down
 8. Create a new tool by pressing '+'
 9. Call this external tool 'Run Jetty'. This will be used to launch a new instance of jetty from the repository.
 10. Adjust the settings according to the screenshot. **Make sure to adjust the path to point to your repository location**
![alt tag](https://github.com/jazz-community/jazz-debug-environment/blob/master/docs/images/start_jetty_config.png)
 11. Create another external tool, this time calling it 'Stop Jetty'. This will be used to kill any running debugging instance of jazz running in jetty. As before, mimic the settings in the screenshot. **Make sure to adjust the path to point to your repository location**
![alt tag](https://github.com/jazz-community/jazz-debug-environment/blob/master/docs/images/stop_jetty_config.png)
 12. Confirm that your setup resembles the screenshot. Make sure that the 'Stop Jetty' tool runs before the 'Run Jetty' tool. This can be adjusted with the arrows above the list view.
![alt tag](https://github.com/jazz-community/jazz-debug-environment/blob/master/docs/images/completed_debug_configuration.png)
 13. To run jetty from IntelliJ, click 'Run -> Debug... -> <Name of your debug configuration>'

## Contributing
Please use the [Issue Tracker](https://github.com/jazz-community/jazz-debug-environment/issues) of this repository to report issues or suggest enhancements.

For general contribution guidelines, please refer to [CONTRIBUTING.md](https://github.com/jazz-community/jazz-debug-environment/blob/master/CONTRIBUTING.md)

## Licensing
Copyright (c) Siemens AG. All rights reserved.<br>
Licensed under the [MIT](https://github.com/jazz-community/jazz-debug-environment/blob/master/LICENSE) License.
