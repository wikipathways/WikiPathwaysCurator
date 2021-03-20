/* Copyright (C) 2014,2018,2021  Egon Willighagen <egon.willighagen@gmail.com>
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
package nl.unimaas.bigcat.wikipathways.curator;

import java.time.Duration;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.tests.OudatedDataSourcesTests;

public class OudatedDataSources extends JUnitTests {

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
		}
	}

	@BeforeEach
	public void waitForIt() throws InterruptedException { Thread.sleep(OPSWPRDFFiles.SLEEP_TIME); }

	@Test
	public void outdatedUniprot() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
	        : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.outdatedUniprot(helper);
		performAssertions(assertions);
	}

	@Test
	public void outdatedUniprot2() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
	        : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.outdatedUniprot2(helper);
		performAssertions(assertions);
	}

	@Test
	public void outdatedUniprot3() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
	        : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.outdatedUniprot3(helper);
		performAssertions(assertions);
	}

	@Test
	public void outdatedUniprot4() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.outdatedUniprot4(helper);
		performAssertions(assertions);
	}

	@Test
	public void oldUniprotSwissProt() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.oldUniprotSwissProt(helper);
		performAssertions(assertions);
	}

	@Test
	public void wrongPubChem() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.wrongPubChem(helper);
		performAssertions(assertions);
	}

	@Test
	public void noInChIDataSourceYet() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.noInChIDataSourceYet(helper);
		performAssertions(assertions);
	}

	@Test
	public void outdatedKeggCompoundDataSource() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = OudatedDataSourcesTests.outdatedKeggCompoundDataSource(helper);
		performAssertions(assertions);
	}

	@Test
	public void outdatedKeggCompoundDataSource2() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedKeggCompoundDataSource2(helper);
			performAssertions(assertions);
		});
	}

	@Tag("outdated")
	@Test
	public void outdatedKeggOrthologDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedKeggOrthologDataSource(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedKeggEnzymeDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedKeggEnzymeDataSource(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedEnsemblMouseDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedEnsemblMouseDataSource(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedEnsemblCapsSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
				   : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedEnsemblCapsSource(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedEnsemblHumanDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedEnsemblHumanDataSource(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedEnsemblMouseDataSourceFromGPML() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedEnsemblMouseDataSourceFromGPML(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedEnsemblHumanDataSourceFromGPML() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedEnsemblHumanDataSourceFromGPML(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedEnsemblYeastDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = OudatedDataSourcesTests.outdatedEnsemblHumanDataSourceFromGPML(helper);
			performAssertions(assertions);
		});
	}

	@Test
	public void outdatedEnsemblCowDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl4.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'Ensembl Cow' data sources (use 'Ensembl'):\n" + table);
		});
	}

	@Test
	public void outdatedEnsemblChickenDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl_chicken.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'Ensembl Chicken' data sources (use 'Ensembl'):\n" + table);
		});
	}

	@Test
	public void outdatedECNumberDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ecNumber.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'EC Number' data sources (use 'Enzyme Nomenclature'):\n" + table);
		});
	}

	@Test
	public void outdatedChemSpiderDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/chemspider.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'ChemSpider' data sources (use 'Chemspider'):\n" + table);
		});
	}
}
