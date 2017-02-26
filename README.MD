
Jazz Debug Environment
===================

The goal of this project is to provide you with a ready-to-go environment for extending your jazz applications.

[TOC]

Prerequisites
-------------------------------
 - Rational Team Concert Server SDK, download from [Jazz.net](https://jazz.net/downloads/rational-team-concert/) (make sure that you choose the right version)
 - IBM JRE (Java Runtime Environment by IBM)

Installation
-------------------------------
 1. Click the 'Clone or download' button on GitHub, then do one of the following:
  - Download & extract the ZIP file to your desired location
  - Copy the clone URL, navigate to the desired location on your machine and use `git clone <clone URL>` to get this repos content
 4. Extract the RTC Server SDK into the 'sdk' folder.
 5. Extract/Copy the IBM JRE into the 'jre' folder.
 6. Extract one of the database presets which are available within the 'db_presets' folder into the 'db' folder
 7. Verify that your folder structure looks like the following:

> - conf
> - db_presets
> --- .......
> - db
> --- conf
> - jre
> --- bin
> --- docs
> --- lib
> --- properties
> --- ........
> - sdk
> --- features
> --- license
> --- plugins

Start the Server
-------------------------------

 - If you have changed your configuration, or running this for the first time, do the following:
	 - run the `generate_jetty_config.ps1` script which can be found in the 'conf/jetty' folder
 - Start the server by running `run_jetty.ps1`
 - Open [https://localhost:7443/jazz](https://localhost:7443/jazz)



Upgrade Debug Environment
-------------------------------
We will try to release a new version of the Debug Environment as soon as possible, so that you can only pull the latest changes from GitHub. If you cannot wait or if you would like to contribute to our project by providing it with an upgrade package, follow the instructions below...

### Prepare your own package
Please note: this was accurate as of 6.0.3, it may change in future releases. If so, please contribute such hints back to this documentation

#### Prerequisites
Required Downloads from [Jazz.net](https://jazz.net/downloads/rational-team-concert/):

 - Jazz Team Server and the CCM Application, and Trial licenses for Rational Team Concert
 - 10-Free Developers License Activation Kit
 - p2 Install Repository
 - Rational Team Concert Server SDK 

Aditionally, Eclipse Luna (4.4.2) is required, get it from [eclipse.org](https://eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/lunasr2)

#### Installation
TODO: describe how to install

You can basically follow Ralph Schoons Extension Workshop written for 6.0.3

 - Create DB
 - Run Jetty based Launch (from Ralphs Extension Workshop)
 - Login with ADMIN/ADMIN
 - Deploy Predefined Process Templates
 - Import 10 Free Devloper Licenses
 - Shutdown
 - 
 
#### Copy Database


#### Create sdk_files.cfg
Navigate to the following folder (whereas `${workspace_loc}` is the folder of your Eclipse workspace)

    ${workspace_loc}\.metadata\.plugins\org.eclipse.pde.core\[RTCExt] Jetty RTC Server\org.eclipse.equinox.simpleconfigurator

Within this folder, grab the file named `bundles.info` and copy it to `conf/jetty/user_configs/`. Open the file in Notepad++ (or any other editor which supports regular expressions).
Use the Find/Replace feature in Notepad++:

 - Find: `.*(\/plugins\/)(.*(.jar|\/)).*`
 - Replace: `$2@start`
 - Set 'Search Mode' to 'Regular Expression' and do NOT tick 'matches newline'
 - Apply using 'Replace All'
 - Verify results (see example below)
 - Rename to `sdk_files.cfg` (will overwrite the one which was created for a previous jazz version)

**Example before Find/Replace**

>com.hp.hpl.jena.rdf,2.7.2000.v20160630_1732,file:/C:/RTC603Dev/installs/rtc-server-sdk/plugins/com.hp.hpl.jena.rdf_2.7.2000.v20160630_1732/,4,true
>com.ibm.icu,52.1.1.v201501240615,file:/C:/RTC603Dev/installs/rtc-server-sdk/plugins/com.ibm.icu_52.1.1.v201501240615.jar,4,true

**Example after Find/Replace**

> com.hp.hpl.jena.rdf_2.7.2000.v20160630_1732/@start
> com.ibm.icu_52.1.1.v201501240615.jar@start

