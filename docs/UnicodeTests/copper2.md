# Copper chemical formula can use Unicode

This test checks for labels of copper ion data nodes which can use [Unicode](https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts#Superscripts_and_subscripts_block).

## Why does this test show up?

For a copper ion data node the label Cu2+ is used, but the chemically more correct Unicode
version Cu²⁺ can be used.

## Curation action

Open the pathway and replace the Cu2+ label with a Cu²⁺ label.

IMPORTANT: PathVisio 3.3 currently does not display Unicode characters correctly in the
main view, but it can still be used to edit the labels. After uploading, the classic and
the new website will display the Unicode label correctly.
