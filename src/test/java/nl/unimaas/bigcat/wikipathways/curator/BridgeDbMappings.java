/* Copyright (C) 2013,2018  Egon Willighagen <egon.willighagen@gmail.com>
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
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BridgeDbMappings {

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
			String parseErrors = OPSWPRDFFiles.getParseErrors();
			Assert.assertNotNull(parseErrors);
			Assertions.assertEquals(0, parseErrors.length(), parseErrors.toString());
		}
	}
	
	@Test
	public void hasSomeEntrezGeneMappings() throws Exception {
		String sparql = ResourceHelper.resourceAsString("general/hasEntrezGeneMappings.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertNotSame(0, table.getRowCount(), () -> "Expected some mapped Entrez Genes.");
		});
	}

	@Test
	public void hasSomeHMDBMappings() throws Exception {
		String sparql = ResourceHelper.resourceAsString("general/hasHMDBMappings.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertNotSame(0, table.getRowCount(), () -> "Expected some mapped HMDB identifiers.");
		});
	}

	@Test
	public void hasSomeChemSpiderMappings() throws Exception {
		String sparql = ResourceHelper.resourceAsString("general/hasChemSpiderMappings.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertNotSame(0, table.getRowCount(), () -> "Expected some mapped ChemSpider identifiers.");
		});
	}

	@Test
	public void hasSomeEnsemblMappings() throws Exception {
		String sparql = ResourceHelper.resourceAsString("general/hasEnsemblMappings.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertNotSame(0, table.getRowCount(), () -> "Expected some mapped Ensembl identifiers.");
		});
	}

	@Test
	public void hasSomeUniprotMappings() throws Exception {
		String sparql = ResourceHelper.resourceAsString("general/hasUniprotMappings.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertNotSame(0, table.getRowCount(), () -> "Expected some mapped Uniprot identifiers.");
		});
	}
}
