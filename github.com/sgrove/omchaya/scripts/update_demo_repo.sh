#!/bin/bash

git checkout demo
git rm omchaya.dev.js out omchaya.prod.js omchaya.prod.js.map
git commit -m "Removing old compiled assets"
git merge master
lein clean
lein cljsbuild clean
lein cljsbuild once
git add -f omchaya.dev.js out omchaya.prod.js omchaya.prod.js.map
git commit -m "Updating compiled assets"
git push origin demo
