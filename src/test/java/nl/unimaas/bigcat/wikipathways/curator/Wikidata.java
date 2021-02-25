/* Copyright (C) 2018  Egon Willighagen <egon.willighagen@gmail.com>
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.tests.WikidataTests;

public class Wikidata extends JUnitTests {

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
			String parseErrors = OPSWPRDFFiles.getParseErrors();
			Assertions.assertNotNull(parseErrors);
			Assertions.assertEquals(0, parseErrors.length(), parseErrors.toString());
		}
	}

	@BeforeEach
	public void waitForIt() throws InterruptedException { Thread.sleep(OPSWPRDFFiles.SLEEP_TIME); }

	@Tag("expertCuration")
	@Test
	public void chebiWithoutMapping_Reactome() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.chebiWithoutMapping_Reactome(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void chebiWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.chebiWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void casWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.casWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void hmdbWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.hmdbWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void keggWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.keggWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void pubchemCIDWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.pubchemCIDWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void chemspiderCIDWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.chemspiderCIDWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void lipidMapsWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.lipidMapsWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void kNApSAcKWithoutMapping() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.kNApSAcKWithoutMapping(helper);
		performAssertions(assertions);
	}

	@Tag("wikidata")
	@Test
	public void replaceWikipedia() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.replaceWikipedia(helper);
		performAssertions(assertions);
	}

	@Test
	public void wikidataIdentifiersWrong() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.wikidataIdentifiersWrong(helper);
		performAssertions(assertions);
	}

	private static Set<String> allowedDuplicates = new HashSet<>();
	{{
		allowedDuplicates.add("7732-18-5"); // water + demi water
	}}

	@Tag("wikidata")
	@Test
	public void duplicateWikidataMappings() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.duplicateWikidataMappings(helper);
		performAssertions(assertions);
	}

	@Test
	public void wikDataTypo() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			List<IAssertion> assertions = WikidataTests.wikDataTypo(helper);
			performAssertions(assertions);
		});
	}

	@Tag("wikidata")
	@Tag("noCovid")
	@Test
	public void noWikidataForGenes() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = WikidataTests.noWikidataForGenes(helper);
		performAssertions(assertions);
	}
}
