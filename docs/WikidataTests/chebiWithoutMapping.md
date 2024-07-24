# ChEBI identifier without a match in Wikidata

This [ChEBI](https://www.ebi.ac.uk/chebi/) identifier is used in WikiPathways but it does not
have a known entry in [Wikidata](https://wikidata.org/).

## Why does this fail show up?

If the used ChEBI identifier exists in ChEBI, then there is nothing wrong with WikiPathways
and no curation is needed in WikiPathways.

This test fails if the latest BridgeDb metabolite identifier mapping does not have a mapping.
The cause can be that the lipid is not listed in Wikidata yet, or that the BridgeDb file did
not capture it yet.

## Curation action

This test is really aimed a curation of Wikidata and ChEBI. You can check if the chemical is
found in Wikidata by searching for the chemical in Wikidata or Wikipedia by name.
If it is there, then please let the [BridgeDb](https://www.bridgedb.org/) metabolite ID mapping database
developers know this in their [issue tracker](https://github.com/bridgedb/create-bridgedb-metabolites/issues).
