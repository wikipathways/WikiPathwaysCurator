/* Copyright (C) 2016-2025  Egon Willighagen <egon.willighagen@gmail.com>
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

import nl.unimaas.bigcat.wikipathways.curator.tests.InteractionTests;

public class Interactions extends JUnitTests {

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
	public void noMetaboliteToNonMetaboliteConversions() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.noMetaboliteToNonMetaboliteConversions(helper, null));
		});
	}

	@Test
	public void noNonMetaboliteToMetaboliteConversions() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
		try {
			performAssertions(InteractionTests.noNonMetaboliteToMetaboliteConversions(helper, null));
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
		});
	}

	@Test
	public void noGeneProteinConversions() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.noGeneProteinConversions(helper, null));
		});
	}

	@Test
	public void noProteinProteinConversions() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.noProteinProteinConversions(helper, null));
		});
	}

	@Test
	public void nonNumericIDs() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.nonNumericIDs(helper, null));
		});
	}

	@Tag("expertCuration")
	@Test
	public void interactionsWithLabels() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.interactionsWithLabels(helper, null));
		});
	}

	@Tag("expertCuration")
	@Test
	public void interactionsWithUnconnectedPoints() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.interactionsWithUnconnectedPoints(helper, null));
		});
	}

	@Tag("expertCuration")
	@Test
	public void possibleTranslocations() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.possibleTranslocations(helper, null));
		});
	}

	@Test
	public void incorrectKEGGIdentifiers() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
			performAssertions(InteractionTests.incorrectKEGGIdentifiers(helper, null));
		});
	}

}
