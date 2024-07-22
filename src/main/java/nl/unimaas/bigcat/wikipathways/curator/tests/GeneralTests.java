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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertTrue;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class GeneralTests {

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(titlesShortEnough(helper, format));
		assertions.addAll(weirdCharacterTitles(helper, format));
		assertions.addAll(duplicateTitles(helper, format));
		assertions.addAll(noTags(helper, format));
		assertions.addAll(recentness(helper, format));
		assertions.addAll(nullDataSources(helper, format));
		assertions.addAll(undefinedDataSources(helper, format));
		assertions.addAll(emptyDataSources(helper, format));
		assertions.addAll(undefinedIdentifier(helper, format));
		assertions.addAll(dataNodeWithoutGraphId(helper, format));
		assertions.addAll(groupsHaveDetail(helper, format));
		assertions.addAll(emptyLabelOfNodeWithIdentifier(helper, format));
		assertions.addAll(curationAndHypothetical(helper, format));
		assertions.addAll(curationAndNeedsWork(helper, format));
		assertions.addAll(stockComments(helper, format));
		return assertions;
	}

	public static List<IAssertion> titlesShortEnough(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "titlesShortEnough");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allTitles_notReactome.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "page");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String title = table.get(i, "title");
				if (title != null && title.length() > 100) {
					errors += table.get(i, "page") + " '" +
							title + "'\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Too long pathway titles (>80 chars): " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> weirdCharacterTitles(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "weirdCharacterTitles");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allTitles.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "page");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String title = table.get(i, "title");
				// okay, there is one situation where a '+' is allows: "NAD+"
				title = title.replaceAll("NAD\\+", "");
				if (title != null) {
					int weirdCharCount = 0;
					for (char c : title.toCharArray()) {
						if (Character.isAlphabetic(c)) {}
						else if (Character.isDigit(c)) {}
					    else if (Character.isSpaceChar(c)) {}
					    else if (c == '\'') {}
					    else if (c == '-') {}
					    else if (c == '.') {}
					    else if (c == ',') {}
					    else if (c == ':') {}
					    else if (c == '(') {}
					    else if (c == ')') {}
					    else if (c == '[') {}
					    else if (c == ']') {}
					    else if (c == '/') {}
					    else {
					    	weirdCharCount++;
					    }
					}
					if (weirdCharCount > 0) {
					    errors += table.get(i, "page") + " '" +
						    	title + "' has " + weirdCharCount + " weird character(s)\n";
					    errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Titles with unexpected characters (only allow alphanumerics and one of '/\\-.,:()[]'):" + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> duplicateTitles(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "duplicateTitles");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allTitlesBySpecies.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "page");
		assertions.add(new AssertNotNull(test, table));
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
		assertions.add(new AssertEquals(test,
			0, errorCount, "Duplicate titles: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> curationAndHypothetical(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "curationAndHypothetical");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allCurationAndHypothetical.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "page");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String page  = table.get(i, "page");
				String title = table.get(i, "title");
				errors += page + " '" + title + "' \n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Pathways tagged as Curation and Hypothetical: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> curationAndNeedsWork(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "curationAndNeedsWork");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allCurationAndNeedsWork.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "page");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String page  = table.get(i, "page");
				String title = table.get(i, "title");
				errors += page + " '" + title + "' \n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Pathways tagged as Curation and NeedsWork: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> curationAndReactome(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "curationAndReactome");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allCurationAndReactome.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "page");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String page  = table.get(i, "page");
				String title = table.get(i, "title");
				errors += page + " '" + title + "' \n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Pathways tagged as Curation and Reactome: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> noTags(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "noTags");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/noTags.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "page");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String page  = table.get(i, "page");
				String title = table.get(i, "title");
				String organism = table.get(i, "organism");
				errors += page + " '" + title + " (" + organism + ")' \n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Pathways without any tag: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> recentness(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "recentness");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/rdfDate.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			1, table.getRowCount(), "Expected only one PAV createdData but got: " + table.getRowCount(), "" + table)
		);
		Date pavDate = new SimpleDateFormat("yyyy-MM-dd").parse(table.get(1, "date").substring(0,10));
		Date now = new Date();
		long diffInMillies = now.getTime() - pavDate.getTime();
	    long dayDiff =  TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);
		assertions.add(new AssertTrue(test,
			(dayDiff <= 40), "The data release is " + dayDiff + " days old: " + table.get(1, "date")
		));
		return assertions;
	}

	public static List<IAssertion> nullDataSources(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "nullDataSources");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/nullDataSource.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Data nodes with a 'null' data source: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> undefinedDataSources(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "undefinedDataSources");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/undefinedDataSource.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Data nodes with an 'undefined' data source: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> emptyDataSources(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "emptyDataSources", "Data nodes with an identifier by an empty data source", false);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/emptyDataSource.rq");
		StringMatrix table = helper.sparql(sparql);
		String errors = "";
		if (table.getRowCount() > 0) {
            for (int i=1; i<=table.getRowCount(); i++) {
            	if ("text/markdown".equals(format)) {
				    errors += "* " + asMarkdownLink(table.get(i, "homepage")) + " " + table.get(i, "identifier") +
				    	"(" + table.get(i, "label") + ")\n";
            	} else {
            		errors += table.get(i, "homepage") + " " + table.get(i, "identifier") +
    				    "(" + table.get(i, "label") + ")\n";
            	}
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Data nodes with a '' data source: " + table.getRowCount(), errors, format
		));
		return assertions;
	}

	public static List<IAssertion> undefinedIdentifier(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "undefinedIdentifier");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allUndefinedIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Data nodes with an 'undefined' identifier: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> emptyLabelOfNodeWithIdentifier(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "emptyLabelOfNodeWithIdentifier");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/emptyLabelsWithIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "node") + " (" +
						  table.get(i, "label")+ "), " + table.get(i, "homepage") + "\n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Data nodes with an identifier but empty label: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> dataNodeWithoutGraphId(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "dataNodeWithoutGraphId");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("structure/dataNodeWithoutGraphId.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Data nodes without @GraphId: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> groupsHaveDetail(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "groupsHaveDetail");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("structure/groupDetails.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Expected details for things of type gpml:Group: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> stockComments(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("GeneralTests", "stockComments", "The pathway contains stock comments", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/stockComments.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
            for (int i=1; i<=table.getRowCount(); i++) {
            	if ("text/markdown".equals(format)) {
				    errors += "* " + asMarkdownLink(table.get(i, "homepage")) + " " + table.get(i, "thing") + "\n";
            	} else {
            		errors += table.get(i, "homepage") + " " + table.get(i, "thing") + "\n";
            	}
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Number of graphical items with the comment \"Type your comment here\": " + table.getRowCount(), errors, format
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

}
