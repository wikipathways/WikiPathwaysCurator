/* Copyright (C) 2021-2022  Egon Willighagen <egon.willighagen@gmail.com>
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

public class LIPIDMAPSTests {

	private static final Map<String,String> retired = BridgeDbTiwidReader.parseCSV("tiwid/lipidmaps.csv");

	// TODO: use identifiers.org or Bioregistry formatted identifiers instead
	@SuppressWarnings("serial")
	private static Set<String> okayNonLIPIDMAPIdentifiers = new HashSet<String>() {{
		// now load the deprecation data
		add("15346"); // ChEBI, CoA
		add("16108"); // ChEBI
		add("17115"); // ChEBI, L-serine
		add("26523"); // ROS
		add("26523"); // RNS
		add("CHEBI:17234"); // glucose
		add("CHEBI:17489"); // cAMP
		add("CHEBI:29016"); // arginine
		add("CHEBI:30616"); // ATP
		add("CHEBI:33704"); // amino acids
		add("CHEBI:57560");
		add("CHEBI:192800"); // LDL-
	}};

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(retiredIdentifiers(helper));
		assertions.addAll(onlyLIPIDMAPS(helper));
		return assertions;
	}

	public static List<IAssertion> retiredIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("LIPIDMAPSTests", "retiredIdentifiers", "Retired LIPID MAPS identifiers", true);
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/lipidmaps.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;

		// Testing
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (retired.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " but it has been retired, see " +
					    "https://www.lipidmaps.org/data/LMSDRecord.php?LMID=" + identifier +
					    (retired.get(identifier) != null ? " and replaced by " + retired.get(identifier) : "") +
					    "\n";
					errorCount++;
				}
			}
		}

		// Reporting
		assertions.add(new AssertEquals(test,
			0, errorCount, "Retired LIPID MAPS identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> onlyLIPIDMAPS(SPARQLHelper helper) throws Exception {
		Test test = new Test("LIPIDMAPSTests", "onlyLIPIDMAPS", "Only LIPID MAPS identifiers", true);
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithoutLIPIDMAPSIdentifier.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;

		// Testing
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (okayNonLIPIDMAPIdentifiers.contains(identifier)) {}
				  // some small molecules are okay, as we don't expect LIPID MAPS identifiers for them
				else {
				  errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " from " + table.get(i, "source") +
					    " but expected a LIPID MAPS identifier\n";
				  errorCount++;
				}
			}
		}

		// Reporting
		assertions.add(new AssertEquals(test,
			0, errorCount, "Expected a LIPID MAPS identifier, but found other identifiers: " + errorCount, errors
		));
		return assertions;
	}

}
