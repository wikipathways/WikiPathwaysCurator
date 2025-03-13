# Genes with identifier but no Ensembl identifier mapping

## Why does this fail show up?

This fail shows up if BridgeDb did not have an Ensembl mapping for the given gene or protein.
Ensembl is used as source for these mappings.

## Curation action

There is not a single reason why this fail shows up. It can be that the used identifier
is for a putative gene where there is no consensus yet. Or the identifier
was valid at some moment in time, but is no longer used. Another reason can be that
Ensembl does not (or no longer) support the identifier. Manual curation of the identifier
used in the pathway is advised.
