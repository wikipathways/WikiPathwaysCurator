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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class MetabolitesTests {

	private static void addIdentifiersOrg(Set<String> setToAddTo, String identifier) {
		setToAddTo.add("https://" + identifier);
		setToAddTo.add("http://" + identifier);
	}

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(metaboliteAlsoOtherType(helper));
		assertions.addAll(casNumbersNotMarkedAsMetabolite(helper));
		assertions.addAll(chemspiderIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(HMDBIDsNotMarkedAsMetabolite(helper));
		assertions.addAll(KEGGIDsNotMarkedAsMetabolite(helper));
		return assertions;
	}

	public static List<IAssertion> metaboliteAlsoOtherType(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/badType.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("MetabolitesTests", "metaboliteAlsoOtherType", table));
		Set<String> exceptions = new HashSet<String>();
		    MetabolitesTests.addIdentifiersOrg(exceptions, "identifiers.org/chebi/CHEBI:16991"); // DNA
		    MetabolitesTests.addIdentifiersOrg(exceptions, "identifiers.org/chebi/CHEBI:39026"); // LDL
		    MetabolitesTests.addIdentifiersOrg(exceptions, "identifiers.org/wikidata/Q27205"); // fibrin
		    MetabolitesTests.addIdentifiersOrg(exceptions, "identifiers.org/wikidata/Q381899"); // fibrogen
		    MetabolitesTests.addIdentifiersOrg(exceptions, "identifiers.org/wikidata/Q2162109"); // fibrin degradation product
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String metabolite = table.get(i, "metabolite");
				if (!exceptions.contains(metabolite)) {
					    errors += metabolite + " is also found to be " + table.get(i, "type") + "\n";
				    errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals("MetabolitesTests", "metaboliteAlsoOtherType", 
			0, errorCount, "Metabolite is also found to be another type: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> chemspiderIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/chemspiderNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("MetabolitesTests", "chemspiderIDsNotMarkedAsMetabolite", table));
		assertions.add(new AssertEquals("MetabolitesTests", "chemspiderIDsNotMarkedAsMetabolite", 
			0, table.getRowCount(), "Unexpected ChemSpider identifiers for non-metabolites: " + table.getRowCount(), table.toString()
		));
		return assertions;
	}

	public static List<IAssertion> HMDBIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/hmdbNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("MetabolitesTests", "HMDBIDsNotMarkedAsMetabolite", table));
		assertions.add(new AssertEquals("MetabolitesTests", "HMDBIDsNotMarkedAsMetabolite", 
			0, table.getRowCount(), "Unexpected HMDB identifiers for non-metabolites: " + table.getRowCount(), table.toString()
		));
		return assertions;
	}

	public static List<IAssertion> KEGGIDsNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/keggNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("MetabolitesTests", "KEGGIDsNotMarkedAsMetabolite", table));
		assertions.add(new AssertEquals("MetabolitesTests", "KEGGIDsNotMarkedAsMetabolite", 
			0, table.getRowCount(), "Unexpected KEGG identifiers for non-metabolites: " + table.getRowCount(), table.toString()
		));
		return assertions;
	}
	
	public static List<IAssertion> casNumbersNotMarkedAsMetabolite(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/casNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("MetabolitesTests", "casNumbersNotMarkedAsMetabolite", table));
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
		assertions.add(new AssertEquals(
			"MetabolitesTests", "casNumbersNotMarkedAsMetabolite",
			0, errorCount, "Unexpected CAS identifiers for non-metabolites: " + errorCount, errors
		));
		return assertions;
	}

}
