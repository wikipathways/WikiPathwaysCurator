# DataNodes with undefined type but known data source

This test checks data nodes with an identifier have a type different then `@Type="Unknown"`.

## Why does this fail show up?

The default DataNode `@Type` is `Unknown` but if a data source is given, then there is a good
change it can be typed more specifically. For example, when the data source is Ensembl, then
the DataNode can be typed as `GeneProduct`. Similarly, when the data source is LIPID MAPS, then
the DataNode can be typed as `Metabolite`.

## Curation action

The solution is to open the pathway, find the DataNode with the given identifier, and change
the DataNode `@Type` to something different than `Unknown`.
