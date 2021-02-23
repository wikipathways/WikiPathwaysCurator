/* Copyright (C) 2015,2018,2021  Egon Willighagen <egon.willighagen@gmail.com>
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
package nl.unimaas.bigcat.wikipathways.curator.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class WikidataTests {

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

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(keggWithoutMapping(helper));
		assertions.addAll(pubchemCIDWithoutMapping(helper));
		assertions.addAll(hmdbWithoutMapping(helper));
		assertions.addAll(casWithoutMapping(helper));
		assertions.addAll(wikDataTypo(helper));
		assertions.addAll(duplicateWikidataMappings(helper));
		assertions.addAll(noWikidataForGenes(helper));
		assertions.addAll(wikidataIdentifiersWrong(helper));
		assertions.addAll(chemspiderCIDWithoutMapping(helper));
		return assertions;
	}

	public static List<IAssertion> keggWithoutMapping(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteKEGG.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "keggWithoutMapping", table));
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
		assertions.add(new AssertEquals("WikidataTests", "keggWithoutMapping",
			0, errorCount, "KEGG Compound identifiers without Wikidata mappings: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> pubchemCIDWithoutMapping(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metabolitePubChemCID.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "pubchemCIDWithoutMapping", table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals("WikidataTests", "pubchemCIDWithoutMapping",
			0, table.getRowCount(), "PubChem-compound identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> hmdbWithoutMapping(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteHMDB.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "hmdbWithoutMapping", table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals("WikidataTests", "hmdbWithoutMapping",
			0, table.getRowCount(), "HMDB identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> casWithoutMapping(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteCAS.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "casWithoutMapping", table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals("WikidataTests", "casWithoutMapping",
			0, table.getRowCount(), "CAS identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> wikDataTypo(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/wikidata.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "wikDataTypo", table));
		assertions.add(new AssertEquals("WikidataTests", "wikDataTypo",
			0, table.getRowCount(), "Typo 'Wikdata' data sources (use 'Wikidata'): " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> duplicateWikidataMappings(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/duplicateWikidata.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "duplicateWikidataMappings", table));
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
		assertions.add(new AssertEquals("WikidataTests", "duplicateWikidataMappings",
			0, errorCount, "More than one Wikidata identifier for: " + errorCount, errors
		));
		return assertions;
	}

	private static Set<String> acceptableWikidataGenes = new HashSet<>();
	{{
		acceptableWikidataGenes.add("Q27205");   // Protein Fibrin
		acceptableWikidataGenes.add("Q311213");  // Protein HbA1c
		acceptableWikidataGenes.add("Q381899");  // Protein Fibrinogen
		acceptableWikidataGenes.add("Q2162109"); // Protein D-dimer
	}}

	public static List<IAssertion> noWikidataForGenes(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/noWikidataYet.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "noWikidataForGenes", table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String wdQ = table.get(i, "identifier");
				if (!acceptableWikidataGenes.contains(wdQ)) {
					errors += table.get(i, "homepage") +
							" has " + wdQ + " for the " +
							table.get(i, "wpType") + " " + table.get(i, "label") + "\n";
					++errorCount;
				}
			}
		}
		assertions.add(new AssertEquals("WikidataTests", "noWikidataForGenes",
			0, errorCount, "Wikidata identifiers cannot be used for GeneProduct or Protein yet: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wikidataIdentifiersWrong(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allWikidataIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "wikidataIdentifiersWrong", table));
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
		assertions.add(new AssertEquals("WikidataTests", "wikidataIdentifiersWrong",
			0, errorCount, "Wikidata identifiers that do not start with a 'Q' followed by a number: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> chemspiderCIDWithoutMapping(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteChemspider.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("WikidataTests", "chemspiderCIDWithoutMapping", table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals("WikidataTests", "chemspiderCIDWithoutMapping",
			0, table.getRowCount(), "Chemspider identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

}
