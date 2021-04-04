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

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotSame;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class HMDBMetabolitesTests {

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(outdatedIdentifiers(helper));
		assertions.addAll(correctFormat(helper));
		return assertions;
	}

	public static List<IAssertion> outdatedIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("HMDBMetabolitesTests", "outdatedIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/hmdb/outdatedHMDBidentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 
			0, table.getRowCount(), "Outdated HMDB identifiers", table.toString()
		));
		return assertions;
	}
	
	public static List<IAssertion> correctFormat(SPARQLHelper helper) throws Exception {
		Test test = new Test("HMDBMetabolitesTests", "correctFormat");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allHMDBIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		assertions.add(new AssertNotSame(test, 0, table.getRowCount(), "I expected more than zero HMDB identifiers."));
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				boolean correctFormat = false;
				if (identifier.startsWith("HMDB")) {
					try {
						String uniquePart = identifier.substring(4);
						Integer.parseInt(uniquePart);
						correctFormat = true;
					} catch (Exception e) {
						// not a number
					}
				}
				if (!correctFormat) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
							" has " + identifier + " which is not a correct format\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "HMDB identifiers detected with incorrect format: " + errorCount, errors
		));
		return assertions;
	}

}
