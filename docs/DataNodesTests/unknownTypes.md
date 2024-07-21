# Datanodes of unknown type

Datanodes can be of type `GeneProduct`, `Protein`, `Metabolite`, and a few more. This
test finds nodes that have datanodes of `@Type` `Unknown` or without a `@Type` attribute
and then the node type defaults to `Unknown`.

## Why does this fail show up?

The DataNode does not have a `@Type` defined or it is explicitly defined at `Unknown`.

## Curation action

Set the `@Type` of the datanode to something more specific.
