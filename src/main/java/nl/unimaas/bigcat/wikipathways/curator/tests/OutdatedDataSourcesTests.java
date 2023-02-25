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

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertTrue;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class OutdatedDataSourcesTests {

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(outdatedUniprot(helper));
		assertions.addAll(outdatedUniprot2(helper));
		assertions.addAll(outdatedUniprot3(helper));
		assertions.addAll(outdatedUniprot4(helper));
		assertions.addAll(oldUniprotSwissProt(helper));
		assertions.addAll(wrongPubChem(helper));
		assertions.addAll(noInChIDataSourceYet(helper));
		assertions.addAll(outdatedKeggCompoundDataSource(helper));
		assertions.addAll(outdatedKeggCompoundDataSource2(helper));
		assertions.addAll(outdatedKeggOrthologDataSource(helper));
		assertions.addAll(outdatedKeggEnzymeDataSource(helper));
		assertions.addAll(outdatedEnsemblMouseDataSource(helper));
		assertions.addAll(outdatedEnsemblCapsSource(helper));
		assertions.addAll(outdatedEnsemblHumanDataSource(helper));
		assertions.addAll(outdatedEnsemblMouseDataSourceFromGPML(helper));
		assertions.addAll(outdatedEnsemblHumanDataSourceFromGPML(helper));
		assertions.addAll(outdatedEnsemblYeastDataSource(helper));
		assertions.addAll(outdatedEnsemblCowDataSource(helper));
		assertions.addAll(outdatedEnsemblChickenDataSource(helper));
		assertions.addAll(outdatedECNumberDataSource(helper));
		assertions.addAll(outdatedChemSpiderDataSource(helper));
		assertions.addAll(gpml2021transition(helper));
		assertions.addAll(discontinuedUniGene(helper));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedUniprot");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Outdated 'Uniprot' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot2(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedUniprot2");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot2.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Outdated 'UniProt/TrEMBL' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot3(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedUniprot3");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot3.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Outdated 'Uniprot/TrEMBL' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> outdatedUniprot4(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedUniprot4");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot4.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Outdated 'UniProt' data sources (use 'Uniprot-TrEMBL')"
		));
		return assertions;
	}

	public static List<IAssertion> oldUniprotSwissProt(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "oldUniprotSwissProt");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/swissprot.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "node") + ", " + table.get(i, "homepage") + "\n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Outdated 'Uniprot-SwissProt' data sources (use 'Uniprot-TrEMBL'): " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> wrongPubChem(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "wrongPubChem");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/pubchem.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		// the metabolite test pathway has one outdated PubChem deliberately (WP2582)
		assertions.add(new AssertTrue(test,
			(table.getRowCount() <= 1), "Outdated 'PubChem' data sources (use 'PubChem-compound' or 'PubChem-substance')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> noInChIDataSourceYet(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "noInChIDataSourceYet");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/inchi.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertEquals(test,
			0, table.getRowCount(), "Don't use 'InChI' data sources yet, but found: " + table.getRowCount(), "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedKeggCompoundDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedKeggCompoundDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/keggcompound.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
		assertions.add(new AssertTrue(test,
			(table.getRowCount() <= 1), "Outdated 'Kegg Compound' data sources (use 'KEGG Compound')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedKeggCompoundDataSource2(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedKeggCompoundDataSource2");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/keggcompound2.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
		assertions.add(new AssertTrue(test,
			(table.getRowCount() <= 1), "Outdated 'kegg.compound' data sources (use 'KEGG Compound')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedKeggOrthologDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedKeggOrthologDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/keggortholog.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
		assertions.add(new AssertTrue(test,
			(table.getRowCount() <= 1), "Outdated 'Kegg ortholog' data sources", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedKeggEnzymeDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedKeggEnzymeDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/keggenzyme.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		// the metabolite test pathway has one outdated Kegg enzyme deliberately (WP2582)
		assertions.add(new AssertTrue(test,
			(table.getRowCount() <= 1), "Outdated 'Kegg enzyme' data sources", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblMouseDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblMouseDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() <= 1), "Outdated 'Ensembl Mouse' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblCapsSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblCapsSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensemblCaps.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'ENSEMBL' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblHumanDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblHumanDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl2.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'Ensembl Human' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblMouseDataSourceFromGPML(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblMouseDataSourceFromGPML");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl_gpml.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'Ensembl Mouse' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblHumanDataSourceFromGPML(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblHumanDataSourceFromGPML");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl2_gpml.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'Ensembl Human' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblYeastDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblYeastDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl3.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'Ensembl Yeast' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblCowDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblCowDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl4.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'Ensembl Cow' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedEnsemblChickenDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedEnsemblChickenDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl_chicken.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'Ensembl Chicken' data sources (use 'Ensembl')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedECNumberDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedECNumberDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/ecNumber.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'EC Number' data sources (use 'Enzyme Nomenclature')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> outdatedChemSpiderDataSource(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "outdatedChemSpiderDataSource");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/chemspider.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "Outdated 'ChemSpider' data sources (use 'Chemspider')", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> gpml2021transition(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "gpml2021transition");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/gpml2021transition.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertTrue(test,
			(table.getRowCount() < 1), "More outdated data sources", "" + table
		));
		return assertions;
	}

	public static List<IAssertion> discontinuedUniGene(SPARQLHelper helper) throws Exception {
		Test test = new Test("OudatedDataSourcesTests", "discontinuedUniGene");
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("outdated/unigene.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "node") + ", " + table.get(i, "homepage") + "\n";
				errorCount++;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "The 'UniGene' database no longer exists, but used " + errorCount + " times", errors
		));
		return assertions;
	}

}
