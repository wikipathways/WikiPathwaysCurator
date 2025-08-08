# Metabolite with an outdated ChEBI identifier

## Why does this fail show up?

This fail shows up if the ChEBI identifier used as `Xref` is known in the [Sec2Pri](http://github.com/sec2pri/)
project as outdated.

## Curation action

There is not a single reason why this fail shows up. It can be that the used `Xref` identifier
is mistyped or if it has been deprecated or replaced. The error message may provide a suggestion,
but it is recommended to check the correctness.
