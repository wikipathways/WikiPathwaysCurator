/* Copyright (C) 2021  Egon Willighagen <egon.willighagen@gmail.com>
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
import java.util.List;
import java.util.Map;

import nl.unimaas.bigcat.wikipathways.curator.BridgeDbTiwidReader;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class PathwayTests {

	@SuppressWarnings({ "serial" })
	private static final Map<String,String> deprecated = BridgeDbTiwidReader.parseCSV("tiwid/wikipathways.csv");

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(deletedPathways(helper));
		assertions.addAll(linksToDeletedPathways(helper));
		return assertions;
	}

	public static List<IAssertion> deletedPathways(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" + 
				"PREFIX wp:      <http://vocabularies.wikipathways.org/wp#>\n" +
				"prefix xsd:     <http://www.w3.org/2001/XMLSchema#>\n\n"
				+ "SELECT ?pathway WHERE {\n  VALUES ?wpid { \n";
		for (String deprecatedPW : deprecated.keySet()) {
			sparql += "    \"" + deprecatedPW + "\"^^xsd:string\n";
		}
		sparql += "  }\n"
				+ "  ?pathway dcterms:identifier ?wpid .\n"
				+ "  MINUS { ?pathway a wp:DataNode } \n}";
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("PathwayTests", "deletedPathways", table));
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String pathway = table.get(i, "pathway");
				errors += pathway + " \n";
			}
		}
		assertions.add(new AssertEquals("PathwayTests", "deletedPathways",
			0, table.getRowCount(), "Found " + table.getRowCount() + " deleted pathways", errors
		));
		return assertions;
	}

	public static List<IAssertion> linksToDeletedPathways(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" + 
				"prefix xsd:     <http://www.w3.org/2001/XMLSchema#>\n" +
				"prefix foaf:    <http://xmlns.com/foaf/0.1/>\n" +
				"prefix wp:      <http://vocabularies.wikipathways.org/wp#>\n\n"
				+ "SELECT ?homepage ?wpid WHERE {\n  VALUES ?wpid { \n";
		for (String deprecatedPW : deprecated.keySet()) {
			sparql += "    \"" + deprecatedPW + "\"^^xsd:string\n";
		}
		sparql += "  }\n"
				+ "  ?pathwayNode a wp:DataNode ;\n" + 
				  "    dcterms:identifier ?wpid ;\n" + 
				  "    dcterms:isPartOf ?pathway .\n" + 
				  "  ?pathway foaf:page ?homepage . \n}";
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("PathwayTests", "linksToDeletedPathways", table));
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String pathway = table.get(i, "homepage");
				String replacement = deprecated.get(table.get(i, "wpid"));
				errors += pathway + " links to a deleted pathway: " + table.get(i, "wpid") +
						(replacement != null ? " (check " + replacement + " as replacement)" : "") + "\n";
			}
		}
		assertions.add(new AssertEquals("PathwayTests", "deletedPathways",
			0, table.getRowCount(), "Found " + table.getRowCount() + " pathways that link to deleted pathways", errors
		));
		return assertions;
	}

}
