/* Copyright (C) 2018  Egon Willighagen <egon.willighagen@gmail.com>
 * Copyright (C) 2018  Denise Slenter <dslentermsc@gmail.com>
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class HMDBSecMetabolites {

	private static Map<String,String> oldToNew = new HashMap<String, String>();
	private static Set<String> nonExisting = new HashSet<String>();

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
		}

		// now load the deprecation data
		String deprecatedData = ResourceHelper.resourceAsString("metabolite/hmdb/HMDBDataSecondaryAll_Final.csv");
		String lines[] = deprecatedData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			oldToNew.put(ids[0], ids[1]);
		}
		System.out.println("size: " + oldToNew.size());

		nonExisting.add("HMDB0002708"); // "How did you get here? That page doesn't exist. Oh well, it happens."
	}

	@Test
	public void outdatedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allHMDBIdentifiers.rq");
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			String errors = "";
			int errorCount = 0;
			Assertions.assertNotSame(0, table.getRowCount(), "I expected more than zero HMDB identifiers.");
			if (table.getRowCount() > 0) {
				for (int i=1; i<=table.getRowCount(); i++) {
					String identifier = table.get(i, "identifier");
					if (identifier.length() == 11) identifier.replace("HMDB00", "HMDB");
					if (oldToNew.containsKey(identifier)) {
						String primID = oldToNew.get(identifier);
						if (!primID.equals(identifier.replace("HMDB", "HMDB00"))) { // ignore longer format
							errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
									" has " + identifier + " but has primary identifier " + primID + "\n";
							errorCount++;
						}
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Secondary HMDB identifiers detected:\n" + errors
			);
		});
	}

	@Test
	public void nonExisting() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allHMDBIdentifiers.rq");
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			String errors = "";
			int errorCount = 0;
			Assertions.assertNotSame(0, table.getRowCount(), "I expected more than zero HMDB identifiers.");
			if (table.getRowCount() > 0) {
				for (int i=1; i<=table.getRowCount(); i++) {
					String identifier = table.get(i, "identifier");
					if (nonExisting.contains(identifier)) {
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
								" has " + identifier + " but it does not exist\n";
						errorCount++;
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Non-existing HMDB identifiers detected:\n" + errors
			);
		});
	}

	@Tag("outdated")
	@Test
	public void oldFormat() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allHMDBIdentifiersGPML.rq");
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			String errors = "";
			int errorCount = 0;
			Assertions.assertNotSame(0, table.getRowCount(), "I expected more than zero HMDB identifiers.");
			if (table.getRowCount() > 0) {
				for (int i=1; i<=table.getRowCount(); i++) {
					String identifier = table.get(i, "identifier");
					if (identifier.length() < 11) {
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
								" has " + identifier + " but the new format is " + identifier.replace("HMDB", "HMDB00") + "\n";
						errorCount++;
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Old HMDB identifier format detected:\n" + errors
			);
		});
	}
}
