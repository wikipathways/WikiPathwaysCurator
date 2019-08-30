/* Copyright (C) 2018  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class Wikidata {

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
			String parseErrors = OPSWPRDFFiles.getParseErrors();
			Assertions.assertNotNull(parseErrors);
			Assertions.assertEquals(0, parseErrors.length(), parseErrors.toString());
		}
	}

	@BeforeEach
	public void waitForIt() throws InterruptedException { Thread.sleep(200); }

	private static Set<String> zwitterIonsWithoutWikidata = new HashSet<>();
	{{
		zwitterIonsWithoutWikidata.add("CHEBI:33384"); // L-serine zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:57427"); // L-leucine zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:57972"); // L-alanine zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:58045"); // L-isoleucine zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:58048"); // L-asparagine zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:58315"); // L-tyrosine zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:58359"); // L-glutamine zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:60039"); // L-proline zwitterion
		zwitterIonsWithoutWikidata.add("CHEBI:58199"); // L-homocystein zwitterion
	}}

	@Tag("wikidata")
	@Test
	public void chebiWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteChEBI.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String chebiID = table.get(i, "metabolite");
				if (!zwitterIonsWithoutWikidata.contains(chebiID.substring(29))) {
				    errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					       + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
				    errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount,
			"ChEBI identifiers without Wikidata mappings:\n" + errors
		);
	}

	@Tag("wikidata")
	@Test
	public void casWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteCAS.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"CAS identifiers without Wikidata mappings:\n" + errors
		);
	}

	@Tag("wikidata")
	@Test
	public void hmdbWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteHMDB.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"HMDB identifiers without Wikidata mappings:\n" + errors
		);
	}

	private static Set<String> noWarnKEGGCIDs = new HashSet<>();
	{{  // ACPs (acyl-carrier proteins)
		noWarnKEGGCIDs.add("C04688");
		noWarnKEGGCIDs.add("C05729");
		noWarnKEGGCIDs.add("C05746");
		noWarnKEGGCIDs.add("C05747");
		noWarnKEGGCIDs.add("C05748");
		noWarnKEGGCIDs.add("C05749");
		noWarnKEGGCIDs.add("C05750");
		noWarnKEGGCIDs.add("C05751");
		noWarnKEGGCIDs.add("C05752");
		noWarnKEGGCIDs.add("C05753");
		noWarnKEGGCIDs.add("C05754");
		noWarnKEGGCIDs.add("C05755");
		noWarnKEGGCIDs.add("C05757");
		noWarnKEGGCIDs.add("C05758");
		noWarnKEGGCIDs.add("C05759");
		noWarnKEGGCIDs.add("C05759");
		noWarnKEGGCIDs.add("C05761");
		noWarnKEGGCIDs.add("C05761");
		noWarnKEGGCIDs.add("C05762");
		noWarnKEGGCIDs.add("C05763");
		noWarnKEGGCIDs.add("C05764");
		noWarnKEGGCIDs.add("C16255");
		noWarnKEGGCIDs.add("C00173");
		noWarnKEGGCIDs.add("C16239");
		// things with R groups
		noWarnKEGGCIDs.add("C16254");
		noWarnKEGGCIDs.add("C15973");
		noWarnKEGGCIDs.add("C06250");
		noWarnKEGGCIDs.add("C16236");
		noWarnKEGGCIDs.add("C16237");
	}}

	@Tag("wikidata")
	@Test
	public void keggWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteKEGG.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String keggID = table.get(i, "metabolite");
				if (!noWarnKEGGCIDs.contains(keggID.substring(37))) {
					errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
						   + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
					errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount,
			"KEGG Compound identifiers without Wikidata mappings:\n" + errors
		);
	}

	@Tag("wikidata")
	@Test
	public void pubchemCIDWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metabolitePubChemCID.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"PubChem-compound identifiers without Wikidata mappings:\n" + errors
		);
	}

	@Tag("wikidata")
	@Test
	public void chemspiderCIDWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteChemspider.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"Chemspider identifiers without Wikidata mappings:\n" + errors
		);
	}

	@Tag("wikidata")
	@Test
	public void lipidMapsWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteLipidMaps.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"LIPID MAPS identifiers without Wikidata mappings:\n" + errors
		);
	}

	@Tag("wikidata")
	@Test
	public void kNApSAcKWithoutMapping() throws Exception {
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteKNApSAcK.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"KNApSAcK identifiers without Wikidata mappings:\n" + errors
		);
	}

	@Tag("wikidata")
	@Test
	public void replaceWikipedia() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/wikipedia.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "node") +
					" can be replaced by the matching Wikidata identifier; \n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"Wikipedia identifies that can be replaced by Wikidata identifiers:\n" + errors
		);
	}

	@Test
	public void wikidataIdentifiersWrong() throws Exception {
		String sparql = ResourceHelper.resourceAsString("general/allWikidataIdentifiers.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier").trim();
				String pathwayPage = table.get(i, "homepage");
				if (identifier.isEmpty() || !identifier.startsWith("Q")) {
					errors += pathwayPage + " -> " + table.get(i, "label") +
							", '" + table.get(i, "identifier") + "'\n ";
					errorCount++;
				} else if (!identifier.isEmpty() && identifier.startsWith("Q")) {
					try {
						Integer.parseInt(identifier.substring(1));
					} catch (NumberFormatException exception) {
						errors += pathwayPage + " -> " + table.get(i, "label") +
								", " + table.get(i, "identifier") + "\n ";
						errorCount++;
					}
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Wikidata identifiers that do not start with a 'Q' followed by a number:\n" + errors
		);
	}

	private static Set<String> allowedDuplicates = new HashSet<>();
	{{
		allowedDuplicates.add("7732-18-5"); // water + demi water
	}}

	@Tag("wikidata")
	@Test
	public void duplicateWikidataMappings() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/duplicateWikidata.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String metaboliteID = table.get(i, "metaboliteID").trim();
				if (!metaboliteID.contains(metaboliteID.substring(27))) {
				    String results = table.get(i, "results");
				    errors += metaboliteID + " mapped to Wikidata: " + results + "\n ";
				    errorCount++;
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "More than one Wikidata identifier for:\n" + errors
		);
	}

	@Test
	public void wikDataTypo() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/wikidata.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Typo 'Wikdata' data sources (use 'Wikidata'):\n" + table);
		});
	}

	@Tag("wikidata")
	@Test
	public void noWikidataForGenes() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/noWikidataYet.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "homepage") +
					" has " + table.get(i, "identifier") + " for the " +
					table.get(i, "wpType") + " " + table.get(i, "label") + "\n";
			}
		}
		Assertions.assertEquals(
			0, table.getRowCount(),
			"Wikidata identifiers cannot be used for GeneProduct or Protein yet:\n" + errors
		);
	}
}
