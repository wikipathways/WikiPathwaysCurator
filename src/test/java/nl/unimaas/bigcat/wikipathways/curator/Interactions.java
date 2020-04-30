/* Copyright (C) 2016,2018  Egon Willighagen <egon.willighagen@gmail.com>
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

public class Interactions {

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
	public void noMetaboliteToNonMetaboliteConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noMetaboliteNonMetaboliteConversions.rq");
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Set<String> allowedProteinProducts = new HashSet<String>();
			allowedProteinProducts.add("http://identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
			allowedProteinProducts.add("http://identifiers.org/chebi/CHEBI:39026"); // LPL
			String errors = "";
			int errorCount = 0;
			if (table.getRowCount() > 0) {
				// OK, but then it must be proteins, e.g. IFN-b
				for (int i=1; i<=table.getRowCount(); i++) {
					String targetID = table.get(i, "target");
					if (!allowedProteinProducts.contains(targetID)) {
						errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
								table.get(i, "metabolite") + " " + table.get(i, "target") + " " +
								table.get(i, "interaction") + "\n";
						errorCount++;
					} // else, OK, this is allows as conversion target
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Unexpected metabolite to non-metabolite conversions:\n" + errors
			);
		});
	}

	@Test
	public void noNonMetaboliteToMetaboliteConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noNonMetaboliteMetaboliteConversions.rq");
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Set<String> allowedProducts = new HashSet<String>();
			allowedProducts.add("http://identifiers.org/hmdb/HMDB04246"); // from KNG1, e.g. in WP
			allowedProducts.add("http://identifiers.org/hmdb/HMDB0004246"); // from KNG1, e.g. in WP
			allowedProducts.add("http://identifiers.org/hmdb/HMDB0061196"); // angiotensin, a peptide hormone
			Set<String> allowedProteinSubstrates = new HashSet<String>();
			allowedProteinSubstrates.add("http://identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
			allowedProteinSubstrates.add("http://identifiers.org/chebi/CHEBI:39026"); // LDL
			allowedProteinSubstrates.add("http://identifiers.org/wikidata/Q381899"); // fibrinogen
			String errors = "";
			int errorCount = 0;
			if (table.getRowCount() > 0) {
				// OK, but then it must be proteins, e.g. IFN-b
				for (int i=1; i<=table.getRowCount(); i++) {
					String metabolite = table.get(i, "metabolite");
					String nonmetabolite = table.get(i, "target");
					if (!allowedProducts.contains(metabolite) &&
							!allowedProteinSubstrates.contains(nonmetabolite)) {
						errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
								nonmetabolite + " " + metabolite + " " +
								table.get(i, "interaction") + "\n";
						errorCount++;
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Unexpected non-metabolite to metabolite conversions:\n" + errors
			);
		});
	}

	@Test
	public void noGeneProteinConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noGeneProteinConversions.rq");
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Set<String> allowedProteinSubstrates = new HashSet<String>();
			allowedProteinSubstrates.add("http://identifiers.org/uniprot/P0DTD1"); // SARS-CoV-2 main protease
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
					}
					errorCount++;
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Unexpected gene-protein conversions:\n" + errors
			);
		});
	}

	@Test
	public void noProteinProteinConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noProteinProteinConversions.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
				  table.get(i, "protein1") + " " + table.get(i, "protein2") + " " +
				  table.get(i, "interaction") + "\n";
				errorCount++;
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Unexpected protein-protein conversions:\n" + errors
		);
	}

	@Test
	public void nonNumericIDs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/nonNumericRhea.rq");
		Assertions.assertTimeout(Duration.ofSeconds(50), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			String errors = "";
			if (table.getRowCount() > 0) {
				for (int i=1; i<=table.getRowCount(); i++) {
					String id = table.get(i, "id");
					if (id != null && id.length() > 0) {
						try {
							Integer.parseInt(id);
						} catch (NumberFormatException exception) {
							errors += table.get(i, "homepage") + " " +
									table.get(i, "id") + "\n";
						}
					}
				}
			}
			Assertions.assertEquals(
				0, errors.length(), "Found Rhea IDs that are not numbers (they should not include a 'Rhea:' prefix):\n" + errors
			);
		});
	}

	@Tag("curation")
	public void interactionsWithLabels() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/interactionsWithLabels.rq");
		Assertions.assertTimeout(Duration.ofSeconds(50), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			String errors = "";
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
						}
					}
				}
			}
			Assertions.assertEquals(
				0, errors.length(), "Interactions found that involve Labels:\n" + errors
			);
		});
	}
}
