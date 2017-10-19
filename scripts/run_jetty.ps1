& "jre\bin\java.exe" `
-Xdebug "-agentlib:jdwp=transport=dt_socket,address=127.0.0.1:9999,server=y,suspend=n" `
-Xmx512M `
-XX:PermSize=64M `
-XX:MaxPermSize=256M `
"-Dlog4j.configuration=file:conf/jazz/log4j.properties" `
"-Dcom.ibm.team.repository.web.homeuri=http://www.jazz.net" `
-"Dcom.ibm.team.repository.web.helpuri=http://publib.boulder.ibm.com/infocenter/clmhelp/v4r0/index.jsp" `
"-Dcom.ibm.team.repository.discovery.document.location=file:conf/jazz/services.xml" `
"-Dcom.ibm.team.repository.scr.document.location=file:conf/jazz/scr.xml" `
"-Dcom.ibm.team.repository.ws.allow.identity.assertion=false" `
"-Dorg.mortbay.log.LogFactory.noDiscovery=true" `
"-Dorg.eclipse.equinox.servlet.bridge.enabled=true" `
"-Dorg.eclipse.emf.ecore.plugin.EcorePlugin.doNotLoadResourcesPlugin=true" `
"-Declipse.ignoreApp=false" `
"-Dosgi.noShutdown=true" `
"-Dosgi.compatibility.bootdelegation=false" `
"-Dorg.osgi.framework.bootdelegation=sun.*,com.ms.*,com.ibm.*,com.sun.*,org.w3c.*,org.xml.*,javax.*" `
"-Dcom.ibm.team.server.configURL=file:conf/jazz/teamserver.properties" `
"-Dcom.ibm.team.repository.server.webapp.url=https://localhost:7443/jazz" `
"-Dcom.ibm.team.scm.enable.distributed=true" `
"-Dcom.ibm.team.repository.server.mode=STANDALONE" `
"-Dcom.ibm.team.repository.db.jdbc.location=db/conf/jazz/derby/repositoryDB" `
"-Dcom.ibm.team.jfs.index.root.directory=db/conf/jazz/indices" `
"-Dcom.ibm.team.fulltext.indexLocation=db/conf/jazz/indices/workitemindex" `
"-Djetty.http.port=7080" `
"-Djetty.https.port=7443" `
"-Duser.language=en" `
"-Dcom.siemens.bt.jazz.services.cqrest.cqrooturi=https://bt-clearquest.hqs.sbt.siemens.com/cqweb/oslc/" `
"-Dfile.encoding=UTF-8" `
-classpath sdk\plugins\org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar org.eclipse.equinox.launcher.Main `
-dev "file:conf/jetty/gen/dev.properties" `
-configuration "file:conf/jetty/gen"
