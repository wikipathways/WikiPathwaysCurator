/* Copyright (C) 2013,2018-2022  Egon Willighagen <egon.willighagen@gmail.com>
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
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class MetabolitesTests {

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(metaboliteAlsoOtherType(helper));
		assertions.addAll(casNumbersNotMarkedAsMetabolite(helper));
		assertions.addAll(chemspiderIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(HMDBIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(KEGGIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(metabolitesWithAnEnsembleID(helper));
		assertions.addAll(metabolitesWithAnEntrezGeneID(helper));
		assertions.addAll(metabolitesWithDbButNoIdentifier(helper));
		assertions.addAll(metabolitesWithIdentifierButNoDb(helper));
		assertions.addAll(ChEBIIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(PubChemIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(PubChemSubstanceIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(PubChemIDsNotNumbers(helper));
		assertions.addAll(PubChemSubstanceIDsNotNumbers(helper));
		assertions.addAll(tooManyInChIKeys(helper, format));
		assertions.addAll(achiralAminoAcids(helper));
		return assertions;
	}

	public static List<IAssertion> metaboliteAlsoOtherType(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "metaboliteAlsoOtherType");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/badType.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		Set<String> exceptions = new HashSet<String>();
		    exceptions.add("CHEBI:16991"); exceptions.add("16991"); // DNA
		    exceptions.add("CHEBI:39026"); exceptions.add("39026"); // LDL
		    exceptions.add("CHEBI:81569"); exceptions.add("81569"); // Follicle stimulating hormone
		    exceptions.add("CHEBI:138181"); exceptions.add("138181"); // chemokine ligand 8
		    exceptions.add("Q27205"); // fibrin
		    exceptions.add("Q381899"); // fibrogen
		    exceptions.add("Q2162109"); // fibrin degradation product
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String metabolite = table.get(i, "metabolite");
				String id = table.get(i, "id");
				if (!exceptions.contains(id)) {
					    errors += metabolite + " (" + table.get(i, "id") +
					    		  ") is found to be " + table.get(i, "type") +
					    		  " in " + table.get(i, "pathway") + "\n";
				    errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Metabolite is also found to be another type: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> chemspiderIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "chemspiderIDsNotMarkedAsMetabolite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/chemspiderNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 
			0, table.getRowCount(), "Unexpected ChemSpider identifiers for non-metabolites: " + table.getRowCount(), table.toString()
		));
		return assertions;
	}

	public static List<IAssertion> HMDBIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "HMDBIDsNotMarkedAsMetabolite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/hmdbNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 
			0, table.getRowCount(), "Unexpected HMDB identifiers for non-metabolites: " + table.getRowCount(), table.toString()
		));
		return assertions;
	}

	public static List<IAssertion> KEGGIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "KEGGIDsNotMarkedAsMetabolite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/keggNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test, 
			0, table.getRowCount(), "Unexpected KEGG identifiers for non-metabolites: " + table.getRowCount(), table.toString()
		));
		return assertions;
	}
	
	public static List<IAssertion> casNumbersNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "casNumbersNotMarkedAsMetabolite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/casNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedProteins = new HashSet<String>();
		allowedProteins.add("IFN-b");
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected CAS identifiers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> metabolitesWithAnEnsembleID(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "metabolitesWithAnEnsembleID");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithAnEnsembleID.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Unexpected metabolites with an Ensemble identifier: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> metabolitesWithAnEntrezGeneID(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "metabolitesWithAnEntrezGeneID");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithAnEntrezGeneID.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "pathway");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Unexpected metabolites with an Entrez Gene identifier:: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> metabolitesWithDbButNoIdentifier(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "metabolitesWithDbButNoIdentifier");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithDatabaseButNoIdentifier.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Unexpected metabolites with identifier but no database source: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> metabolitesWithIdentifierButNoDb(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "metabolitesWithIdentifierButNoDb");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithIdentifierButNoDatabase.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Unexpected metabolites with identifier but no database source: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> ChEBIIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "ChEBIIDsNotMarkedAsMetabolite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/chebiNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowed = new HashSet<String>();
		allowed.add("CHEBI:15986"); // polynucleotide
		allowed.add("CHEBI:9160");  // single stranded DNA
		allowed.add("CHEBI:16991"); // double stranded DNA
		allowed.add("CHEBI:33697"); // ribonucleic acid (RNA)
		allowed.add("CHEBI:33698"); // DNA
		allowed.add("CHEBI:33699"); // mRNA
		allowed.add("CHEBI:39026"); // LDL
		allowed.add("CHEBI:89981"); // LPS
		allowed.add("CHEBI:138181"); // IL-1β
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowed.contains(table.get(i, "identifier").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
					    " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected ChEBI identifiers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> PubChemIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "PubChemIDsNotMarkedAsMetabolite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/pubchemNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedProteins = new HashSet<String>();
		allowedProteins.add("Fibrin");
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected PubChem identifiers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> PubChemSubstanceIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "PubChemSubstanceIDsNotMarkedAsMetabolite");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/pubchemSubstanceNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowedProteins = new HashSet<String>();
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " -> " + table.get(i, "identifier") + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected PubChem Substance identifiers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> PubChemIDsNotNumbers(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "PubChemIDsNotNumbers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allPubChemIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer.parseInt(identifier);
				} catch (NumberFormatException exception) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected PubChem Compound identifiers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> PubChemSubstanceIDsNotNumbers(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "PubChemSubstanceIDsNotNumbers");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allPubChemSubstanceIdentifiers.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer.parseInt(identifier);
				} catch (NumberFormatException exception) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Unexpected PubChem Substance identifiers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> tooManyInChIKeys(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("MetabolitesTests", "tooManyInChIKeys", "Too many InChIKeys for the used identifier", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/tooManyInChIKeys.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "examplePathway");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer.parseInt(identifier);
				} catch (NumberFormatException exception) {
					if ("text/markdown".equals(format)) {
						errors += "* " + asMarkdownLink(table.get(i, "examplePathway")) +
								" has: " + table.get(i, "label") + " (" + table.get(i, "identifier") +
								") with InChIKeys: `" + table.get(i, "inchikeys") + "`\n" ;
					} else {
						errors += table.get(i, "examplePathway") + " (example) has: " + table.get(i, "label") + " " + table.get(i, "identifier") +
							" with InChIKeys: " + table.get(i, "inchikeys") + "\n" ;
					}
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Non-zero count of metabolites with more than one InChIKey: " + errorCount,
			errors, format
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

	public static List<IAssertion> achiralAminoAcids(SPARQLHelper helper) throws Exception {
		Test test = new Test("MetabolitesTests", "achiralAminoAcids");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/achiralAminoAcids.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		Set<String> allowed = new HashSet<String>();
		allowed.add("WP3953"); // has cysteine convert into L-cysteine
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				if (!allowed.contains(table.get(i, "wpid"))) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " +
				        table.get(i, "achiralAA").substring(30) + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Metabolites with ChEBI identifiers of achiral amino acids: " + errorCount, errors
		));
		return assertions;
	}

}
