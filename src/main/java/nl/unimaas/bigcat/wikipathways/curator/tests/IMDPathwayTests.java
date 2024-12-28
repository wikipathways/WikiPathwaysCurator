/* Copyright (C) 2023  Egon Willighagen <egon.willighagen@gmail.com>
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
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class IMDPathwayTests {
	
	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(allMetabolitesInteract(helper, format));
		assertions.addAll(metabolicConversions(helper, format));
		assertions.addAll(catalystsWithCommonDataSource(helper));
		assertions.addAll(metabolicConversionIdentifiersCommon(helper, format));
		assertions.addAll(diseasesHaveIdentifiers(helper));
		assertions.addAll(omimIdentifiers(helper));
		return assertions;
	}

	public static List<IAssertion> allMetabolitesInteract(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("IEMPathwayTests", "allMetabolitesInteract",
			"All metabolites are involved in interactions", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("imd/allMetabolitesInteract.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String wpURL = table.get(i, "url");
				if ("text/markdown".equals(format)) {
					errors += "* " + asMarkdownLink(table.get(i, "url")) + " has an metabolite not linked to an interaction: " +
						table.get(i, "metaboliteLabel") + "\n";
				} else {
					errors += table.get(i, "url") + " has an metabolite not linked to an interaction: " +
						table.get(i, "metaboliteLabel") + "\n";
				}
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Found metabolites without interaction: " + errorCount,
			errors, format
		));
		return assertions;
	}

	public static List<IAssertion> metabolicConversions(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("IEMPathwayTests", "metabolicConversions", "Unexpected metabolic conversion", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("imd/metabolicConversions.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				if ("text/markdown".equals(format)) {
					errors += "* " + asMarkdownLink(table.get(i, "url")) + " " + table.get(i, "interaction") + "\n";
				} else {
					errors += table.get(i, "url") + " " + table.get(i, "interaction") + "\n";
				}
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected interactions, not from metabolite to metabolite and not from RNA to RNA: " +
		    errorCount, errors, format
		));
		return assertions;
	}

	@SuppressWarnings("serial")
	private static List<String> commonCatalystDataSources = new ArrayList<>() {{
		add("NCBI Protein");
		add("Uniprot-TrEMBL");
		add("UniProtKB");
		add("Ensembl");
		add("Entrez Gene");
		add("Enzyme Nomenclature");
	}};

	public static List<IAssertion> catalystsWithCommonDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("IEMPathwayTests", "catalystsWithCommonDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("imd/allCatalysts.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String source = table.get(i, "source");
				if (!commonCatalystDataSources.contains(source)) {
			        errors += table.get(i, "url") + " " + table.get(i, "source") + "\n";
				    errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected data source for catalysts: " + errorCount, errors
		));
		return assertions;
	}

	@SuppressWarnings("serial")
	private static List<String> commonCatalysisDataSources = new ArrayList<>() {{
		add("Rhea");
		add("Reactome");
	}};

	public static List<IAssertion> metabolicConversionIdentifiersCommon(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("IEMPathwayTests", "metabolicConversionIdentifiersCommon",
			"Common metabolite-metabolite conversion identifier", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("imd/metabolicConversionIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0 && table.getColumnCount() > 2) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String source = table.get(i, "interactionSource");
				if (source != null && !commonCatalysisDataSources.contains(source)) {
					if ("text/markdown".equals(format)) {
						errors += "* " + asMarkdownLink(table.get(i, "url")) + " " +
							asMarkdownLink(table.get(i, "interaction")) + " " +
							table.get(i, "interactionSource") + " " +
							table.get(i, "interactionID") + "\n";
					} else {
						errors += table.get(i, "url") + " " + table.get(i, "interaction") + " " +
							table.get(i, "interactionSource") + " " +
							table.get(i, "interactionID") + "\n";
					}
				    errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected data source for the metabolic conversion: " + errorCount,
			errors, format
		));
		return assertions;
	}

	public static List<IAssertion> diseasesHaveIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("IEMPathwayTests", "diseasesHaveIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("imd/allDiseaseLabels.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "href");
				if (identifier == null || identifier.isEmpty()) {
			        errors += table.get(i, "url") +
			        	" \"" + table.get(i, "diseaseLabel").replaceAll("\n"," ") + 
			        	"\" does not have an identifier\n";
				    errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Likely diseases without identifiers: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> omimIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("IEMPathwayTests", "omimIdentifiers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("imd/allDiseaseLabels.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "href");
				if (identifier.contains("omim.org")) {
					if (!identifier.startsWith("https://omim.org/entry/") &&
							!identifier.startsWith("https://www.omim.org/entry/")) {
  			            errors += table.get(i, "url") +
			        	    " \"" + table.get(i, "diseaseLabel").replaceAll("\n"," ") + 
			        	    "\" has unexpected OMIM href: " + table.get(i, "href") + "\n";
				        errorCount++;
					}
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "OMIM links should start with \"https://omim.org/entry/\": " + errorCount, errors
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}
}
