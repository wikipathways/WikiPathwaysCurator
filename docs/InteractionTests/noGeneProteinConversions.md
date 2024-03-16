# Possibly wrong MIM type for Gene-Protein conversions

This test checks if there are interactions where `mim-conversion` is used for
gene to protein translations.

## Why does this fail show up?

The listed interactions are annotated with `mim-conversion`, which is not accurate. 

## Curation action

The interaction should be either a general `Arrow` or a more specific TranscriptionTranslation.
