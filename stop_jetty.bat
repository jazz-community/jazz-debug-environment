wmic Path win32_process Where "CommandLine Like '%%jazz_debug_environment%%java.exe%%'" Call Terminate
