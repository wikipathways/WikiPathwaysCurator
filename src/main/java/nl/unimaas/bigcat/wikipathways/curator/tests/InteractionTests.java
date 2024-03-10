/* Copyright (C) 2013,2018-2024  Egon Willighagen <egon.willighagen@gmail.com>
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class InteractionTests {
	
	private static void addIdentifiersOrg(Set<String> setToAddTo, String identifier) {
		setToAddTo.add("https://" + identifier);
		setToAddTo.add("http://" + identifier);
	}

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(noMetaboliteToNonMetaboliteConversions(helper, format));
		assertions.addAll(noNonMetaboliteToMetaboliteConversions(helper, format));
		assertions.addAll(noGeneProteinConversions(helper, format));
		assertions.addAll(nonNumericIDs(helper, format));
		assertions.addAll(interactionsWithLabels(helper, format));
		assertions.addAll(possibleTranslocations(helper, format));
		assertions.addAll(noProteinProteinConversions(helper, format));
		assertions.addAll(incorrectKEGGIdentifiers(helper, format));
		return assertions;
	}

	public static List<IAssertion> noMetaboliteToNonMetaboliteConversions(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "noMetaboliteToNonMetaboliteConversions", "No metabolite to non-metabolite conversions", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/noMetaboliteNonMetaboliteConversions.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedProteinProducts = new HashSet<String>();
            InteractionTests.addIdentifiersOrg(allowedProteinProducts, "identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
            InteractionTests.addIdentifiersOrg(allowedProteinProducts, "identifiers.org/uniprot/Q42533"); // Biotin carboxyl carrier protein of acetyl-CoA carboxylase 1, chloroplastic
            InteractionTests.addIdentifiersOrg(allowedProteinProducts, "identifiers.org/chebi/CHEBI:39026"); // LPL
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String targetID = table.get(i, "target");
				if (!allowedProteinProducts.contains(targetID)) {
					if ("text/markdown".equals(format)) {
						errors += "* " + table.get(i, "organism") + " " + asMarkdownLink(table.get(i, "pathway")) + " → " +
								asMarkdownLink(table.get(i, "metabolite")) + " " +
								asMarkdownLink(table.get(i, "target")) + " " +
								table.get(i, "interaction") + "<br />\n";
					} else {
						errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " → " +
							table.get(i, "metabolite") + " " + table.get(i, "target") + " " +
							table.get(i, "interaction") + "\n";
					}
					errorCount++;
				} // else, OK, this is allows as conversion target
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected metabolite to non-metabolite conversions:" + errorCount, errors, format
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

	public static List<IAssertion> noNonMetaboliteToMetaboliteConversions(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "noNonMetaboliteToMetaboliteConversions");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/noNonMetaboliteMetaboliteConversions.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedProducts = new HashSet<String>();
		    InteractionTests.addIdentifiersOrg(allowedProducts, "identifiers.org/hmdb/HMDB04246"); // from KNG1, e.g. in WP
		    InteractionTests.addIdentifiersOrg(allowedProducts, "identifiers.org/hmdb/HMDB0004246"); // from KNG1, e.g. in WP
		    InteractionTests.addIdentifiersOrg(allowedProducts, "identifiers.org/hmdb/HMDB0061196"); // angiotensin, a peptide hormone
		    InteractionTests.addIdentifiersOrg(allowedProducts, "identifiers.org/chebi/CHEBI:2718"); // angiotensin, a peptide hormone
		    InteractionTests.addIdentifiersOrg(allowedProducts, "identifiers.org/chebi/CHEBI:64645"); // amyloid-β, a peptide
		    InteractionTests.addIdentifiersOrg(allowedProducts, "identifiers.org/pubchem.compound/3081372"); // angiotensin I, a peptide hormone
		Set<String> allowedProteinSubstrates = new HashSet<String>();
			InteractionTests.addIdentifiersOrg(allowedProteinSubstrates, "identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
            InteractionTests.addIdentifiersOrg(allowedProteinSubstrates, "identifiers.org/uniprot/Q42533"); // Biotin carboxyl carrier protein of acetyl-CoA carboxylase 1, chloroplastic
            InteractionTests.addIdentifiersOrg(allowedProteinSubstrates, "identifiers.org/uniprot/P05067"); // Amyloid-beta precursor protein
			InteractionTests.addIdentifiersOrg(allowedProteinSubstrates, "identifiers.org/chebi/CHEBI:39026"); // LDL
			InteractionTests.addIdentifiersOrg(allowedProteinSubstrates, "identifiers.org/wikidata/Q381899"); // fibrinogen
		Map<String,String> allowedInteractions = new HashMap<String,String>();
			allowedInteractions.put( // CSID1 -> Fe 2+
				"https://identifiers.org/ensembl/ENSG00000122873",
				"https://identifiers.org/chebi/CHEBI:29033"
			);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String metabolite = table.get(i, "metabolite");
				String nonmetabolite = table.get(i, "target");
				if (allowedProducts.contains(metabolite)) {}
				else if (allowedProteinSubstrates.contains(nonmetabolite)) {}
				else if (allowedInteractions.containsKey(nonmetabolite) &&
						allowedInteractions.get(nonmetabolite).equals(metabolite)) {}
				else { // other situations are not okay
					errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
							nonmetabolite + " " + metabolite + " " +
							table.get(i, "interaction") + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected non-metabolite to metabolite conversions: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> noGeneProteinConversions(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "noGeneProteinConversions");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/noGeneProteinConversions.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedProteinSubstrates = new HashSet<String>();
		    InteractionTests.addIdentifiersOrg(allowedProteinSubstrates, "identifiers.org/uniprot/P0DTD1"); // SARS-CoV-2 main protease
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String protein = table.get(i, "protein");
				if (!allowedProteinSubstrates.contains(protein)) {
					errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
							protein + " " + table.get(i, "gene") + " " +
							table.get(i, "interaction") + " Did you mean wp:TranscriptionTranslation?\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected gene-protein conversions: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> nonNumericIDs(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "nonNumericIDs");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/nonNumericRhea.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					try {
						Integer.parseInt(id);
					} catch (NumberFormatException exception) {
						errors += table.get(i, "homepage") + " " +
								table.get(i, "id") + "\n";
						errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Incorrect Rhea IDs: " + errorCount, 
			"Found Rhea IDs that are not numbers (they should not include a 'Rhea:' prefix):\n" + errors
		));
		return assertions;
	}

	public static List<IAssertion> incorrectKEGGIdentifiers(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "incorrectKEGGIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/keggIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					if (id.startsWith("R")) { // all okay
					} else {
						errors += table.get(i, "homepage") + " " +
								table.get(i, "id") + "\n";
						errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Incorrect KEGG reaction IDs: " + errorCount,
			"Found KEGG reaction IDs that do not start with an 'R':\n" + errors
		));
		return assertions;
	}

	public static List<IAssertion> interactionsWithLabels(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "interactionsWithLabels");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/interactionsWithLabels.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					try {
						Integer.parseInt(id);
					} catch (NumberFormatException exception) {
						errors += table.get(i, "homepage") + " \"" +
								table.get(i, "label") + "\" with graphId " +
							table.get(i, "id") + "\n";
						errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Interactions found that involve Labels: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> possibleTranslocations(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "possibleTranslocations", "Possible MIM translocation", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/possibleTranslocations.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedInNonTranslocation = new HashSet<String>();
		allowedInNonTranslocation.add("https://identifiers.org/chebi/CHEBI:17984"); // Acyl-CoA (n-2)
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String source = table.get(i, "source");
				if (!allowedInNonTranslocation.contains(source)) {
                    errors += table.get(i, "homepage") + " " +
						table.get(i, "interaction") + " \"" +
						table.get(i, "sourceLabel") + "\" (" + source + ") and \n" +
						table.get(i, "targetLabel") + "\" (" +
						table.get(i, "target") + ")\n";
				    errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Interactions between identical nodes: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> noProteinProteinConversions(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("InteractionTests", "noProteinProteinConversions");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/noProteinProteinConversions.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedProteinSubstrates = new HashSet<String>();
		allowedProteinSubstrates.add("https://identifiers.org/uniprot/P0DTD1"); // SARS-CoV-2 main protease
		allowedProteinSubstrates.add("https://identifiers.org/uniprot/P0DTC2"); // SARS-CoV-2 spike protein (resulting in S1, S2, S2'
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String protein1 = table.get(i, "protein1");
				if (!allowedProteinSubstrates.contains(protein1)) {
				    errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
				    		protein1 + " " + table.get(i, "protein2") + " " +
				        table.get(i, "interaction") + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected protein-protein conversions: " + errorCount, errors
		));
		return assertions;
	}

}
