/* Copyright (C) 2013  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.util.HashSet;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

public class Metabolites {

	@BeforeClass
	public static void loadData() throws InterruptedException {
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);
	}
	
	@Test
	public void casNumbersNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/casNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Set<String> allowedProteins = new HashSet<String>();
		allowedProteins.add("IFN-b");
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=0; i<table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
				}
			}
		}
		Assert.assertEquals(
			"Unexpected CAS identifiers for non-metabolites:\n" + errors,
			0, errors.length()
		);
	}

	@Test
	public void chemspiderIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/chemspiderNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Unexpected ChemSpider identifiers for non-metabolites:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void ChEBIIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/chebiNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Set<String> allowed = new HashSet<String>();
		allowed.add("CHEBI:15986"); // polynucleotide
		allowed.add("CHEBI:9160");  // single stranded DNA
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=0; i<table.getRowCount(); i++) {
				if (!allowed.contains(table.get(i, "identifier").trim())) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
				}
			}
		}
		Assert.assertEquals(
			"Unexpected ChEBI identifiers for non-metabolites:\n" + errors,
			0, errors.length()
		);
	}

	@Test
	public void HMDBIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/hmdbNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Unexpected HMDB identifiers for non-metabolites:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void KEGGIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/keggNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Unexpected KEGG identifiers for non-metabolites:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void PubChemIDsNotMarkedAsMetabolite() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/pubchemNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Set<String> allowedProteins = new HashSet<String>();
		allowedProteins.add("Fibrin");
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=0; i<table.getRowCount(); i++) {
				if (!allowedProteins.contains(table.get(i, "label").trim())) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
				}
			}
		}
		Assert.assertEquals(
			"Unexpected PubChem identifiers for non-metabolites:\n" + errors,
			0, errors.length()
		);
	}

	@Test
	public void PubChemIDsNotNumbers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allPubChemIdentifiers.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=0; i<table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer.parseInt(identifier);
				} catch (NumberFormatException exception) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
				}
			}
		}
		Assert.assertEquals(
			"Unexpected PubChem identifiers for non-metabolites:\n" + errors,
			0, errors.length()
		);
	}

	@Test
	public void metabolitesWithIdentifierButNoDb() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithIdentifierButNoDatabase.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Unexpected metabolites with identifier but no database source:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void metabolitesWithDbButNoIdentifier() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithDatabaseButNoIdentifier.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Unexpected metabolites with identifier but no database source:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void metabolitesWithAnEntrezGeneID() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithAnEntrezGeneID.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Unexpected metabolites with an Entrez Gene identifier:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void metabolitesWithAnEnsembleID() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/metabolitesWithAnEnsembleID.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Unexpected metabolites with an Ensemble identifier:\n" + table, 0, table.getRowCount());
	}
}
