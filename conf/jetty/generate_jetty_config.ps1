function formatJars($config) {
    $result = ""

    foreach ($line in $config) {
        if (![String]::IsNullOrEmpty($line) -and !$line.StartsWith("#")) {
            $result += "reference\:file\:$line,"
        }
    }

    return $result
}

function encode($outFile, $tempFile) {
    $encoding = New-Object System.Text.ASCIIEncoding
    $tempFile = Get-Content $tempFile
    [System.IO.File]::WriteAllLines($outFile, $tempFile, $encoding)
}

function createConfig($tempFile) {
    # write a header with creation time stamp
    $timestamp = getTimeStamp
    echo "# generated configuration file $timestamp" >> $tempFile

    # copy template input
    Get-Content data/config.template >> $tempFile

    # these files need to be added to the osgi bundles variable in
    # the config.ini file.
    $userConfigs = Get-ChildItem "user_configs\*.cfg"

    $osgiOutput = "osgi.bundles="

    foreach ($configFile in $userConfigs) {
        $content = Get-Content $configFile
        $osgiOutput += formatJars $content
    }

    # remove the last character because it's a comma
    $osgiOutput = $osgiOutput -replace ".{1}$"

    echo $osgiOutput >> $tempFile
}

function getTimeStamp {
    return [DateTime]::Now.ToString('dd-MM-yy HH:mm:ss')
}

function mergeProperties($tempFile) {
    $timestamp = getTimeStamp
    echo "# generated properties file $timestamp" >> $tempFile

    # write template to output
    Get-Content data/dev.template >> $tempFile

    $files = @(Get-ChildItem user_configs/ -Filter *.properties)

    foreach ($file in $files) {
        $content = Get-Content user_configs/$file

        foreach ($line in $content) {
            if (![String]::IsNullOrEmpty($line) -and !$line.StartsWith("#")) {
                echo $line >> $tempFile
            }
        }
    }
}

# remove previous files or create folder
if (Test-Path gen) {
    rm -r gen/*
} else {
    New-Item gen -type directory >$null 2>&1
}

# create empty files
$tempFile = New-Item gen/config.temp -type file
$outFile = New-Item gen/config.ini -type file

$tempProps = New-Item gen/dev.temp -type file
$devProps = New-Item gen/dev.properties -type file

createConfig $tempFile
mergeProperties $tempProps

# fix encoding problems. If files aren't encoded this way,
# java processes will not be able to read them as configs.
encode $outFile $tempFile
encode $devProps $tempProps


Read-Host -Prompt "Configuration generated successfully. Press Enter to Exit"

