/* Copyright (C) 2020  Egon Willighagen <egon.willighagen@gmail.com>
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
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotSame;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class CASMetabolitesTests {

	// the content comes from the resource metabolite/cas/deprecated.csv (see below in the loadData() method)
	private static Map<String,String> oldToNew = new HashMap<String, String>();

	@SuppressWarnings({ "serial" })
	private static final Map<String,String> deprecated = new HashMap<String,String>() {{
		put("2646-71-1", "53-57-6"); // the first is a salt of the second
		put("142-10-9", "591-57-1"); // the first is a stereo-aspecific version of the second
		put("9029-62-3", "something that is not for an enzyme"); // the first is a stereo-aspecific version of the second
		put("102029-88-9", "73495-12-2"); // the first is a salt of the second
	}};

	static {
		// now load the deprecation data
		String deprecatedData = ResourceHelper.resourceAsString("metabolite/cas/deprecated.csv");
		String lines[] = deprecatedData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			oldToNew.put(ids[0], ids[1]);
		}
	}

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(deletedCASIdentifiers(helper));
		assertions.addAll(outdatedIdentifiers(helper));
		return assertions;
	}

	public static List<IAssertion> deletedCASIdentifiers(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allCASIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("CASMetabolitesTests", "deletedCASIdentifiers", table));
		assertions.add(new AssertNotSame(
			"CASMetabolitesTests", "deletedCASIdentifiers", 0, oldToNew.size(),
			"Error while loading the deleted CAS numbers"
		));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (oldToNew.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
							" has " + identifier + " but has new identifier " +
							oldToNew.get(identifier) + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(
			"CASMetabolitesTests", "deletedCASIdentifiers",
			0, errorCount, "Deleted CAS registry numbers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> outdatedIdentifiers(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allCASIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("CASMetabolitesTests", "outdatedIdentifiers", table));
		assertions.add(new AssertNotSame(
			"CASMetabolitesTests", "outdatedIdentifiers", 0, oldToNew.size(),
			"Error while loading the deleted CAS numbers"
		));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (deprecated.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
							" should be " + deprecated.get(identifier) + "; ";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(
			"CASMetabolitesTests", "outdatedIdentifiers",
			0, errorCount, "Deprecated CAS Registry numbers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}
}
