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

import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChemSpiderMetabolites {

	@SuppressWarnings("serial")
	private static final ArrayList<Integer> deprecated = new ArrayList<Integer>() {{
		add(Integer.valueOf(358));
	}};

	@BeforeClass
	public static void loadData() throws InterruptedException {
		OPSWPRDFFiles.loadData();
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);
	}

	@Test(timeout=20000)
	public void outdatedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allChemSpiderIdentifiers.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer csid = Integer.valueOf(identifier);
					if (deprecated.contains(csid)) {
						errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
						errorCount++;
					}
				} catch (NumberFormatException exception) {
					// ignore, we got chemSpiderIDsNotNumbers() for this
				}
			}
		}
		Assert.assertEquals(
			"Deprecated ChemSpider identifiers for non-metabolites:\n" + errors,
			0, errorCount
		);
	}

	@Test
	public void chemSpiderIDsNotNumbers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/allChemSpiderIdentifiers.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		Assert.assertNotNull(table);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				try {
					Integer.parseInt(identifier);
				} catch (NumberFormatException exception) {
					errors += table.get(i, "homepage") + table.get(i, "label") + table.get(i, "identifier");
					errorCount++;
				}
			}
		}
		Assert.assertEquals(
			"ChemSpider identifiers that are not integers:\n" + errors,
			0, errorCount
		);
	}
}
