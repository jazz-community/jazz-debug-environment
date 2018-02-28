#!/bin/bash

while read file; do
    s=$(echo "$file" | tr -d '\r')
    s+="_[[:digit:]].*"
    echo "$s"
    find /d/workspaces_git/jazz-debug-environment \
       -regextype posix-extended \
       -name "$s" >> 'sdk_files_find.cfg'
done < sdk_files_nover.cfg
