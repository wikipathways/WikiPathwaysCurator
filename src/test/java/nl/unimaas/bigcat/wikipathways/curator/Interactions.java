/* Copyright (C) 2016  Egon Willighagen <egon.willighagen@gmail.com>
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

import org.apache.jena.rdf.model.Model;

public class Interactions {

	@BeforeClass
	public static void loadData() throws InterruptedException {
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);
	}

	@Test
	public void noGeneGeneConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noGeneGeneConversions.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "organism") + " " + table.get(i, "page") + " -> " +
				  table.get(i, "protein1") + " " + table.get(i, "protein2") + " " +
				  table.get(i, "interaction") + "\n";
				errorCount++;
			}
		}
		Assert.assertEquals(
			"Unexpected gene-gene conversions:\n" + errors,
			0, errorCount
		);
	}

	@Test
	public void noGeneProteinConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noGeneProteinConversions.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "organism") + " " + table.get(i, "page") + " -> " +
				  table.get(i, "protein1") + " " + table.get(i, "protein2") + " " +
				  table.get(i, "interaction") + "\n";
				errorCount++;
			}
		}
		Assert.assertEquals(
			"Unexpected gene-protein conversions:\n" + errors,
			0, errorCount
		);
	}

	@Test
	public void noProteinProteinConversions() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactions/noProteinProteinConversions.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				errors += table.get(i, "organism") + " " + table.get(i, "page") + " -> " +
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
