Upgrade Debug Environment
-------------------------
This Guide describes on how to create a Debug Environment for a new Release of Jazz. 
If you just wish to use the Debug Environment, read our main [README](https://github.com/jazz-community/jazz-debug-environment/blob/master/README.MD)

We will try to release a new version of the Debug Environment as soon as possible, so that you can only pull the latest changes from GitHub. If you cannot wait or if you would like to contribute to our project by providing it with an upgrade package, you may follow the instructions below.

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
You can basically follow [Ralph Schoons Extension Workshop written for 6.0.3](https://rsjazz.wordpress.com/2017/03/17/updated-rtc-extensions-workshop-for-rtc-6-0-3)

 - Create DB
 - Run Jetty based Launch (from Ralphs Extension Workshop)
 - Login with ADMIN/ADMIN
 - Deploy Predefined Process Templates
 - Import 10 Free Devloper Licenses
 - Shutdown
 
#### Copy Database
Copy generated database and create a ZIP file out of it.

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
```
com.hp.hpl.jena.rdf,2.7.2000.v20160630_1732,file:/C:/RTC603Dev/installs/rtc-server-sdk/plugins/com.hp.hpl.jena.rdf_2.7.2000.v20160630_1732/,4,true
com.ibm.icu,52.1.1.v201501240615,file:/C:/RTC603Dev/installs/rtc-server-sdk/plugins/com.ibm.icu_52.1.1.v201501240615.jar,4,true
```

**Example after Find/Replace**
```
com.hp.hpl.jena.rdf_2.7.2000.v20160630_1732/@start
com.ibm.icu_52.1.1.v201501240615.jar@start
```