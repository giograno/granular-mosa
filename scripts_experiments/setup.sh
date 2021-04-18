#!/bin/bash
rm -rf projects SF110
wget http://www.evosuite.org/files/SF110-20130704.zip
unzip -q SF110-20130704.zip 
mv SF110-20130704 projects
wget http://www.evosuite.org/files/SF110-20130704-src.zip
unzip -q SF110-20130704-src.zip
mv SF110-20130704-src SF110

