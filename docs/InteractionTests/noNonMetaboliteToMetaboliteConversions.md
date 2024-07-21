# No non-metabolite to metabolite conversions

This test checks for interactions that convert one molecule to another. 

## Why does this fail show up?

The returned interactions converts protein, RNA or DNA omtp a metabolite This is
expected for some proteins that act as signalling molecules (hormones), like
bradykinin, but not generally applicable.

## Curation action

If this report is a false-positive (e.g. another hormone), then please
[report it here](https://github.com/wikipathways/WikiPathwaysCurator/issues).

In most cases the use of `mim-conversion` was an error, and quite common as
the depiction in PathVisio is identical as a regular directed interaction.
