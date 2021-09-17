/* Copyright (C) 2015,2018,2021  Egon Willighagen <egon.willighagen@gmail.com>
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

public class UniProtTests {

	private static final Map<String,String> deprecated = BridgeDbTiwidReader.parseCSV("tiwid/uniprot.csv");

	@SuppressWarnings({ "serial" })
	private static final Set<String> unreviewed = new HashSet<String>() {{ //Unreviewed IDs; website doesn't contains replacement info
        add("O60411");
        add("O95220");
	add("A6NMV7");
	add("A0A024RB99");
	add("C9JNK1");
	}};

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(outdatedIdentifiers(helper));
		assertions.addAll(deletedIdentifiers(helper));
		assertions.addAll(unreviewedIdentifiers(helper));
		assertions.addAll(incorrectIdentifiers(helper));
		assertions.addAll(allP62805(helper));
		return assertions;
	}

	public static List<IAssertion> outdatedIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("UniProtTests", "outdatedIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (deprecated.containsKey(identifier) && deprecated.get(identifier) != null) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
						  " is deprecated and possibly replaced by " + deprecated.get(identifier) + "; \n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Deprecated UniProt identifiers: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> deletedIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("UniProtTests", "deletedIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (deprecated.containsKey(identifier) && deprecated.get(identifier) == null) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
						  " is deleted; \n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Deleted UniProt identifiers: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> unreviewedIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("UniProtTests", "unreviewedIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (unreviewed.contains(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
						  " is unreviewed, please visit UniProt (https://www.uniprot.org/uniprot/" +
					      table.get(i, "identifier") + ") for (potential) reviewed version; \n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unreviewed UniProt identifiers: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> incorrectIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("UniProtTests", "incorrectIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (identifier.contains(" ") || identifier.contains(";")) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
						  " is incorrect; \n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Incorrect UniProt identifiers: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> allP62805(SPARQLHelper helper) throws Exception {
		Test test = new Test("UniProtTests", "allP62805");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("proteins/allUniProtP62805.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (identifier.contains(" ") || identifier.contains(";")) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
						  " may not be the identifier you intended (P62805 matches 14 different genes), please check; \n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Potentially imprecise UniProt identifier: " + errorCount, errors
		));
		return assertions;
	}
}
