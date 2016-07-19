/* Copyright (C) 2015  Egon Willighagen <egon.willighagen@gmail.com>
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
import org.junit.Ignore;
import org.junit.Test;

import org.apache.jena.rdf.model.Model;

public class EnsemblGenes {

	@BeforeClass
	public static void loadData() throws InterruptedException {
		OPSWPRDFFiles.loadData();
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);
	}

	@Test(timeout=50000)
	public void wrongEnsemblIDForHumanSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Human.rq");
		Assert.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for human for: " + System.getProperty("SUBSETPREFIX"));
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
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
			"Ensembl identifiers for wrong species for a human pathway:\n" + table,
			0, errorCount
		);
	}

	@Test(timeout=50000)
	public void wrongEnsemblIDForRatSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Rat.rq");
		Assert.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for rat for: " + System.getProperty("SUBSETPREFIX"));
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
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
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
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
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a cow pathway:\n" + table,
			0, table.getRowCount()
		);
	}

	@Ignore("Too many false positives at this moment")
	@Test(timeout=50000)
	public void outdatedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/possiblyOutdatedEnsemblIdentifiers.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Outdated Ensembl identifiers:\n" + table, 0, table.getRowCount());
	}

}
