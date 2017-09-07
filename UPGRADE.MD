# Upgrade Debug Environment
This Guide describes on how to create a Debug Environment for a new Release of Jazz. 
If you just wish to use the Debug Environment, read our main [README](https://github.com/jazz-community/jazz-debug-environment/blob/master/README.MD)

We will try to release a new version of the Debug Environment as soon as possible, so that you can only pull the latest changes from GitHub. If you cannot wait or if you would like to contribute to our project by providing it with an upgrade package, you may follow the instructions below.

### Prepare your own package
Please note: this was accurate as of 6.0.3, it may change in future releases. If so, please contribute such hints back to this documentation

#### Prerequisites
Required Downloads from the *All Downloads* section on [Jazz.net](https://jazz.net/downloads/rational-team-concert/):

- 10-Free Developers License Activation Kit
- Client for Eclipse 4.2.x IDE
- Rational Team Concert **Server** SDK 

One more Download is required:
- Go to the feature based launches wiki page at https://jazz.net/wiki/bin/view/Main/FeatureBasedLaunches
- Scroll down to the end of the page. Download the latest available version of the feature based launcher for Eclipse 4.4.2 or later. At the time of writing the version is *launcher442.zip*.

#### Installation

The following instruction are a compact excerpt of [Ralph Schoon's Extension Workshop written for 6.0.3](https://rsjazz.wordpress.com/2017/03/17/updated-rtc-extensions-workshop-for-rtc-6-0-3). 

- Extract all Downloads on your local machine
- In the extracted folder containing the feature based launches (e.g. launcher442\plugins) locate the JAR file named *com.ibm.team.dev.launch_0.3.13.201605301342.jar* (or similar). Copy it into the RTC Eclipse Client dropins folder. Create a folder named **dropins** if it does not exist.
- Start **eclipse.exe** from inside the RTC Client Eclipse package. During start, define a workspace folder if not already set.
- Target Platform Setup:
    - From the RTC Eclipse Client menu bar, select **Window > Preferences**. In the Preferences dialog, select **Plug-in Development > Target Platform**. Wait for the load process to finish, then click **Add...**
    - In the New Target Definition wizard, select **Nothing: Start with an empty target definition** and then click **Next**.
    - On the second page of the wizard, enter **RTC Server SDK** as the Name and click **Add...**
    - In the Add Content wizard, select **Installation** and then click **Next**
    - On the second page of the wizard, set the path to the downloaded and extracted *RTC Server SDK* as the **Location** and then click **Finish**
    - After the operation completes, click **Finish** in the New Target Definition wizard
    - Back on the Preferences dialog, select the new **RTC Server SDK Target Definition** and then click **OK**
- Database Creation:
    - From the menu bar, select **File > Import…** and then in the Import wizard, select **Plug-in Development > Plug-ins and Fragments** and then click **Next**
    - Make sure that **Import From** is set to **Target Definition** and **RTC Server SDK** is selected for it. Set **Import as** to **Project with source folders**. Then click **Next**
    - Enter **com.ibm.team.common.tests.utils** into the ID field and select the plug-in matching that ID (Click **Add ->**). Click **Finish**.
    - In the top right corner of Eclipse, switch to the **Plug-in Development** perspective
    - Select **Run Configurations…** from the drop down menu of the Run toolbar icon
    - On the Run Configurations dialog, select **JUnit2 Launch > [Standard] Create Test Database** and then click **Run**
    - This may take a while to run. The *Console* view will appear and show quite a bit of output. The *JUnit* view will also be active. When the database creation is complete, the *JUnit* view will show success. Note that the Console view will show some exceptions. Be patient, this takes a good while and unless the launch is terminated, let it keep running. **The important thing is that the JUnit view shows success. If it fails, retry again and again until it succeeds**
- Run local Jazz server:
    - Navigate again to **Run Configurations…**. This time, choose **[JSA Standard] Jetty (Standalone)** and click **Run** to start a local development server. 
    - The **Console** will print a lot of info and error messages. Just ignore them
    - Open a web browser and navigate to **https://localhost:9443/jazz/admin**. Type **ADMIN** as user ID and **ADMIN** as password as soon as the login screen appears.
    - Deploy Predefined Process Templates. Navigate to **Templates** and click on **Deploy Predefined Templates**.
    - Import the 10 Free Developer Licenses you've initially downloaded. Navigate to **Server > License Key Management** and click **Add...** in the *Client Access License Types* grid. Upload the License ZIP file and work through the installation wizzard.
    - Shutdown the local server by closing Eclipse
 
#### Copy Database
Create the following folder structure:
```
-- conf
---- jazz
------- indices
------- derby
--------- **repositoryDB**
```

Parallel to your eclipse workspace, there is another folder named **<your eclipse workspace>-testDB**. Copy it's contents over into the above created **repositoryDB** folder.

Create a ZIP file out of the above created **conf** folder and name it **<RTC Version>_db.zip** (e.g. 604_db.zip) and create a pull request on GitHub if you want to share this DB preset with the community.

#### Create sdk_files.cfg
Navigate to the following folder (whereas `${workspace_loc}` is the folder of your Eclipse workspace)

    ${workspace_loc}\.metadata\.plugins\org.eclipse.pde.core\[JSA Standard] Jetty (Standalone)\org.eclipse.equinox.simpleconfigurator

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

