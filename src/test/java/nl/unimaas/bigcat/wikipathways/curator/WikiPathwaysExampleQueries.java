/* Copyright (C) 2015  Egon Willighagen <egon.willighagen@gmail.com>
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class WikiPathwaysExampleQueries {

	@Ignore(
		"This test should check that latest revision number and fail if not matching, because " +
		"the queries need testing then"
	)
	@BeforeClass
	public static void checkSynchedWithLatestWikiPageVersion() throws InterruptedException {
		// see ignore message
	}

	@BeforeClass
	public static void namespacesAreLoaded() throws Exception {
		String sparql = ResourceHelper.resourceAsString("wiki/namespacesAreLoaded.rq");
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertEquals(
			"The namespaces do not appear to be loaded",
			1, table.getRowCount()
		);
	}

	@Test
	public void voidInfo() throws Exception {
		String sparql = ResourceHelper.resourceAsString("wiki/voidInfo.rq");
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotSame(
			"Expected at least one data set to be specified with VoID",
			0, table.getRowCount()
		);
	}

	@Test
	public void getSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("wiki/getSpecies.rq");
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertTrue(
			"Expected at least 13 species",
			table.getRowCount() >= 13
		);
	}

	@Test
	public void allMousePathways() throws Exception {
		String sparql = ResourceHelper.resourceAsString("wiki/allMousePathways.rq");
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertTrue(
			"Expected at least 95 mouse pathways",
			table.getRowCount() >= 95
		);
	}

}
