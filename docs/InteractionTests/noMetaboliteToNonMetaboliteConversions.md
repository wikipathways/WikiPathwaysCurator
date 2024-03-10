# No metabolite to non-metabolite conversions

This test checks for interactions that convert one molecule to another. 

## Why does this fail show up?

The returned interactions converts a metabolites into RNA or DNA. This is only
expected in the details of DNA replication and protein synthesis.

## Curation action

In most cases the use of `mim-conversion` was an error, and quite common as
the depiction in PathVisio is identical as a regular directed interaction.
