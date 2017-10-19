@echo off
@start powershell.exe -file %1

:: Ugly hack has to be used instead of the built-in timeout command because
:: IntelliJ, for reasons, doesn't allow the timeout command in external 
:: tools.

ping 127.0.0.1 -n 10 > NUL
