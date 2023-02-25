/* Copyright (C) 2013,2018-2020  Egon Willighagen <egon.willighagen@gmail.com>
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

public class GeneTests {

	private static final Map<String,String> deprecated = BridgeDbTiwidReader.parseCSV("tiwid/hgnc.csv");

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(entrezGeneIdentifiersNotNumber(helper));
		assertions.addAll(affyProbeIdentifiersNotCorrect(helper));
		assertions.addAll(outdatedIdentifiers(helper));
		assertions.addAll(numericHGNCIDs(helper));
		assertions.addAll(nonNumericHGNCAccessionNumbers(helper));
		return assertions;
	}

	public static List<IAssertion> entrezGeneIdentifiersNotNumber(SPARQLHelper helper) throws Exception {
		Test test = new Test("GeneTests", "entrezGeneIdentifiersNotNumber");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allEntrezGenesIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				String pathwayPage = table.get(i, "homepage");
				if (!pathwayPage.contains("WP2806")) { // this pathway has a number of non-human genes
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
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Entrez Gene identifiers that are not numbers: " + errorCount, errors
		));
		return assertions;
	}
	
	public static List<IAssertion> affyProbeIdentifiersNotCorrect(SPARQLHelper helper) throws Exception {
		Test test = new Test("GeneTests", "affyProbeIdentifiersNotCorrect");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allAffyProbeIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				String pathwayPage = table.get(i, "homepage");
				if (!pathwayPage.contains("WP2806")) { // this pathway has a number of non-human genes
					identifier = identifier.trim();
					if (!identifier.isEmpty()) {
						if (identifier.contains("CHEBI:")) {
							errors += table.get(i, "homepage") + " -> " + table.get(i, "label") +
									", " + table.get(i, "identifier") + "\n ";
							errorCount++;
						} else if (identifier.contains("ENS:")) {
							errors += table.get(i, "homepage") + " -> " + table.get(i, "label") +
									", " + table.get(i, "identifier") + "\n ";
							errorCount++;
						}
					}
				}
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Affy Probe identifiers that do not look right: " + errorCount, errors
		));
		return assertions;
	}
	
	public static List<IAssertion> outdatedIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("GeneTests", "outdatedIdentifiers");
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
				if (deprecated.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " but has been replace by " +
						deprecated.get(identifier) + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Old HGNC identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> numericHGNCIDs(SPARQLHelper helper) throws Exception {
		Test test = new Test("GeneTests", "outdatedIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allHGNCIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "identifier");
				if (id != null && id.length() > 0) {
					try {
						Integer.parseInt(id);
						// okay, that doesn't look right
						errorCount++;
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
								" has " + id + "\n";;
					} catch (NumberFormatException exception) {
						// this is expected: "HGNC" data source are the symbols
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found integer HGNC symbols (did you mean 'HGNC Accession number'?): " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> nonNumericHGNCAccessionNumbers(SPARQLHelper helper) throws Exception {
		Test test = new Test("GeneTests", "nonNumericHGNCAccessionNumbers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allHGNCAccessionNumbers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "identifier");
				if (id != null && id.length() > 0) {
					try {
						Integer.parseInt(id);
					} catch (NumberFormatException exception) {
						errorCount++;
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
								" has " + id + "\n";;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found non-integer HGNC Accession numbers (did you mean 'HGNC'?): " + errorCount, errors
		));
		return assertions;
	}

}
