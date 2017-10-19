#!/bin/bash

kill $(ps -C java -o pid,command | grep jazz | cut -d ' ' -f 2)
