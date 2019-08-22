[![Build Status](https://travis-ci.org/BiGCAT-UM/WikiPathwaysCurator.svg?branch=master)](https://travis-ci.org/BiGCAT-UM/WikiPathwaysCurator)

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

## SPARQL end point

If you wish to run the tests agains a SPARQL end point (e.g. http://sparql.wikipathways.org/),
then you run:

```shell
mvn install -DSPARQLEP=http://sparql.wikipathways.org/
```
