/* Copyright (C) 2013,2018  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.unimaas.bigcat.wikipathways.curator;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Metabolites {

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
		}
	}

	@BeforeEach
	public void waitForIt() throws InterruptedException { Thread.sleep(OPSWPRDFFiles.SLEEP_TIME); }

	@Test
	public void metaboliteAlsoOtherType() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/badType.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Set<String> exceptions = new HashSet<String>();
		exceptions.add("http://identifiers.org/chebi/CHEBI:16991"); // DNA
		exceptions.add("http://identifiers.org/chebi/CHEBI:39026"); // LDL
		exceptions.add("http://identifiers.org/wikidata/Q27205"); // fibrin
		exceptions.add("http://identifiers.org/wikidata/Q381899"); // fibrogen
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String metabolite = table.get(i, "metabolite");
				if (!exceptions.contains(metabolite)) {
  				    errors += metabolite + " is also found to be " + table.get(i, "type") + "\n";
				    errorCount++;
				}
			}
		}
		Assertions.assertEquals(
		    0, errorCount, "Metabolite is also found to be another type:\n" + errors
		);
	}

	@Test
	public void casNumbersNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/casNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Set<String> allowedProteins = new HashSet<String>();
		allowedProteins.add("IFN-b");
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Unexpected CAS identifiers for non-metabolites:\n" + errors
		);
	}

	@Test
	public void chemspiderIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/chemspiderNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Assertions.assertEquals(0, table.getRowCount(), "Unexpected ChemSpider identifiers for non-metabolites:\n" + table);
	}

	@Test
	public void ChEBIIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/chebiNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Set<String> allowed = new HashSet<String>();
		allowed.add("CHEBI:15986"); // polynucleotide
		allowed.add("CHEBI:9160");  // single stranded DNA
		allowed.add("CHEBI:16991"); // double stranded DNA
		allowed.add("CHEBI:33697"); // ribonucleic acid (RNA)
		allowed.add("CHEBI:33698"); // DNA
		allowed.add("CHEBI:33699"); // mRNA
		allowed.add("CHEBI:39026"); // LDL
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowed.contains(table.get(i, "identifier").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
					    " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Unexpected ChEBI identifiers for non-metabolites:\n" + errors
		);
	}

	@Test
	public void HMDBIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/hmdbNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Assertions.assertEquals(0, table.getRowCount(), "Unexpected HMDB identifiers for non-metabolites:\n" + table);
	}

	@Test
	public void KEGGIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/keggNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Assertions.assertEquals(0, table.getRowCount(), "Unexpected KEGG identifiers for non-metabolites:\n" + table);
	}

	@Test
	public void PubChemIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/pubchemNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Set<String> allowedProteins = new HashSet<String>();
		allowedProteins.add("Fibrin");
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Unexpected PubChem identifiers for non-metabolites:\n" + errors
		);
	}

	@Test
	public void PubChemSubstanceIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/pubchemSubstanceNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Set<String> allowedProteins = new HashSet<String>();
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Unexpected PubChem Substance identifiers for non-metabolites:\n" + errors
		);
	}

	@Test
	public void PubChemIDsNotNumbers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allPubChemIdentifiers.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer.parseInt(identifier);
				} catch (NumberFormatException exception) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
					errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Unexpected PubChem Compound identifiers for non-metabolites:\n" + errors
		);
	}

	@Test
	public void PubChemSubstanceIDsNotNumbers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allPubChemSubstanceIdentifiers.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer.parseInt(identifier);
				} catch (NumberFormatException exception) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
					errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Unexpected PubChem Substance identifiers for non-metabolites:\n" + errors
		);
	}

	@Test
	public void metabolitesWithIdentifierButNoDb() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithIdentifierButNoDatabase.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Assertions.assertEquals(0, table.getRowCount(), "Unexpected metabolites with identifier but no database source:\n" + table);
	}

	@Test
	public void metabolitesWithDbButNoIdentifier() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithDatabaseButNoIdentifier.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Assertions.assertEquals(0, table.getRowCount(), "Unexpected metabolites with identifier but no database source:\n" + table);
	}

	@Test
	public void metabolitesWithAnEntrezGeneID() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithAnEntrezGeneID.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Assertions.assertEquals(0, table.getRowCount(), "Unexpected metabolites with an Entrez Gene identifier:\n" + table);
	}

	@Test
	public void metabolitesWithAnEnsembleID() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithAnEnsembleID.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		Assertions.assertEquals(0, table.getRowCount(), "Unexpected metabolites with an Ensemble identifier:\n" + table);
	}
}
