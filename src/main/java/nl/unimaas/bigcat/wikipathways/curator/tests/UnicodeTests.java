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
package nl.unimaas.bigcat.wikipathways.curator.tests;

import java.util.ArrayList;
import java.util.List;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class UnicodeTests {

	public static List<IAssertion> all(SPARQLHelper helper, String format) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(water(helper, format));
		assertions.addAll(calcium(helper, format));
		assertions.addAll(hydron(helper, format));
		assertions.addAll(sodium(helper, format));
		assertions.addAll(potassium(helper, format));
		return assertions;
	}

	public static List<IAssertion> water(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "water", "Water chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/water.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = table.getRowCount();
		for (int i=1; i<=errorCount; i++) {
			if ("text/markdown".equals(format)) {
				errors += "* " + asMarkdownLink(table.get(i, "homepage")) + "\n" ;
			} else {
				errors += table.get(i, "homepage") + "\n" ;
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Metabolite can use H₂O instead of H2O: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> calcium(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "calcium", "Calcium chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/calcium.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = table.getRowCount();
		for (int i=1; i<=errorCount; i++) {
			if ("text/markdown".equals(format)) {
				errors += "* " + asMarkdownLink(table.get(i, "homepage")) + "\n" ;
			} else {
				errors += table.get(i, "homepage") + "\n" ;
			}
		}
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Metabolite can use Ca²⁺ instead of Ca2+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> hydron(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "hydron", "Hydron chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/hydron.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = table.getRowCount();
		for (int i=1; i<=errorCount; i++) {
			if ("text/markdown".equals(format)) {
				errors += "* " + asMarkdownLink(table.get(i, "homepage")) + "\n" ;
			} else {
				errors += table.get(i, "homepage") + "\n" ;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Metabolite can use H⁺ instead of H+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> potassium(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "potassium", "Potassium chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/potassium.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = table.getRowCount();
		for (int i=1; i<=errorCount; i++) {
			if ("text/markdown".equals(format)) {
				errors += "* " + asMarkdownLink(table.get(i, "homepage")) + "\n" ;
			} else {
				errors += table.get(i, "homepage") + "\n" ;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Metabolite can use K⁺ instead of K+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> sodium(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "sodium", "Sodium chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/sodium.rq");
		StringMatrix table = SPARQLHelper.classicify(helper.sparql(sparql), "homepage");
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = table.getRowCount();
		for (int i=1; i<=errorCount; i++) {
			if ("text/markdown".equals(format)) {
				errors += "* " + asMarkdownLink(table.get(i, "homepage")) + "\n" ;
			} else {
				errors += table.get(i, "homepage") + "\n" ;
			}
		}
		assertions.add(new AssertEquals(test,
			0, errorCount, "Metabolite can use Na⁺ instead of Na+: " + errorCount, errors, format
		));
		return assertions;
	}

	private static String asMarkdownLink(String url) {
		if (url.startsWith("http://classic.wikipathways.org/")) url = url.replace("_rr","_r"); // yeah, silly workaround
		return "[" + url + "](" + url + ")";
	}

}
