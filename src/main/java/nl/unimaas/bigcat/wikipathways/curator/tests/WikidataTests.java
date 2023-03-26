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
import java.util.Map;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.BridgeDbTiwidReader;
import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class WikidataTests {

	@SuppressWarnings("serial")
	private static Set<String> acceptableWikidataGenes = new HashSet<String>()
	{{
		add("Q27205");   // Protein Fibrin
		add("Q311213");  // Protein HbA1c
		add("Q381899");  // Protein Fibrinogen
		add("Q2162109"); // Protein D-dimer
	}};

	@SuppressWarnings("serial")
	private static Set<String> noWarnKEGGCIDs = new HashSet<String>()
	{{  // ACPs (acyl-carrier proteins)
		add("C04688");
		add("C05729");
		add("C05746");
		add("C05747");
		add("C05748");
		add("C05749");
		add("C05750");
		add("C05751");
		add("C05752");
		add("C05753");
		add("C05754");
		add("C05755");
		add("C05757");
		add("C05758");
		add("C05759");
		add("C05759");
		add("C05761");
		add("C05761");
		add("C05762");
		add("C05763");
		add("C05764");
		add("C16255");
		add("C00173");
		add("C16239");
		// things with R groups
		add("C16254");
		add("C15973");
		add("C06250");
		add("C16236");
		add("C16237");
	}};

	@SuppressWarnings("serial")
	private static Set<String> zwitterIonsWithoutWikidata = new HashSet<String>()
	{{
		add("CHEBI:33384"); // L-serine zwitterion
		add("CHEBI:57476"); // L-homoserine zwitterion
		add("CHEBI:57427"); // L-leucine zwitterion
		add("CHEBI:57743"); // Citrulline zwitterion
		add("CHEBI:57844"); // L-Methionine zwitterion
		add("CHEBI:57972"); // L-alanine zwitterion
		add("CHEBI:58045"); // L-isoleucine zwitterion
		add("CHEBI:58048"); // L-asparagine zwitterion
		add("CHEBI:58199"); // L-homocystein zwitterion
		add("CHEBI:58315"); // L-tyrosine zwitterion
		add("CHEBI:58359"); // L-glutamine zwitterion
		add("CHEBI:60039"); // L-proline zwitterion
	}};

	private static final Map<String,String> retired = BridgeDbTiwidReader.parseCSV("tiwid/wikidata.csv");

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(keggWithoutMapping(helper));
		assertions.addAll(pubchemCIDWithoutMapping(helper));
		assertions.addAll(hmdbWithoutMapping(helper));
		assertions.addAll(casWithoutMapping(helper));
		assertions.addAll(wikDataTypo(helper));
		assertions.addAll(duplicateWikidataMappings(helper));
		assertions.addAll(wikidataIdentifiersWrong(helper));
		assertions.addAll(chemspiderCIDWithoutMapping(helper));
		assertions.addAll(lipidMapsWithoutMapping(helper));
		assertions.addAll(kNApSAcKWithoutMapping(helper));
		assertions.addAll(replaceWikipedia(helper));
		assertions.addAll(chebiWithoutMapping_Reactome(helper));
		assertions.addAll(chebiWithoutMapping(helper));
		assertions.addAll(inchikeyWithoutMapping(helper));
		assertions.addAll(retiredIdentifiers(helper));
		return assertions;
	}

	public static List<IAssertion> chebiWithoutMapping_Reactome(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "chebiWithoutMapping_Reactome");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteChEBI_Reactome.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "ChEBI identifiers without Wikidata mappings: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> chebiWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "chebiWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteChEBI.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "ChEBI identifiers without Wikidata mappings: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> keggWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "keggWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteKEGG.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "KEGG Compound identifiers without Wikidata mappings: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> pubchemCIDWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "pubchemCIDWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metabolitePubChemCID.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "PubChem-compound identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> hmdbWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "hmdbWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteHMDB.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "HMDB identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> inchikeyWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "hmdbWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteInChIKey.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "inchikey") + "\t" + table.get(i, "label") + "\n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "InChIKey identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> casWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "casWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteCAS.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "CAS identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> wikDataTypo(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "wikDataTypo");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/wikidata.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Typo 'Wikdata' data sources (use 'Wikidata'): " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> duplicateWikidataMappings(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "duplicateWikidataMappings");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/duplicateWikidata.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "More than one Wikidata identifier for: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> noWikidataForGenes(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "noWikidataForGenes");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/noWikidataYet.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "Wikidata identifiers cannot be used for GeneProduct or Protein yet: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wikidataIdentifiersWrong(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "wikidataIdentifiersWrong");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allWikidataIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "Wikidata identifiers that do not start with a 'Q' followed by a number: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> chemspiderCIDWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "chemspiderCIDWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteChemspider.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Chemspider identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> lipidMapsWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "lipidMapsWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteLipidMaps.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String lmid = table.get(i, "metabolite");
				if (lmid.startsWith("https://identifiers.org/lipidmaps/LMSP0502") ||
					lmid.startsWith("https://identifiers.org/lipidmaps/LMSP0506") ||
					lmid.startsWith("https://identifiers.org/lipidmaps/LMSP0601") ||
					lmid.startsWith("https://identifiers.org/lipidmaps/LMSP0602")) {
					// ignore a few glycosphingolipids for now
				} else {
					errorCount++;
				    errors +=  lmid + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "LIPID MAPS identifiers without Wikidata mappings: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> kNApSAcKWithoutMapping(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "kNApSAcKWithoutMapping");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/wikidata/metaboliteKNApSAcK.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "metabolite") + " (" + table.get(i, "label") + ") "
					    + "does not have a Wikidata mapping in " + table.get(i, "homepage") + " ; \n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "KNApSAcK identifiers without Wikidata mappings: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> replaceWikipedia(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "replaceWikipedia");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/wikipedia.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "node") +
					" can be replaced by the matching Wikidata identifier; \n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Wikipedia identifies that can be replaced by Wikidata identifiers: " + table.getRowCount(), errors
		));
		return assertions;
	}

	public static List<IAssertion> retiredIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("WikidataTests", "retiredIdentifiers");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/allWikidata.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;

		// Testing
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (retired.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " but it has been replace" +
					    (retired.get(identifier) != null ? " by " + retired.get(identifier) : "") +
					    "\n";
					errorCount++;
				}
			}
		}

		// Reporting
		assertions.add(new AssertEquals(test, false,
			0, errorCount, "Retired Wikidata identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

}
