# Non-numeric identifiers

This test checks if Rhea identifiers linked to interactions are in the expect format, which
is with just the number and without a prefix.

## Why does this fail show up?

The identifier likely included a prefix like `Rhea:` or `RHEA:`.

## Curation action

If the test fails, update the Rhea identifier in the GPML to only be the number. For example,
the identifier `RHEA:47969` should be listed in the GPML only as `47969`.

