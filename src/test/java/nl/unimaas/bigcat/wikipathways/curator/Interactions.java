/* Copyright (C) 2016,2018-2020  Egon Willighagen <egon.willighagen@gmail.com>
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
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.tests.InteractionTests;

public class Interactions extends JUnitTests {

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
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			List<IAssertion> assertions = InteractionTests.noMetaboliteToNonMetaboliteConversions(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void noNonMetaboliteToMetaboliteConversions() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:")) 
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			List<IAssertion> assertions = InteractionTests.noNonMetaboliteToMetaboliteConversions(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void noGeneProteinConversions() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:")) 
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			List<IAssertion> assertions = InteractionTests.noGeneProteinConversions(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void noProteinProteinConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noProteinProteinConversions.rq");
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
				String protein1 = table.get(i, "protein1");
				if (!allowedProteinSubstrates.contains(protein1)) {
				    errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
				    		protein1 + " " + table.get(i, "protein2") + " " +
				        table.get(i, "interaction") + "\n";
					errorCount++;
				}
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

	@Tag("expertCuration")
	@Test
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
