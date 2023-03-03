/* Copyright (C) 2015,2018-2020  Egon Willighagen <egon.willighagen@gmail.com>
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
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotSame;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class EnsemblTests {

	private static final Map<String,String> deprecated = BridgeDbTiwidReader.parseCSV("tiwid/ensembl.csv");
	
	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(outdatedIdentifiers(helper));
		assertions.addAll(wrongEnsemblIDForHumanSpecies(helper));
		assertions.addAll(wrongEnsemblIDForRatSpecies(helper));
		assertions.addAll(wrongEnsemblIDForCowSpecies(helper));
		assertions.addAll(wrongEnsemblIDForMouseSpecies(helper));
		return assertions;
	}

	public static List<IAssertion> outdatedIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("EnsemblTests", "outdatedIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allEnsemblIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertNotSame(test,
			0, table.getColumnCount(), "Expected more than 0 Ensembl identifiers"
		));
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
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Deprecated Ensembl identifiers: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForHumanSpecies(SPARQLHelper helper) throws Exception {
		Test test = new Test("EnsemblTests", "wrongEnsemblIDForHumanSpecies");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Human.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Ensembl identifiers for wrong species for a human pathway: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForRatSpecies(SPARQLHelper helper) throws Exception {
		Test test = new Test("EnsemblTests", "wrongEnsemblIDForRatSpecies");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Rat.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 
			0, table.getRowCount(), "Ensembl identifiers for wrong species for a rat pathway: " + table.getRowCount()
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForCowSpecies(SPARQLHelper helper) throws Exception {
		Test test = new Test("EnsemblTests", "wrongEnsemblIDForCowSpecies");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Cow.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 
			0, table.getRowCount(), "Ensembl identifiers for wrong species for a cow pathway: " + table.getRowCount()
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForMouseSpecies(SPARQLHelper helper) throws Exception {
		Test test = new Test("EnsemblTests", "wrongEnsemblIDForMouseSpecies");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Mouse.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "Ensembl identifiers for wrong species for a mouse pathway: " + errorCount, errors
		));
		return assertions;
	}

}
