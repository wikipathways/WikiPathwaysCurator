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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.jena.rdf.model.Model;

public class References {

	@BeforeClass
	public static void loadData() throws InterruptedException {
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);
	}
	
	@Test
	public void nonNumericPubMedIDs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("references/nonNumericPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		String errors = "";
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					try {
						Integer.parseInt(id);
					} catch (NumberFormatException exception) {
						errors += table.get(i, "homepage") + ", " +
								table.get(i, "id") + "\n";
					}
				}
			}
		}
		Assert.assertEquals(
			"Found PubMed IDs that are not numbers:\n" + errors,
			0, errors.length()
		);
	}

	@Test
	public void zeroPubMedIDs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("references/nonNumericPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		String errors = "";
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String id = table.get(i, "id");
				if (id != null && id.length() > 0) {
					try {
						if (Integer.parseInt(id) == 0) {
							errors += table.get(i, "homepage") + ", " +
									table.get(i, "id") + "\n";
						}
					} catch (NumberFormatException exception) {
						// already reporting these in nonNumericPubMedIDs()
					}
				}
			}
		}
		Assert.assertEquals(
			"Found '0's as PubMed IDs:\n" + errors,
			0, errors.length()
		);
	}
}
