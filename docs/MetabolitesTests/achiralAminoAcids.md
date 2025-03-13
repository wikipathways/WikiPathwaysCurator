# Identifier used of a amino acid with undefined stereochemistry

This test checks for identifiers of amino acids where the stereochemistry
is not defined, e.g. DL-serine.

## Why does this fail show up?

While most amino acids involved in metabolism are the L-form, D-form amino acids
can have specific biology.

## Curation action

If this report is a false-positive, look up the specific biology and decide if
the pathway involves the L-amino acid (more common) or the D-amino acid (less common).

A useful approach is to follow the enzyme identifier to the UniProtKB and check
the Rhea record for the enzyme. Frequently, Rhea has the stereochemistry well-defined.
