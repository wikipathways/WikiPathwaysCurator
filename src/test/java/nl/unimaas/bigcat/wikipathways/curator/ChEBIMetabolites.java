/* Copyright (C) 2018  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChEBIMetabolites {

	private static Map<String,String> oldToNew = new HashMap<String, String>();

	@BeforeClass
	public static void loadData() throws InterruptedException {
		OPSWPRDFFiles.loadData();
		Model data = OPSWPRDFFiles.loadData();
		Assert.assertTrue(data.size() > 5000);

		// now load the deprecation data
		String deprecatedData = ResourceHelper.resourceAsString("metabolite/chebi/deprecated.csv");
		String lines[] = deprecatedData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			oldToNew.put(ids[0], ids[1]);
		}
		System.out.println("size: " + oldToNew.size());
	}

	@Test(timeout=20000)
	public void secondaryChEBIIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("metabolite/chebiNumberNotMarkedAsMetabolite.rq");
		StringMatrix table = SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (identifier.startsWith("CHEBI:")) {
					identifier = identifier.substring(7);
				}
				if (oldToNew.containsKey(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " but has primary identifier CHEBI:" +
						oldToNew.get(identifier) + "\n";
				}
			}
		}
		Assert.assertEquals(
			"Secondary ChEBI identifiers detected:\n" + errors,
			0, errorCount
		);
	}

}
