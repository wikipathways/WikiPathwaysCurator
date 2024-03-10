# Possible MIM translocation

This test checks if there are interactions where the reactant and the product are identical,
in which case it may just be the compound moving around in the cell.

## Why does this fail show up?

An interaction was found between two data nodes about the same molecule.

## Curation action

The interaction should be looked at. If the interaction is indeed a MIM translation, the
GPML should be edited (it cannot be done in PathVisio 3.3) and the interaction type should
be set to `mim-translation`.

If the interaction is not a translocation, then possible one of the data node identifiers
needs correcting.
