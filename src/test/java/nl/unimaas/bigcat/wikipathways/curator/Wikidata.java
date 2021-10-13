/* Copyright (C) 2018,2021  Egon Willighagen <egon.willighagen@gmail.com>
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
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.tests.WikidataTests;

public class Wikidata extends JUnitTests {

	private static SPARQLHelper helper = null;

	@BeforeAll
	public static void loadData() throws InterruptedException {
		helper = (System.getProperty("SPARQLEP").startsWith("http"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
			: new SPARQLHelper(OPSWPRDFFiles.loadData());
		Assertions.assertTrue(helper.size() > 5000);
		String parseErrors = OPSWPRDFFiles.getParseErrors();
		Assertions.assertNotNull(parseErrors);
		Assertions.assertEquals(0, parseErrors.length(), parseErrors.toString());
	}

	@BeforeEach
	public void waitForIt() throws InterruptedException { Thread.sleep(OPSWPRDFFiles.SLEEP_TIME); }

	@Tag("expertCuration")
	@Test
	public void chebiWithoutMapping_Reactome() throws Exception {
		performAssertions(WikidataTests.chebiWithoutMapping_Reactome(helper));
	}

	@Tag("wikidata")
	@Test
	public void chebiWithoutMapping() throws Exception {
		performAssertions(WikidataTests.chebiWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void casWithoutMapping() throws Exception {
		performAssertions(WikidataTests.casWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void hmdbWithoutMapping() throws Exception {
		performAssertions(WikidataTests.hmdbWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void keggWithoutMapping() throws Exception {
		performAssertions(WikidataTests.keggWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void pubchemCIDWithoutMapping() throws Exception {
		performAssertions(WikidataTests.pubchemCIDWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void chemspiderCIDWithoutMapping() throws Exception {
		performAssertions(WikidataTests.chemspiderCIDWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void lipidMapsWithoutMapping() throws Exception {
		performAssertions(WikidataTests.lipidMapsWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void kNApSAcKWithoutMapping() throws Exception {
		performAssertions(WikidataTests.kNApSAcKWithoutMapping(helper));
	}

	@Tag("wikidata")
	@Test
	public void replaceWikipedia() throws Exception {
		performAssertions(WikidataTests.replaceWikipedia(helper));
	}

	@Test
	public void wikidataIdentifiersWrong() throws Exception {
		performAssertions(WikidataTests.wikidataIdentifiersWrong(helper));
	}

	@Tag("wikidata")
	@Test
	public void duplicateWikidataMappings() throws Exception {
		performAssertions(WikidataTests.duplicateWikidataMappings(helper));
	}

	@Test
	public void wikDataTypo() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(WikidataTests.wikDataTypo(helper));
		});
	}

	@Tag("wikidata")
	@Tag("noCovid")
	@Test
	public void noWikidataForGenes() throws Exception {
		performAssertions(WikidataTests.noWikidataForGenes(helper));
	}
}
