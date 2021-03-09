/* Copyright (C) 2014-2021  Egon Willighagen <egon.willighagen@gmail.com>
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

import org.junit.jupiter.api.Assertions;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertTrue;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class OudatedDataSourcesTests {

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(outdatedUniprot(helper));
		assertions.addAll(outdatedUniprot2(helper));
		assertions.addAll(outdatedUniprot3(helper));
		assertions.addAll(outdatedUniprot4(helper));
		assertions.addAll(oldUniprotSwissProt(helper));
		assertions.addAll(wrongPubChem(helper));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("OudatedDataSourcesTests", "outdatedUniprot", table));
		assertions.add(new AssertEquals("OudatedDataSourcesTests", "outdatedUniprot",
			0, table.getRowCount(), "Outdated 'Uniprot' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot2(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot2.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("OudatedDataSourcesTests", "outdatedUniprot2", table));
		assertions.add(new AssertEquals("OudatedDataSourcesTests", "outdatedUniprot2",
			0, table.getRowCount(), "Outdated 'UniProt/TrEMBL' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot3(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot3.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("OudatedDataSourcesTests", "outdatedUniprot3", table));
		assertions.add(new AssertEquals("OudatedDataSourcesTests", "outdatedUniprot3",
			0, table.getRowCount(), "Outdated 'Uniprot/TrEMBL' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot4(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot4.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("OudatedDataSourcesTests", "outdatedUniprot4", table));
		assertions.add(new AssertEquals("OudatedDataSourcesTests", "outdatedUniprot4",
			0, table.getRowCount(), "Outdated 'UniProt' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> oldUniprotSwissProt(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/swissprot.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("OudatedDataSourcesTests", "oldUniprotSwissProt", table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "node") + ", " + table.get(i, "homepage") + "\n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals("OudatedDataSourcesTests", "oldUniprotSwissProt",
			0, errorCount, "Outdated 'Uniprot-SwissProt' data sources (use 'Uniprot-TrEMBL'): " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wrongPubChem(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "wrongPubChem");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/pubchem.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		// the metabolite test pathway has one outdated PubChem deliberately (WP2582)
		assertions.add(new AssertTrue(test,
			(table.getRowCount() <= 1), "Outdated 'PubChem' data sources (use 'PubChem-compound' or 'PubChem-substance')", "" + table
		));
		return assertions;
	}

}
