/* Copyright (C) 2014,2018  Egon Willighagen <egon.willighagen@gmail.com>
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

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class OudatedDataSources {

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
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(0, table.getRowCount(), "Outdated 'Uniprot' data sources (use 'Uniprot-TrEMBL'):\n" + table);
		});
	}

	@Test
	public void outdatedUniprot2() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot2.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(0, table.getRowCount(), "Outdated 'UniProt/TrEMBL' data sources (use 'Uniprot-TrEMBL'):\n" + table);
		});
	}

	@Test
	public void outdatedUniprot3() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot3.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(0, table.getRowCount(), "Outdated 'Uniprot/TrEMBL' data sources (use 'Uniprot-TrEMBL'):\n" + table);
		});
	}

	@Test
	public void outdatedUniprot4() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot4.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(0, table.getRowCount(), "Outdated 'UniProt' data sources (use 'Uniprot-TrEMBL'):\n" + table);
		});
	}

	@Test
	public void oldUniprotSwissProt() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/swissprot.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(0, table.getRowCount(), "Outdated 'Uniprot-SwissProt' data sources (use 'Uniprot-TrEMBL'):\n" + table);
		});
	}

	@Test
	public void wrongPubChem() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/pubchem.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			// the metabolite test pathway has one outdated PubChem deliberately (WP2582)
			Assertions.assertTrue(table.getRowCount() <= 1, "Outdated 'PubChem' data sources (use 'PubChem-compound' or 'PubChem-substance'):\n" + table);
		});
	}

	@Test
	public void noInChIDataSourceYet() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/inchi.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(0, table.getRowCount(), "Don't use 'InChI' data sources yet, but found:\n" + table);
		});
	}

	@Test
	public void noInChIKeyDataSourceYet() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/inchikey.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(0, table.getRowCount(), "Don't use 'InChIKey' data sources yet, but found:\n" + table);
		});
	}

	@Test
	public void outdatedKeggCompoundDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/keggcompound.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
			Assertions.assertTrue(table.getRowCount() <= 1, "Outdated 'Kegg Compound' data sources (use 'KEGG Compound'):\n" + table);
		});
	}

	@Test
	public void outdatedKeggCompoundDataSource2() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/keggcompound2.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
			Assertions.assertTrue(table.getRowCount() <= 1, "Outdated 'kegg.compound' data sources (use 'KEGG Compound'):\n" + table);
		});
	}

	@Tag("outdated")
	@Test
	public void outdatedKeggOrthologDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/keggortholog.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
			Assertions.assertTrue(table.getRowCount() <= 1, "Outdated 'Kegg ortholog' data sources:\n" + table);
		});
	}

	@Test
	public void outdatedKeggEnzymeDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/keggenzyme.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			// the metabolite test pathway has one outdated Kegg enzyme deliberately (WP2582)
			Assertions.assertTrue(table.getRowCount() <= 1, "Outdated 'Kegg enzyme' data sources:\n" + table);
		});
	}

	@Test
	public void outdatedEnsemblMouseDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'Ensembl Mouse' data sources (use 'Ensembl'):\n" + table);
		});
	}

	@Test
	public void outdatedEnsemblCapsSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensemblCaps.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'ENSEMBL' data sources (use 'Ensembl'):\n" + table);
		});
	}

	@Test
	public void outdatedEnsemblHumanDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl2.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'Ensembl Human' data sources (use 'Ensembl'):\n" + table);
		});
	}

	@Test
	public void outdatedEnsemblMouseDataSourceFromGPML() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl_gpml.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'Ensembl Mouse' data sources (use 'Ensembl'):\n" + table);
		});
	}

	@Test
	public void outdatedEnsemblHumanDataSourceFromGPML() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl2_gpml.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'Ensembl Human' data sources (use 'Ensembl'):\n" + table);
		});
	}

	@Test
	public void outdatedEnsemblYeastDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl3.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
					: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Outdated 'Ensembl Yeast' data sources (use 'Ensembl'):\n" + table);
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
