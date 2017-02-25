rm -r .\workspace -ea ig
rm -r .\conf\jetty\gen\org* -ea ig
rm -r .\conf\jetty\gen\com* -ea ig
rm derby.log -ea ig

Read-Host -Prompt "Clean Successful! Press Enter to close this window"