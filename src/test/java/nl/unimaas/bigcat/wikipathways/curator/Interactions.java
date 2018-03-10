/* Copyright (C) 2016,2018  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;

public class Interactions {

	@BeforeClass
	public static void loadData() throws InterruptedException {
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);
	}

	@Test(timeout=30000)
	public void noMetaboliteToNonMetaboliteConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noMetaboliteNonMetaboliteConversions.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Set<String> allowedProteinProducts = new HashSet<String>();
		allowedProteinProducts.add("http://identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String targetID = table.get(i, "target");
				if (!allowedProteinProducts.contains(targetID)) {
  				    errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
				        table.get(i, "metabolite") + " " + table.get(i, "target") + " " +
				        table.get(i, "interaction") + "\n";
				    errorCount++;
				} // else, OK, this is allows as conversion target
			}
		}
		Assert.assertEquals(
			"Unexpected metabolite to non-metabolite conversions:\n" + errors,
			0, errorCount
		);
	}

	@Test(timeout=30000)
	public void noNonMetaboliteToMetaboliteConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noNonMetaboliteMetaboliteConversions.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		Set<String> allowedProducts = new HashSet<String>();
		allowedProducts.add("http://identifiers.org/hmdb/HMDB04246"); // from KNG1, e.g. in WP
		Set<String> allowedProteinSubstrates = new HashSet<String>();
		allowedProteinSubstrates.add("http://identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String metabolite = table.get(i, "metabolite");
				String nonmetabolite = table.get(i, "target");
				if (!allowedProducts.contains(metabolite) &&
					!allowedProteinSubstrates.contains(nonmetabolite)) {
				    errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
				    		nonmetabolite + " " + metabolite + " " +
				        table.get(i, "interaction") + "\n";
				    errorCount++;
				}
			}
		}
		Assert.assertEquals(
			"Unexpected non-metabolite to metabolite conversions:\n" + errors,
			0, errorCount
		);
	}

	@Test(timeout=30000)
	public void noGeneProteinConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noGeneProteinConversions.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
				  table.get(i, "protein") + " " + table.get(i, "gene") + " " +
				  table.get(i, "interaction") + " Did you mean wp:TranscriptionTranslation?\n";
				errorCount++;
			}
		}
		Assert.assertEquals(
			"Unexpected gene-protein conversions:\n" + errors,
			0, errorCount
		);
	}

	@Test(timeout=30000)
	public void noProteinProteinConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noProteinProteinConversions.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
				  table.get(i, "protein1") + " " + table.get(i, "protein2") + " " +
				  table.get(i, "interaction") + "\n";
				errorCount++;
			}
		}
		Assert.assertEquals(
			"Unexpected protein-protein conversions:\n" + errors,
			0, errorCount
		);
	}
}
