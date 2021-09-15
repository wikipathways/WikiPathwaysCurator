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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.tests.LIPIDMAPSTests;
import nl.unimaas.bigcat.wikipathways.curator.tests.MetabolitesTests;

public class Metabolites extends JUnitTests {

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

	@Test
	public void metaboliteAlsoOtherType() throws Exception {
		performAssertions(MetabolitesTests.metaboliteAlsoOtherType(helper));
	}

	@Test
	public void casNumbersNotMarkedAsMetabolite() throws Exception {
		performAssertions(MetabolitesTests.casNumbersNotMarkedAsMetabolite(helper));
	}

	@Test
	public void chemspiderIDsNotMarkedAsMetabolite() throws Exception {
		performAssertions(MetabolitesTests.chemspiderIDsNotMarkedAsMetabolite(helper));
	}

	@Test
	public void ChEBIIDsNotMarkedAsMetabolite() throws Exception {
		performAssertions(MetabolitesTests.ChEBIIDsNotMarkedAsMetabolite(helper));
	}

	@Test
	public void HMDBIDsNotMarkedAsMetabolite() throws Exception {
		performAssertions(MetabolitesTests.HMDBIDsNotMarkedAsMetabolite(helper));
	}

	@Test
	public void KEGGIDsNotMarkedAsMetabolite() throws Exception {
		performAssertions(MetabolitesTests.KEGGIDsNotMarkedAsMetabolite(helper));
	}

	@Test
	public void PubChemIDsNotMarkedAsMetabolite() throws Exception {
		performAssertions(MetabolitesTests.PubChemIDsNotMarkedAsMetabolite(helper));
	}

	@Test
	public void PubChemSubstanceIDsNotMarkedAsMetabolite() throws Exception {
		performAssertions(MetabolitesTests.PubChemSubstanceIDsNotMarkedAsMetabolite(helper));
	}

	@Test
	public void PubChemIDsNotNumbers() throws Exception {
		performAssertions(MetabolitesTests.PubChemIDsNotNumbers(helper));
	}

	@Test
	public void PubChemSubstanceIDsNotNumbers() throws Exception {
		performAssertions(MetabolitesTests.PubChemSubstanceIDsNotNumbers(helper));
	}

	@Test
	public void metabolitesWithIdentifierButNoDb() throws Exception {
		performAssertions(MetabolitesTests.metabolitesWithIdentifierButNoDb(helper));
	}

	@Test
	public void metabolitesWithDbButNoIdentifier() throws Exception {
		performAssertions(MetabolitesTests.metabolitesWithDbButNoIdentifier(helper));
	}

	@Test
	public void metabolitesWithAnEntrezGeneID() throws Exception {
		performAssertions(MetabolitesTests.metabolitesWithAnEntrezGeneID(helper));
	}

	@Test
	public void metabolitesWithAnEnsembleID() throws Exception {
		performAssertions(MetabolitesTests.metabolitesWithAnEnsembleID(helper));
	}

	@Test
	public void retiredIdentifiers() throws Exception {
		performAssertions(LIPIDMAPSTests.retiredIdentifiers(helper));
	}
}
