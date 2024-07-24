# LIPID MAPS identifier without a match in Wikidata

This https://lipidmaps.org/databases/lmsd identifier is used in WikiPathways but it does not have a known
entry in https://wikidata.org/.

## Why does this fail show up?

If the used LIPID MAPS identifier exists in LIPID MAPS, then there is nothing wrong with WikiPathways
and no curation is needed in WikiPathways.

This test fails if the latest BridgeDb metabolite identifier mapping does not have a mapping.
The cause can be that the lipid is not listed in Wikidata yet, or that the BridgeDb file did
not capture it yet.

## Curation action

This test is really aimed a curation of Wikidata and LIPID MAPS. You can check if the lipid is
found in Wikidata with this https://scholia.toolforge.org/ URL pattern:
[https://scholia.toolforge.org/lipidmaps/LMFA03020002](https://scholia.toolforge.org/lipidmaps/LMFA03020002).

If it is there, then the BridgeDb metabolite ID mapping database may be too old. You can encourage
the development team in [this issue tracker](https://github.com/bridgedb/create-bridgedb-metabolites/issues)
to make a new release.
