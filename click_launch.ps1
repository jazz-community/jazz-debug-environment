echo "Starting the Jazz Debug Environment... (this will likely take a couple of minutes, depending on your machine)"
echo "Navigate to 'https://localhost:7443/jazz' to access the application"

& ((Split-Path $MyInvocation.InvocationName) + "\run_jetty.ps1")

Read-Host -Prompt "For some reason, the jazz debug session did not start properly. Carefully read the entries above (if there are any) and consult the logs. Press Enter to Close this Window"
