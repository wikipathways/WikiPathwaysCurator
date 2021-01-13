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

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class ChEBIMetabolitesTests {

	private static Map<String,String> oldToNew = new HashMap<String, String>();

	private static List<String> nonexisting = new ArrayList<String>();
	static {{
		  nonexisting.add("443041");
		  nonexisting.add("594834");
	}}

	static {
		// now load the deprecation data
		String deprecatedData = ResourceHelper.resourceAsString("metabolite/chebi/deprecated.csv");
		String lines[] = deprecatedData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			oldToNew.put(ids[0], ids[1]);
		}
		System.out.println("size: " + oldToNew.size());
	}

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(secondaryChEBIIdentifiers(helper));
		assertions.addAll(faultyChEBIIdentifiers(helper));
		assertions.addAll(chebiDataTypo(helper));
		return assertions;
	}

	public static List<IAssertion> secondaryChEBIIdentifiers(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allChEBIIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("ChEBIMetabolitesTests", "secondaryChEBIIdentifiers", table));
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
		assertions.add(new AssertEquals(
			"ChEBIMetabolitesTests", "secondaryChEBIIdentifiers",
			0, errorCount, "Secondary ChEBI identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> faultyChEBIIdentifiers(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allChEBIIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("ChEBIMetabolitesTests", "faultyChEBIIdentifiers", table));
	    String errors = "";
	    int errorCount = 0;
	    if (table.getRowCount() > 0) {
	    	// OK, but then it must be proteins, e.g. IFN-b
	    	for (int i=1; i<=table.getRowCount(); i++) {
	    		String identifier = table.get(i, "identifier");
	    		if (identifier.startsWith("CHEBI:")) {
	    			identifier = identifier.substring(6);
	    		}
	    		if (nonexisting.contains(identifier)) {
	    			errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
					    " has a non-existing identifier CHEBI:" +
					    identifier + "\n";
	    			errorCount++;
	    		}
	    	}
	    }
		assertions.add(new AssertEquals(
			"ChEBIMetabolitesTests", "faultyChEBIIdentifiers",
			0, errorCount, "Non-existing ChEBI identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> chebiDataTypo(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/chebi.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertEquals(
			"ChEBIMetabolitesTests", "faultyChEBIIdentifiers",
			0, table.getRowCount(), "Typo 'CHEBI' data sources (use 'ChEBI'): " + table.getRowCount(),
			"" +table
		));
		return assertions;
	}

}
