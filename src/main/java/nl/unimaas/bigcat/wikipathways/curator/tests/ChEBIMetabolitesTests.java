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
import java.util.HashMap;
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

public class ChEBIMetabolitesTests {

	private static Map<String,String> oldToNew = new HashMap<String, String>();
	private static Map<String,String> neutralIons = new HashMap<String, String>();

	private static Map<String,String> nonexisting = BridgeDbTiwidReader.parseCSV("tiwid/chebi.csv");

	static {
		// now load the deprecation data
		String deprecatedData = ResourceHelper.resourceAsString("metabolite/chebi/deprecated.csv");
		String lines[] = deprecatedData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			oldToNew.put(ids[0], ids[1]);
		}
		// now load the neutral ions data
		String neutralIonsData = ResourceHelper.resourceAsString("metabolite/chebi/neutralIons.csv");
		lines = neutralIonsData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			neutralIons.put(ids[0], ids[1]);
		}
	}

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(secondaryChEBIIdentifiers(helper));
		assertions.addAll(faultyChEBIIdentifiers(helper));
		assertions.addAll(chebiDataTypo(helper));
		assertions.addAll(faultyChEBIChEBIIdentifiers(helper));
		assertions.addAll(neutralIons(helper));
		return assertions;
	}

	public static List<IAssertion> secondaryChEBIIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("ChEBIMetabolitesTests", "secondaryChEBIIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allChEBIIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (identifier.startsWith("CHEBI:")) {
					identifier = identifier.substring(6);
				}
				if (oldToNew.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " but has primary identifier CHEBI:" +
						oldToNew.get(identifier) + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Secondary ChEBI identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> neutralIons(SPARQLHelper helper) throws Exception {
		Test test = new Test("ChEBIMetabolitesTests", "neutralIons");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allChEBIIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (identifier.startsWith("CHEBI:")) {
					identifier = identifier.substring(6);
				}
				if (neutralIons.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " for a neutral atom, but maybe you mean the ion which has primary identifier CHEBI:" +
						neutralIons.get(identifier) + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "ChEBI identifiers for neutral atoms which are likely ions: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> faultyChEBIIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("ChEBIMetabolitesTests", "faultyChEBIIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allChEBIIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
	    String errors = "";
	    int errorCount = 0;
	    if (table.getRowCount() > 0) {
	    	// OK, but then it must be proteins, e.g. IFN-b
	    	for (int i=1; i<=table.getRowCount(); i++) {
	    		String identifier = table.get(i, "identifier");
	    		if (identifier.startsWith("CHEBI:")) {
	    			identifier = identifier.substring(6);
	    		}
	    		if (nonexisting.containsKey(identifier) && nonexisting.get(identifier) == null) {
	    			errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
					    " has a non-existing identifier CHEBI: " +
					    identifier + "\n";
	    			errorCount++;
	    		}
	    	}
	    }
		assertions.add(new AssertEquals(test,
			0, errorCount, "Non-existing ChEBI identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> chebiDataTypo(SPARQLHelper helper) throws Exception {
		Test test = new Test("ChEBIMetabolitesTests", "chebiDataTypo");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/chebi.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Typo 'CHEBI' data sources (use 'ChEBI'): " + table.getRowCount(),
			"" +table
		));
		return assertions;
	}

	public static List<IAssertion> faultyChEBIChEBIIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("ChEBIMetabolitesTests", "faultyChEBIChEBIIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allChEBIIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
	    String errors = "";
	    int errorCount = 0;
	    if (table.getRowCount() > 0) {
	    	for (int i=1; i<=table.getRowCount(); i++) {
	    		String identifier = table.get(i, "identifier");
	    		if (identifier.startsWith("ChEBI:")) {
	    			errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
					    " has a faulty identifier " +
					    identifier + " (it should be a number or start with \"CHEBI:\")\n";
	    			errorCount++;
	    		}
	    	}
	    }
		assertions.add(new AssertEquals(test,
			0, errorCount, "Faulty ChEBI identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

}
