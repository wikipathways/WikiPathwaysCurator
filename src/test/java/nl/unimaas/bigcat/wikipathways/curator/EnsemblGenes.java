/* Copyright (C) 2015,2018  Egon Willighagen <egon.willighagen@gmail.com>
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

public class EnsemblGenes {

	@SuppressWarnings({ "serial" })
	private static final Map<String,String> deprecated = new HashMap<String,String>() {{
		put("ENSG00000199004", "ENSG00000284190"); // old MIR21 identifier
		put("ENSG00000132142", "ENSG00000132142.1");
		put("ENSG00000011177", "ENSG00000011177.5");
		put("ENSG00000079782", "ENSG00000079782.8");
		put("ENSG00000096087", "ENSG00000096087.5");
		put("ENSG00000105663", "ENSG00000105663.1");
		put("ENSG00000172070", "ENSG00000172070.5");
		put("ENSG00000175818", "ENSG00000175818.6");
		put("ENSG00000184674", "ENSG00000184674.8");
		put("ENSG00000187919", "ENSG00000187919.5");
		put("ENSG00000197592", "ENSG00000197592.3");
		put("ENSG00000198339", "ENSG00000198339.3");
		put("ENSG00000198981", "ENSG00000198981.2");
		put("ENSG00000199004", "ENSG00000199004.1");
		put("ENSG00000199066", "ENSG00000199066.1");
		put("ENSG00000199097", "ENSG00000199097.1");
		put("ENSG00000283147", "ENSG00000283147.1");
	}};

	@BeforeClass
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assert.assertTrue(data.size() > 5000);
		}
	}

	@Test(timeout=20000)
	public void outdatedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/allEnsemblIdentifiers.rq");
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getColumnCount());
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (deprecated.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
							  " is deprecated and possibly replaced by " + deprecated.get(identifier) + "; ";
					errorCount++;
				}
			}
		}
		Assert.assertEquals(
			"Deprecated Ensembl identifiers:\n" + errors,
			0, errorCount
		);
	}

	@Test(timeout=50000)
	public void wrongEnsemblIDForHumanSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Human.rq");
		Assert.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for human for: " + System.getProperty("SUBSETPREFIX"));
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (!identifier.contains("LRG_482")) { // in pathway WP3658
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
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a human pathway:\n" + errors,
			0, errorCount
		);
	}

	@Test(timeout=50000)
	public void wrongEnsemblIDForRatSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Rat.rq");
		Assert.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for rat for: " + System.getProperty("SUBSETPREFIX"));
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a rat pathway:\n" + table,
			0, table.getRowCount()
		);
	}

	@Test(timeout=50000)
	public void wrongEnsemblIDForMouseSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Mouse.rq");
		Assert.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for mouse for: " + System.getProperty("SUBSETPREFIX"));
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				String pathwayPage = table.get(i, "homepage");
				if (!pathwayPage.contains("WP2080") && // this pathway has a few of human mirs
					!pathwayPage.contains("WP2087") && // this pathway has a few of human mirs
					!pathwayPage.contains("WP3632")) { // this pathway has a few human genes (pinged Freddie)
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
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a mouse pathway:\n" + errors,
			0, errorCount
		);
	}

	@Test(timeout=50000)
	public void wrongEnsemblIDForCowSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Cow.rq");
		Assert.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for cow for: " + System.getProperty("SUBSETPREFIX"));
		StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a cow pathway:\n" + table,
			0, table.getRowCount()
		);
	}

}
