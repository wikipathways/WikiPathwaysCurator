# Only LIPID MAPS identifiers

Some communities want lipids in pathways only annotated [LIPID MAPS](https://lipidmaps.org/databases/lmsd) identifiers.
This test checks if only those are used.

## Why does this fail show up?

The pathway is using identifiers for molecules from other databases than LIPID MAPS.

## Curation action

If the test fails, you can look up the LIPID MAPS identifier for the compound
in the pathways that was not using a LIPID MAPS identifier. The GPML source
of the pathway can then be updated to list the new identifier.
