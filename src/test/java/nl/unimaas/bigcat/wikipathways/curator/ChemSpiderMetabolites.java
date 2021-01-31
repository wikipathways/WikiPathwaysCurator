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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChemSpiderMetabolites {

	@SuppressWarnings({ "serial" })
	private static final Map<String,String> deprecated = new HashMap<String,String>();

	static {
		// See BridgeDb Tiwid: https://github.com/bridgedb/tiwid, doi:10.5281/zenodo.4479409
		String tiwidData = ResourceHelper.resourceAsString("tiwid/chemspider.csv");
		BufferedReader reader = new BufferedReader(new StringReader(tiwidData));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) continue;
				String fields[] = line.split(",");
				if (fields.length >= 2) {
					deprecated.put(fields[0], fields[2]);
				} else {
					deprecated.put(fields[0], null);
				}
			}
		} catch (IOException e) {
			// blah
		}
	}

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
	public void outdatedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allChemSpiderIdentifiers.rq");
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
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
						Integer csid = Integer.valueOf(identifier);
						if (deprecated.containsKey("" + csid)) {
							errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " +table.get(i, "identifier");
							errorCount++;
						}
					} catch (NumberFormatException exception) {
						// ignore, we got chemSpiderIDsNotNumbers() for this
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Deprecated ChemSpider identifiers for non-metabolites:\n" + errors
			);
		});
	}

	@Test
	public void chemSpiderIDsNotNumbers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allChemSpiderIdentifiers.rq");
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
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
				0, errorCount, "ChemSpider identifiers that are not integers:\n" + errors
			);
		});
	}
}
