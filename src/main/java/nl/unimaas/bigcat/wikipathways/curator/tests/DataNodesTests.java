/* Copyright (C) 2013,2018-2021  Egon Willighagen <egon.willighagen@gmail.com>
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

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class DataNodesTests {

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(dataNodesWithoutIdentifier(helper, format));
		assertions.addAll(unknownTypes_knownDatasource(helper, format));
		assertions.addAll(unknownTypes(helper, format));
		assertions.addAll(unknownTypes_Reactome(helper, format));
		assertions.addAll(otherDataSource(helper, format));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

	public static List<IAssertion> dataNodesWithoutIdentifier(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("DataNodesTests", "dataNodesWithoutIdentifier", "Data nodes without an identifier", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/dataNodesWithoutIdentifier.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				if ("text/markdown".equals(format)) {
					errors += asMarkdownLink(table.get(i, "homepage")) + " " +
						       table.get(i, "node") + " (" +
							   table.get(i, "label") + ")\n";
				} else {
					errors += table.get(i, "homepage") + " " +
				       table.get(i, "node") + " (" +
					   table.get(i, "label") + ")\n";
				}
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "The following DataNodes have no identifier: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> unknownTypes_knownDatasource(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("DataNodesTests", "unknownTypes_knownDatasource", "Data nodes with type 'Unknown'", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/unknownTypeKnownDatasource.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String datasource = table.get(i, "datasource");
				// Wikidata can be any type and does not quality as "known source"
				if (!"Wikidata".equals(datasource)) {
                    errors += table.get(i, "homepage") + " " +
					    table.get(i, "node") + " (" + datasource + ")\n";
				    errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "The following DataNodes have Unknown @Type: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> unknownTypes_Reactome(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("DataNodesTests", "unknownTypes_Reactome");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/unknownType_Reactome.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "homepage") + " " +
					table.get(i, "node") + " (" +
					table.get(i, "label") + ")\n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "The following DataNodes have Unknown @Type: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> unknownTypes(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("DataNodesTests", "unknownTypes");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/unknownType.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String label = table.hasColumn("label") ? table.get(i, "label") : "null";
				errors += table.get(i, "homepage") + " " +
					table.get(i, "node") + " (" + label + ")\n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "The following DataNodes have Unknown @Type: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> otherDataSource(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("DataNodesTests", "otherDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("missing/dataNodesOtherDataSource.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String label = table.hasColumn("label") ? table.get(i, "label") : "null";
				errors += table.get(i, "homepage") + " " +
					table.get(i, "node") + " (" + label + ")\n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "The following DataNodes have a 'Other' datasource: " + errorCount, errors
		));
		return assertions;
	}
}
