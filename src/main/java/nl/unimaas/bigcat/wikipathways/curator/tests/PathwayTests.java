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

public class PathwayTests {

	private static final Map<String,String> deprecated = BridgeDbTiwidReader.parseCSV("tiwid/wikipathways.csv");

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(deletedPathways(helper));
		assertions.addAll(linksToDeletedPathways(helper));
		assertions.addAll(linksToDeletedPathways_All(helper));
		assertions.addAll(speciesMismatch(helper));
		assertions.addAll(testRoundedRectangle(helper));
		assertions.addAll(youMustCite(helper));
		assertions.addAll(oldLicenses(helper));
		assertions.addAll(mediawikiLinks(helper, format));
		assertions.addAll(allEmptyDescriptions(helper, format));
		assertions.addAll(allShortDescriptions(helper, format));
		return assertions;
	}

	public static List<IAssertion> deletedPathways(SPARQLHelper helper) throws Exception {
		Test test = new Test("PathwayTests", "deletedPathways", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" + 
				"PREFIX wp:      <http://vocabularies.wikipathways.org/wp#>\n" +
				"prefix xsd:     <http://www.w3.org/2001/XMLSchema#>\n\n"
				+ "SELECT ?pathway ?wpid WHERE {\n  VALUES ?wpid { \n";
		for (String deprecatedPW : deprecated.keySet()) {
			sparql += "    \"" + deprecatedPW + "\"\n";
		}
		sparql += "  }\n"
				+ "  ?pathway dcterms:identifier ?wpid .\n"
				+ "  MINUS { ?pathway a wp:DataNode } \n}";
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String pathway = table.get(i, "pathway");
				String wpid = table.get(i, "wpid");
				errors += pathway + " mentions deleted pathway with " + wpid + "\n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Found " + table.getRowCount() + " deleted pathway(s).", errors
		));
		return assertions;
	}

	public static List<IAssertion> linksToDeletedPathways(SPARQLHelper helper) throws Exception {
		Test test = new Test("PathwayTests", "linksToDeletedPathways");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" + 
				"prefix xsd:     <http://www.w3.org/2001/XMLSchema#>\n" +
				"prefix foaf:    <http://xmlns.com/foaf/0.1/>\n" +
				"prefix wp:      <http://vocabularies.wikipathways.org/wp#>\n\n"
				+ "SELECT ?homepage ?wpid WHERE {\n  VALUES ?wpid { \n";
		for (String deprecatedPW : deprecated.keySet()) {
			sparql += "    \"" + deprecatedPW + "\"\n";
		}
		sparql += "  }\n"
				+ "  ?pathwayNode a wp:DataNode ;\n" + 
				  "    dcterms:identifier ?wpid ;\n" + 
				  "    dcterms:isPartOf ?pathway .\n" + 
				  "  ?pathway foaf:page ?homepage . \n}";
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String pathway = table.get(i, "homepage");
				String replacement = deprecated.get(table.get(i, "wpid"));
				errors += pathway + " links to a deleted pathway: " + table.get(i, "wpid") +
						(replacement != null ? " (check " + replacement + " as replacement)" : "") + "\n";
			}
		}
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Found " + table.getRowCount() + " pathways that link to deleted pathways", errors
		));
		return assertions;
	}

	// variation of linksToDeletedPathways() that only works when run on all pathways.
	// Because if run on only Curated, it will fail on linked pathways that are not in Curated
	// themselves.
	public static List<IAssertion> linksToDeletedPathways_All(SPARQLHelper helper) throws Exception {
		Test test = new Test("PathwayTests", "linksToDeletedPathways_All");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("pathways/linkedDeletedPathways.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 0, table.getRowCount(),
			"Pathway that seem to link to deleted pathways: " + table.getRowCount() + ". Actual deleted pathways should be added"
			+ " to https://github.com/bridgedb/tiwid/blob/main/data/wikipathways.csv",
			"" + table
		));
		return assertions;
	}
	
	@SuppressWarnings("serial")
	static List<String> noSpeciesMatch = new ArrayList<String>() {{
		add("WP3396"); // in WP3632, reason in GPML: "mouse pathway not yet available"
	}};

	public static List<IAssertion> speciesMismatch(SPARQLHelper helper) throws Exception {
		Test test = new Test("PathwayTests", "speciesMismatch");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/speciesMismatch.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "linkingHomepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		for (int i=1; i<=table.getRowCount(); i++) {
			String pathwayPage = table.get(i, "linkingHomepage");
			String species = table.get(i, "linkingOrganism");
			String identifier = table.get(i, "linkedID");
			if (!noSpeciesMatch.contains(identifier)) {
				String linkedSpecies = table.get(i, "linkedOrganism");
				errors += pathwayPage + " is " + species + " but links to pathway " + identifier +
						" which is " + linkedSpecies + "\n ";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found " + errorCount + " pathways that link to pathways of a different species", errors
		));
		return assertions;
	}

	public static List<IAssertion> testRoundedRectangle(SPARQLHelper helper) throws Exception {
		Test test = new Test("PathwayTests", "testRoundedRectangle");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/noRoundedRectangle.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				String pathwayPage = table.get(i, "homepage");
				identifier = identifier.trim();
				if (!identifier.isEmpty()) {
					try {
						Integer.parseInt(identifier);
					} catch (NumberFormatException exception) {
						errors += pathwayPage + " -> " + table.get(i, "graphid") +
								", " + identifier + "\n ";
						errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Pathways DataNodes with WikiPathways ID that can be converted to have a RoundedRectangle ShapeType so that they become clickable: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> youMustCite(SPARQLHelper helper) throws Exception {
		Test test = new Test("PathwayTests", "youMustCite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/youMustCite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 0, table.getRowCount(),
			"Pathway description that contain 'you must cite': " + table.getRowCount(),
			"" + table
		));
		return assertions;
	}

	public static List<IAssertion> mediawikiLinks(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("PathwayTests", "mediawikiLinks");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/mediawikiLinks.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errorCount++;
				if ("text/markdown".equals(format)) {
					errors += "* " + asMarkdownLink(table.get(i, "homepage"));
				} else {
					errors += table.get(i, "homepage") + "\n";
				}
			}
		}
		assertions.add(new AssertEquals(test, 0, errorCount,
			"Pathways of which the description may contain MediaWiki-style links: " + table.getRowCount(),
			errors
		));
		return assertions;
	}

	public static List<IAssertion> allEmptyDescriptions(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("PathwayTests", "allEmptyDescriptions", "Pathway description is empty", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allEmptyDescriptions.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errorCount++;
				if ("text/markdown".equals(format)) {
					errors += "* " + asMarkdownLink(table.get(i, "homepage"));
				} else {
					errors += table.get(i, "homepage") + "\n";
				}
			}
		}
		assertions.add(new AssertEquals(test, 0, errorCount,
			"Pathways of with an empty description: " + table.getRowCount(), errors, format
		));
		return assertions;
	}

	public static List<IAssertion> allShortDescriptions(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("PathwayTests", "allShortDescriptions", "Pathway description is too short", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/allShortDescriptions.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errorCount++;
				if ("text/markdown".equals(format)) {
					errors += "* " + asMarkdownLink(table.get(i, "homepage"));
				} else {
					errors += table.get(i, "homepage") + "\n";
				}
			}
		}
		assertions.add(new AssertEquals(test, 0, errorCount,
			"Pathways of with a very short description: " + table.getRowCount(),
			errors, format
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

	@SuppressWarnings("serial")
	private static Set<String> acceptableLicenses = new HashSet<String>() {{
		this.add("CCZero");
		this.add("CC0");
	}};

	public static List<IAssertion> oldLicenses(SPARQLHelper helper) throws Exception {
		Test test = new Test("PathwayTests", "oldLicenses");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("pathways/allLicensedPathways.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String license = table.get(i, "license");
				String pathwayPage = table.get(i, "pathway");
				license = license.trim();
				if (!license.isEmpty()) {
					if (!acceptableLicenses.contains(license)) {
						errors += pathwayPage + " has @License " + license + "\n ";
						errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Pathway using an old license (should be CCZero; remove the @License tag?): " + errorCount, errors
		));
		return assertions;
	}

}
