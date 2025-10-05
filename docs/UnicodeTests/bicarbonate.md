# Bicarbonate chemical formula can use Unicode

This test checks for labels of bicarbonate data nodes which can use [Unicode](https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts#Superscripts_and_subscripts_block).

## Why does this test show up?

For a bicarbonate data node the label HCO3- is used, but the chemically more correct Unicode
version HCO₃⁻ can be used.

## Curation action

Open the pathway and replace the HCO3- label with a HCO₃⁻ label.

IMPORTANT: PathVisio 3.3 currently does not display Unicode characters correctly in the
main view, but it can still be used to edit the labels. After uploading, the classic and
the new website will display the Unicode label correctly.
