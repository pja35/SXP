#!/bin/sh
#
# this script prints a diff between
# * the set of expected pages from intra wiki links
# * the set of existing pages in this directory
#
# you can then use
# $ find . -type f -print | xargs grep "your_broken_link"
# to locate the boken link

# get list of expected pages from intra wiki links
cat * | grep "(\/SXP\/wiki\/" | sed "s/^.*(\/SXP\/wiki\///g" | sed "s/ \"wikilink\".*$//g" | sed "s/$/.md/g" > /tmp/sxp_grep_links

# get list of existing pages
ls --color=none *.md > /tmp/sxp_ls

# compare: step 1
rm -f /tmp/sxp_grep_links2
for i in $(cat /tmp/sxp_grep_links)
do
  grep "^${i}" /tmp/sxp_ls >> /tmp/sxp_grep_links2
done

# compare: step 2
diff /tmp/sxp_grep_links /tmp/sxp_grep_links2

# remove tmp files
rm /tmp/sxp_grep_links /tmp/sxp_grep_links2 /tmp/sxp_ls

