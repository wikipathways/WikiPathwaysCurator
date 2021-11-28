![Java CI with Maven](https://github.com/BiGCAT-UM/WikiPathwaysCurator/workflows/Java%20CI%20with%20Maven/badge.svg)

# Unit tests for curating WikiPathways using SPARQL.

This code base runs a number of SPARQL queries wrapped in JUnit files to detect
issues in the GPML content, by assessing the WPRDF.

## License

MIT license

# Running the tests

First, you need to copy WikiPathways RDF file into a local folder, e.g.
`/tmp/doesntexist/`, and then you can run the tests with by setting a few
options:

```shell
mvn install -DOPSWPRDF=/tmp/doesntexist -DSUBSETPREFIX=wp9
```

Or run a specific test:

```shell
mvn install -DOPSWPRDF=/tmp/doesntexist -DSUBSETPREFIX=wp9 -Dtest=nl.unimaas.bigcat.wikipathways.curator.EnsemblGenes
```

## Selection subsets of tests

JUnit was used to define groups of tests, which can be included and excluded on
runtime. For this, the `junit5.excludeGroups` and `junit5.groups` options
can be used. For example, to only run the VoID header file tests, do:

```shell
mvn install -DOPSWPRDF=/tmp/doesntexist -DSUBSETPREFIX=wp9 -Djunit5.groups=void
```

## SPARQL end point

If you wish to run the tests agains a SPARQL end point (e.g. http://sparql.wikipathways.org/),
then you run:

```shell
mvn install -DSPARQLEP=https://sparql.wikipathways.org/sparql
```

If you have the data loaded into a local [Blazegraph](https://github.com/blazegraph/database)
installation, then the command looks like:

```shell
mvn install -DSPARQLEP=http://localhost:9999/blazegraph/sparql
```
