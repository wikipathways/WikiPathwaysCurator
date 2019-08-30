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

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Genes {

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
	public void waitForIt() throws InterruptedException { Thread.sleep(200); }

	@Test
	public void entrezGeneIdentifiersNotNumber() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/allEntrezGenesIdentifiers.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				String pathwayPage = table.get(i, "homepage");
				if (!pathwayPage.contains("WP2806")) { // this pathway has a number of non-human genes
					identifier = identifier.trim();
					if (!identifier.isEmpty()) {
						try {
							Integer.parseInt(identifier);
						} catch (NumberFormatException exception) {
							errors += table.get(i, "homepage") + " -> " + table.get(i, "label") +
									", " + table.get(i, "identifier") + "\n ";
							errorCount++;
						}
					}
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Entrez Gene identifiers that are not numbers:\n" + errors
		);
	}

	@Test
	public void affyProbeIdentifiersNotCorrect() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/allAffyProbeIdentifiers.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assertions.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				String pathwayPage = table.get(i, "homepage");
				if (!pathwayPage.contains("WP2806")) { // this pathway has a number of non-human genes
					identifier = identifier.trim();
					if (!identifier.isEmpty()) {
						if (identifier.contains("CHEBI:")) {
							errors += table.get(i, "homepage") + " -> " + table.get(i, "label") +
									", " + table.get(i, "identifier") + "\n ";
							errorCount++;
						} else if (identifier.contains("ENS:")) {
							errors += table.get(i, "homepage") + " -> " + table.get(i, "label") +
									", " + table.get(i, "identifier") + "\n ";
							errorCount++;
						}
					}
				}
			}
		}
		Assertions.assertEquals(
			0, errorCount, "Affy Probe identifiers that do not look right:\n" + errors
		);
	}
}
