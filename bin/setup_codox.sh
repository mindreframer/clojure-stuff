#!/bin/bash
set -e

# One-time script to setup codox deploy to github pages.
# https://github.com/weavejester/codox/wiki/Deploying-to-GitHub-Pages
cd `dirname $0`
cd ..

rm -rf doc && mkdir doc
git clone git@github.com:prismatic/plumbing.git doc
cd doc
git symbolic-ref HEAD refs/heads/gh-pages
rm .git/index
git clean -fdx