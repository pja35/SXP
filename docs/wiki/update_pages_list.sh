#!/bin/sh
#
# this script creates/updates(overrides) PagesList.md

# create top of the page
echo "---" > PagesList.md
echo "title: Pages List" >> PagesList.md
echo "permalink: wiki/PagesList/" >> PagesList.md
echo "layout: wiki" >> PagesList.md
echo "---" >> PagesList.md
echo "" >> PagesList.md

# add the list of existing pages in links format
ls --color=none *.md | grep -v PagesList.md | sed "s/\(.*\)/-   [\1](\/SXP\/wiki\/\1 \"wikilink\")/" | sed "s/.md//g"  >> PagesList.md

# add an empty line
echo "" >> PagesList.md

