/* Copyright (C) 2025  Egon Willighagen <egon.willighagen@gmail.com>
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

import nl.unimaas.bigcat.wikipathways.curator.tests.UnicodeTests;

public class Unicode extends JUnitTests {

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
	public void water() throws Exception {
		performAssertions(UnicodeTests.water(helper, null));
	}

	@Test
	public void calcium() throws Exception {
		performAssertions(UnicodeTests.calcium(helper, null));
	}

	@Test
	public void hydron() throws Exception {
		performAssertions(UnicodeTests.hydron(helper, null));
	}

	@Test
	public void potassium() throws Exception {
		performAssertions(UnicodeTests.potassium(helper, null));
	}

	@Test
	public void sodium() throws Exception {
		performAssertions(UnicodeTests.sodium(helper, null));
	}

	@Test
	public void carbondioxide() throws Exception {
		performAssertions(UnicodeTests.carbondioxide(helper, null));
	}

	@Test
	public void oxygen() throws Exception {
		performAssertions(UnicodeTests.oxygen(helper, null));
	}

	@Test
	public void ozone() throws Exception {
		performAssertions(UnicodeTests.ozone(helper, null));
	}

	@Test
	public void iron2() throws Exception {
		performAssertions(UnicodeTests.iron2(helper, null));
	}

	@Test
	public void iron3() throws Exception {
		performAssertions(UnicodeTests.iron3(helper, null));
	}

	@Test
	public void copper() throws Exception {
		performAssertions(UnicodeTests.copper(helper, null));
	}

	@Test
	public void copper2() throws Exception {
		performAssertions(UnicodeTests.copper2(helper, null));
	}

	@Test
	public void manganese() throws Exception {
		performAssertions(UnicodeTests.manganese(helper, null));
	}

	@Test
	public void magnesium() throws Exception {
		performAssertions(UnicodeTests.magnesium(helper, null));
	}

	@Test
	public void ammonia() throws Exception {
		performAssertions(UnicodeTests.ammonia(helper, null));
	}

	@Test
	public void ammonium() throws Exception {
		performAssertions(UnicodeTests.ammonium(helper, null));
	}

	@Test
	public void bicarbonate() throws Exception {
		performAssertions(UnicodeTests.bicarbonate(helper, null));
	}

	@Test
	public void chloride() throws Exception {
		performAssertions(UnicodeTests.chloride(helper, null));
	}

}
