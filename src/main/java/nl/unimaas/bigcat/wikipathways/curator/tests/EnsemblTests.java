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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotSame;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class EnsemblTests {

	@SuppressWarnings({ "serial" })
	private static final Map<String,String> deprecated = new HashMap<String,String>();

	static {
		// See BridgeDb Tiwid: https://github.com/bridgedb/tiwid, doi:10.5281/zenodo.4479409
		String tiwidData = ResourceHelper.resourceAsString("tiwid/ensembl.csv");
		BufferedReader reader = new BufferedReader(new StringReader(tiwidData));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) continue;
				String fields[] = line.split(",");
				deprecated.put(fields[0], fields[2]);
			}
		} catch (IOException e) {
			// blah
		}
	}
	
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
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/allEnsemblIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("EnsemblTests", "outdatedIdentifiers", table));
		assertions.add(new AssertNotSame(
			"EnsemblTests", "outdatedIdentifiers", 0, table.getColumnCount(), "Expected more than 0 Ensembl identifiers"
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
		assertions.add(new AssertEquals("EnsemblTests", "outdatedIdentifiers", 
			0, errorCount, "Deprecated Ensembl identifiers:" + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForHumanSpecies(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Human.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("EnsemblTests", "wrongEnsemblIDForHumanSpecies", table));
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
		assertions.add(new AssertEquals("EnsemblTests", "wrongEnsemblIDForHumanSpecies", 
			0, errorCount, "Ensembl identifiers for wrong species for a human pathway: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForRatSpecies(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Rat.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("EnsemblTests", "wrongEnsemblIDForRatSpecies", table));
		assertions.add(new AssertEquals("EnsemblTests", "wrongEnsemblIDForRatSpecies", 
			0, table.getRowCount(), "Ensembl identifiers for wrong species for a rat pathway: " + table.getRowCount()
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForCowSpecies(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Cow.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("EnsemblTests", "wrongEnsemblIDForCowSpecies", table));
		assertions.add(new AssertEquals("EnsemblTests", "wrongEnsemblIDForCowSpecies", 
			0, table.getRowCount(), "Ensembl identifiers for wrong species for a cow pathway: " + table.getRowCount()
		));
		return assertions;
	}

	public static List<IAssertion> wrongEnsemblIDForMouseSpecies(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Mouse.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("EnsemblTests", "wrongEnsemblIDForMouseSpecies", table));
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
		assertions.add(new AssertEquals("EnsemblTests", "wrongEnsemblIDForMouseSpecies", 
			0, errorCount, "Ensembl identifiers for wrong species for a mouse pathway: " + errorCount, errors
		));
		return assertions;
	}

}
