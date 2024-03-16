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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.BridgeDbTiwidReader;
import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class ReferencesTests {

	// this is list of PubMed identifiers that have been replaced (e.g. because of duplicate PubMed entries)
	private static final Map<String,String> deprecated = BridgeDbTiwidReader.parseTSV("tiwid/pubmed.tsv");

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(nonNumericPubMedIDs(helper, format));
		assertions.addAll(unexpectedPubMedIdentifier(helper, format));
		assertions.addAll(zeroPubMedIDs(helper, format));
		assertions.addAll(atLeastOneReference(helper, format));
		assertions.addAll(citesRetractedArticle(helper, format));
		assertions.addAll(outdatedPubMedIdentifiers(helper, format));
		return assertions;
	}

	public static List<IAssertion> nonNumericPubMedIDs(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("ReferencesTests", "nonNumericPubMedIDs");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("references/nonNumericPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					try {
						Integer.parseInt(id);
					} catch (NumberFormatException exception) {
						if (!id.trim().equals("26056233")) {
						errors += table.get(i, "homepage") + " '" +
								id + "' (reason: " + exception.getMessage() + ")\n";
						errorCount++;
						}
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found PubMed IDs that are not numbers: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> unexpectedPubMedIdentifier(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("ReferencesTests", "unexpectedPubMedIdentifier");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("references/nonNumericPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedIdentifiers = new HashSet<String>();
		allowedIdentifiers.add("0716730510"); // actually, an ISBN number but not recognized as such (bc bp:ID not accessible in old GPML reader)
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0 && !allowedIdentifiers.contains(id.trim())) {
					try {
						int pmid = Integer.parseInt(id);
						if (pmid >= 50000000) {
						    errors += table.get(i, "homepage") + " '" +
							    id + "' is higher than expected\n";
						    errorCount++;
						}
					} catch (NumberFormatException exception) {}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found PubMed IDs with unexpected high value: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> zeroPubMedIDs(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("ReferencesTests", "zeroPubMedIDs");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("references/nonNumericPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					try {
						if (Integer.parseInt(id) == 0) {
							errors += table.get(i, "homepage") + ", '" +
									table.get(i, "id") + "'\n";
							errorCount++;
						}
					} catch (NumberFormatException exception) {
						// already reporting these in nonNumericPubMedIDs()
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found '0's as PubMed IDs: " + errorCount, errors
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

    public static List<IAssertion> atLeastOneReference(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("ReferencesTests", "atLeastOneReference", "At least one reference", true);
    	List<IAssertion> assertions = new ArrayList<>();
    	String sparql = ResourceHelper.resourceAsString("missing/atLeastOneReference.rq");
    	StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				if ("text/markdown".equals(format)) {
				    errors += "* " + asMarkdownLink(table.get(i, "homepage")) + " '" +
	  					    table.get(i, "title") + "' in " +
						    table.get(i, "species") + " has zero references; \n";
				} else {
				    errors += table.get(i, "homepage") + " '" +
  					    table.get(i, "title") + "' in " +
					    table.get(i, "species") + " has zero references; \n";
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Found " + table.getRowCount() + " pathways with zero (PubMed) references", errors
		));
		return assertions;
    }

	public static List<IAssertion> citesRetractedArticle(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("ReferencesTests", "citesRetractedArticle");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("references/nonNumericPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					if (deprecated.containsKey(id) && deprecated.get(id) == null) {
					    errors += table.get(i, "homepage") + " cites PubMed ID " +
								table.get(i, "id") + "\n";
						errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found pathways citing retracted articles: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> outdatedPubMedIdentifiers(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("ReferencesTests", "outdatedPubMedIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("references/nonNumericPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "id");
				if (deprecated.containsKey(identifier) && deprecated.get(identifier) != null) {
					errors += table.get(i, "homepage") + " " + table.get(i, "id") +
						  " is deprecated and possibly replaced by " + deprecated.get(identifier) + "; \n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Deprecated PubMed identifiers: " + errorCount, errors
		));
		return assertions;
	}
}
