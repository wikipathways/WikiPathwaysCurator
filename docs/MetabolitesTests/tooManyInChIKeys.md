# Too many InChIKeys for the used identifier

For good interoperability, metabolite `DataNode` identifiers map to a single InChIKey.
Mapping to more than on InChIKey original from a possible problem in the BridgeDb metabolite
identifier mapping database, copied from the sources it used (currently HMDB, ChEBI, and Wikidata).

## Why does this fail show up?

The used identifier maps to more than one InChIKey. This does not mean the used identifier
is wrong itself.

## Curation action

You can either report the problem in [this BridgeDb tracker](https://github.com/bridgedb/create-bridgedb-metabolites/issues)
or see of the problem originates from [Wikidata](https://www.wikidata.org/).
