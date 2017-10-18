#!/bin/bash

# clear gen folder
rm -rf gen/*

# copy config template to gen
cp data/config.template gen/config.ini

# fix encoding issues
dos2unix user_configs/*.cfg
dos2unix user_configs/*.properties

# add reference string, trunc onto line, prepend with osgi.bundles
# and remove trailing comma
grep -ah "^[^#]" user_configs/*.cfg \
    | sed 's/\(.*\)/reference\\:file\\:\1/' \
    | tr -d '\r' \
    | tr '\n' ',' \
    | sed 's/^/\nosgi.bundles=/' \
    | sed 's/,$//' \
    >> gen/config.ini

# merge .config user files into a single configuration
grep -ah "^[^#]" user_configs/*.properties > gen/dev.properties
