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
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

public class EnsemblGenes {

	@BeforeClass
	public static void loadData() throws InterruptedException {
		OPSWPRDFFiles.loadData();
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);
	}

	@Test(timeout=20000)
	public void wrongEnsemblIDForHumanSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("ensemblGenesWrongSpecies_Human.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a human pathway:\n" + table,
			0, table.getRowCount()
		);
	}

	@Test(timeout=20000)
	public void wrongEnsemblIDForRatSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("ensemblGenesWrongSpecies_Rat.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a rat pathway:\n" + table,
			0, table.getRowCount()
		);
	}

	@Test(timeout=20000)
	public void wrongEnsemblIDForMouseSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("ensemblGenesWrongSpecies_Mouse.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a mouse pathway:\n" + table,
			0, table.getRowCount()
		);
	}

	@Test(timeout=20000)
	public void wrongEnsemblIDForCowSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("ensemblGenesWrongSpecies_Cow.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(
			"Ensembl identifiers for wrong species for a cow pathway:\n" + table,
			0, table.getRowCount()
		);
	}

}
