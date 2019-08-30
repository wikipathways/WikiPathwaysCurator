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

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class UniProt {

	@SuppressWarnings({ "serial" })
	private static final Map<String,String> deprecated = new HashMap<String,String>() {{ //Secondary IDs; website contains replacement info
		// this map shows the deprecated and better UniProt identifier. The syntax below is:
		// put(deprecated, replacement)
        put("B3KP27", "P15408");
        put("O43884","???"); // TODO
	}};
	
	@SuppressWarnings({ "serial" })
	private static final Set<String> unreviewed = new HashSet<String>() {{ //Unreviewed IDs; website doesn't contains replacement info
        add("O60411");
        add("O95220");
	}};

	@SuppressWarnings({ "serial" })
	private static final Set<String> deleted = new HashSet<String>() {{ //Removed IDs; website doesn't contains replacement info
        add("B5MEC1");
        add("P47886");
        add("P47892");
        add("Q9UDD7");
        add("Q9UDD8");
	}};

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
			String parseErrors = OPSWPRDFFiles.getParseErrors();
			Assertions.assertNotNull(parseErrors);
			Assertions.assertEquals(0, parseErrors.length(), parseErrors.toString());
		}
	}

	@BeforeEach
	public void waitForIt() throws InterruptedException { TimeUnit.MICROSECONDS.wait(500); }

	@Tag("outdated")
	@Test
	public void outdatedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtIdentifiers.rq");
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
					if (deprecated.containsKey(identifier)) {
						errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
							  " is deprecated and possibly replaced by " + deprecated.get(identifier) + "; \n";
						errorCount++;
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Deprecated UniProt identifiers:\n" + errors
			);
		});
	}

	@Tag("expertCuration")
	@Test
	public void unreviewedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtIdentifiers.rq");
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
					if (unreviewed.contains(identifier)) {
						errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
							  " is unreviewed, please visit Uniprot for (potential) reviewed version; \n";
						errorCount++;
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Unreviewed UniProt identifiers:\n" + errors
			);
		});
	}
	
	
	
	@Test
	public void deletedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtIdentifiers.rq");
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
					if (deleted.contains(identifier)) {
						errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
							  " is deleted; \n";
						errorCount++;
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Deleted UniProt identifiers:\n" + errors
			);
		});
	}
}
