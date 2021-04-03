/* Copyright (C) 2013,2018-2019  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.tests.GeneralTests;

public class General extends JUnitTests {

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
	@Tag("void")
	public void recentness() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		performAssertions(GeneralTests.recentness(helper));
	}

	@Test
	public void nullDataSources() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		performAssertions(GeneralTests.nullDataSources(helper));
	}

	@Test
	public void undefinedDataSources() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		performAssertions(GeneralTests.undefinedDataSources(helper));
	}

	@Test
	public void undefinedIdentifier() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		performAssertions(GeneralTests.undefinedIdentifier(helper));
	}

	@Test
	public void emptyLabelOfNodeWithIdentifier() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		performAssertions(GeneralTests.emptyLabelOfNodeWithIdentifier(helper));
	}

	@Test
	public void dataNodeWithoutGraphId() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		performAssertions(GeneralTests.dataNodeWithoutGraphId(helper));
	}

	@Test
	public void groupsHaveDetail() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		performAssertions(GeneralTests.groupsHaveDetail(helper));
	}

	@Test
	public void titlesShortEnough() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = GeneralTests.titlesShortEnough(helper);
		performAssertions(assertions);
	}

	@Test
	@Tag("expertCuration")
	public void weirdCharacterTitles() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = GeneralTests.weirdCharacterTitles(helper);
		performAssertions(assertions);
	}

	@Test
	@Tag("expertCuration")
	public void duplicateTitles() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = GeneralTests.duplicateTitles(helper);
		performAssertions(assertions);
	}

	@Test
	@Tag("expertCuration")
	public void curationAndNeedsWork() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = GeneralTests.curationAndNeedsWork(helper);
		performAssertions(assertions);
	}

	@Test
	public void curationAndReactome() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = GeneralTests.curationAndReactome(helper);
		performAssertions(assertions);
	}

	@Test
	@Tag("expertCuration")
	public void noTags() throws Exception {
		SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
			? new SPARQLHelper(System.getProperty("SPARQLEP"))
		    : new SPARQLHelper(OPSWPRDFFiles.loadData());
		List<IAssertion> assertions = GeneralTests.noTags(helper);
		performAssertions(assertions);
	}

}
