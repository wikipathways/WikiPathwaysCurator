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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.tests.OutdatedDataSourcesTests;

public class OutdatedDataSources extends JUnitTests {

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
	public void outdatedUniprot() throws Exception {
		performAssertions(OutdatedDataSourcesTests.outdatedUniprot(helper));
	}

	@Test
	public void outdatedUniprot2() throws Exception {
		performAssertions(OutdatedDataSourcesTests.outdatedUniprot2(helper));
	}

	@Test
	public void outdatedUniprot3() throws Exception {
		performAssertions(OutdatedDataSourcesTests.outdatedUniprot3(helper));
	}

	@Test
	public void outdatedUniprot4() throws Exception {
		performAssertions(OutdatedDataSourcesTests.outdatedUniprot4(helper));
	}

	@Test
	public void oldUniprotSwissProt() throws Exception {
		performAssertions(OutdatedDataSourcesTests.oldUniprotSwissProt(helper));
	}

	@Test
	public void wrongPubChem() throws Exception {
		performAssertions(OutdatedDataSourcesTests.wrongPubChem(helper));
	}

	@Test
	public void noInChIDataSourceYet() throws Exception {
		performAssertions(OutdatedDataSourcesTests.noInChIDataSourceYet(helper));
	}

	@Test
	public void outdatedKeggCompoundDataSource() throws Exception {
		performAssertions(OutdatedDataSourcesTests.outdatedKeggCompoundDataSource(helper));
	}

	@Test
	public void outdatedKeggCompoundDataSource2() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedKeggCompoundDataSource2(helper));
		});
	}

	@Tag("outdated")
	@Test
	public void outdatedKeggOrthologDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedKeggOrthologDataSource(helper));
		});
	}

	@Test
	public void outdatedKeggEnzymeDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedKeggEnzymeDataSource(helper));
		});
	}

	@Test
	public void outdatedEnsemblMouseDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblMouseDataSource(helper));
		});
	}

	@Test
	public void outdatedEnsemblCapsSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblCapsSource(helper));
		});
	}

	@Test
	public void outdatedEnsemblHumanDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblHumanDataSource(helper));
		});
	}

	@Test
	public void outdatedEnsemblMouseDataSourceFromGPML() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblMouseDataSourceFromGPML(helper));
		});
	}

	@Test
	public void outdatedEnsemblHumanDataSourceFromGPML() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblHumanDataSourceFromGPML(helper));
		});
	}

	@Test
	public void outdatedEnsemblYeastDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblYeastDataSource(helper));
		});
	}

	@Test
	public void outdatedEnsemblCowDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblCowDataSource(helper));
		});
	}

	@Test
	public void outdatedEnsemblChickenDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedEnsemblChickenDataSource(helper));
		});
	}

	@Test
	public void outdatedECNumberDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedECNumberDataSource(helper));
		});
	}

	@Test
	public void outdatedChemSpiderDataSource() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.outdatedChemSpiderDataSource(helper));
		});
	}

	@Test
	public void discontinuedUniGene() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			performAssertions(OutdatedDataSourcesTests.discontinuedUniGene(helper));
		});
	}
}
