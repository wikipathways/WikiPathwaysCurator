/* Copyright (C) 2025  Egon Willighagen <egon.willighagen@gmail.com>
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
import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class Sec2PriProjectTests {

	private static final Map<String,String> deprecated2 = BridgeDbTiwidReader.parseTSV("outdated/HGNC_secID2priID.tsv", 3, 1);
	private static final Map<String,String> deprecated3 = BridgeDbTiwidReader.parseTSV("outdated/HGNC_secID2priID.tsv", 2, 0);
	private static final Map<String,String> deprecated4 = BridgeDbTiwidReader.parseTSV("outdated/NCBI_secID2priID.tsv", 1, 0);

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(outdatedIdentifiers2(helper, format));
		assertions.addAll(outdatedIdentifiers3(helper, format));
		assertions.addAll(outdatedNCBIIdentifiers(helper, format));
		return assertions;
	}

	public static List<IAssertion> outdatedIdentifiers2(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneTests", "outdatedIdentifiers2", "HGNC Symbol has been retracted", false);
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allHGNCIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (deprecated2.containsKey(identifier)) {
					if ("text/markdown".equals(format)) {
					    errors += "* " + asMarkdownLink(table.get(i, "homepage")) + " " + table.get(i, "label").replace('\n', ' ') +
						    " has [" + identifier + "](https://bioregistry.io/hgnc.symbol:" + identifier + ") but has been replaced by " +
						    deprecated2.get(identifier) + "\n";
					} else {
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
							    " has " + identifier + " but has been replaced by " +
							    deprecated2.get(identifier) + "\n";
					}
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Old HGNC Symbol detected: " + errorCount, errors, format
		));
		return assertions;
	}


	public static List<IAssertion> outdatedIdentifiers3(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneTests", "outdatedIdentifiers3", "HGNC Accession number has been retracted", false);
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allHGNCAccessionIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = "HGNC:" + table.get(i, "identifier");
				if (deprecated3.containsKey(identifier)) {
					if ("text/markdown".equals(format)) {
					    errors += "* " + asMarkdownLink(table.get(i, "homepage")) + " " + table.get(i, "label").replace('\n', ' ') +
					    	" has HGNC Accession [" + identifier + "](https://bioregistry.io/hgnc:" + identifier + ") but has been replaced by " +
						    deprecated3.get(identifier) + "\n";
					} else {
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						    	" has HGNC Accession " + identifier + " but has been replaced by " +
							    deprecated3.get(identifier) + "\n";
					}
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Old HGNC Accession numbers detected: " + errorCount, errors, format
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

	public static List<IAssertion> outdatedNCBIIdentifiers(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneTests", "outdatedNCBIIdentifiers", "NCBI Gene identifier has been retracted", false);
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		if (deprecated4.size() == 0) return assertions; // data file did not load, so no fails. the file is large and doesn't fit the repo
		
		String sparql = ResourceHelper.resourceAsString("genes/allEntrezGenesIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (deprecated4.containsKey(identifier)) {
					if ("text/markdown".equals(format)) {
					    errors += "* " + asMarkdownLink(table.get(i, "homepage")) + " " + table.get(i, "label").replace('\n', ' ') +
					    	" has NCBI Gene [" + identifier + "](https://bioregistry.io/ncbigene:" + identifier + ") but has been replaced by " +
						    deprecated4.get(identifier) + "\n";
					} else {
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						    	" has NCBI Gene " + identifier + " but has been replaced by " +
							    deprecated4.get(identifier) + "\n";
					}
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Old NCBI Gene identifiers detected: " + errorCount, errors, format
		));
		return assertions;
	}

}
