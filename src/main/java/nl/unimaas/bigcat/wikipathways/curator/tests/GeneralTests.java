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
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class GeneralTests {

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(titlesShortEnough(helper));
		assertions.addAll(weirdCharacterTitles(helper));
		assertions.addAll(duplicateTitles(helper));
		return assertions;
	}

	public static List<IAssertion> titlesShortEnough(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allTitles.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("GeneralTests", "titlesShortEnough", table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String title = table.get(i, "title");
				if (title != null && title.length() > 80) {
					errors += table.get(i, "page") + " '" +
							title + "'\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals("GeneralTests", "titlesShortEnough",
			0, errorCount, "Too long pathway titles (>80 chars):\n" + errors
		));
		return assertions;
	}

	public static List<IAssertion> weirdCharacterTitles(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allTitles.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("GeneralTests", "weirdCharacterTitles", table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String title = table.get(i, "title");
				if (title != null) {
					int weirdCharCount = 0;
					for (char c : title.toCharArray()) {
						if (Character.isAlphabetic(c)) {}
						else if (Character.isDigit(c)) {}
					    else if (Character.isSpaceChar(c)) {}
					    else if (c == '-') {}
					    else if (c == ',') {}
					    else if (c == ':') {}
					    else {
					    	weirdCharCount++;
					    }
					}
					if (weirdCharCount > 0) {
					    errors += table.get(i, "page") + " '" +
						    	title + "' has " + weirdCharCount + " weird characters\n";
					    errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals("GeneralTests", "titlesShortEnough",
			0, errorCount, "Titles with unexpected characters (only allow alphanumerics and spaces):\n" + errors
		));
		return assertions;
	}

	public static List<IAssertion> duplicateTitles(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allTitlesBySpecies.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("GeneralTests", "duplicateTitles", table));
		String errors = "";
		int errorCount = 0;
		Map<String,String> allTitles = new HashMap<>();
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String page  = table.get(i, "page");
				String title = table.get(i, "title") + " - " + table.get(i, "species");
				if (allTitles.containsKey(title.toLowerCase())) {
				    errors += page + " '" + title + "' is duplicate title of " +
				              allTitles.get(title.toLowerCase()) + "\n";
				    errorCount++;
				}
				allTitles.put(title.toLowerCase(), page);
			}
		}
		assertions.add(new AssertEquals("GeneralTests", "duplicateTitles",
			0, errorCount, "Duplicate titles:\n" + errors
		));
		return assertions;
	}

}
