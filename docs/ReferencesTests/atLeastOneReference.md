# At least one reference

This test checks if the pathway cites external resources. Each pathway
should have at least one reference.

Currently, the GPML only fully supports PubMed identifiers, and the tests
throws false positives for pathways with zero PubMed identifiers but with
a book, book chapter, or references with only DOIs.

## Why does this fail show up?

The pathway does not cite a reference with a PubMed identifier.

## Curation action

An additional PubMed identifier can be added to the pathway. This also
solves the false positives.
