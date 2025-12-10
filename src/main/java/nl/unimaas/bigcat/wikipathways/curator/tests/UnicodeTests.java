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
		assertions.addAll(oxygen(helper, format));
		assertions.addAll(ozone(helper, format));
		assertions.addAll(carbondioxide(helper, format));
		assertions.addAll(iron2(helper, format));
		assertions.addAll(iron3(helper, format));
		assertions.addAll(copper(helper, format));
		assertions.addAll(copper2(helper, format));
		assertions.addAll(manganese(helper, format));
		assertions.addAll(magnesium(helper, format));
		assertions.addAll(ammonia(helper, format));
		assertions.addAll(ammonium(helper, format));
		assertions.addAll(chloride(helper, format));
		assertions.addAll(bicarbonate(helper, format));
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

	public static List<IAssertion> carbondioxide(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "carbondioxide", "The carbon dioxide chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/carbondioxide.rq");
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
			0, errorCount, "Metabolite can use CO₂ instead of CO2: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> ozone(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "ozone", "The ozone chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/ozone.rq");
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
			0, errorCount, "Metabolite can use O₃ instead of O3: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> oxygen(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "oxygen", "The oxygen chemical formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/oxygen.rq");
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
			0, errorCount, "Metabolite can use O₂ instead of O2: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> iron2(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "iron2", "The iron ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/iron2.rq");
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
			0, errorCount, "Metabolite can use Fe²⁺ instead of Fe2+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> iron3(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "iron3", "The iron ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/iron3.rq");
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
			0, errorCount, "Metabolite can use Fe³⁺ instead of Fe3+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> copper(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "copper", "The copper ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/copper.rq");
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
			0, errorCount, "Metabolite can use Cu⁺ instead of Cu+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> copper2(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "copper2", "The copper ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/copper2.rq");
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
			0, errorCount, "Metabolite can use Cu²⁺ instead of Cu2+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> manganese(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "manganese", "The manganese ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/manganese.rq");
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
			0, errorCount, "Metabolite can use Mn²⁺ instead of Mn2+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> magnesium(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "magnesium", "The magnesium ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/magnesium.rq");
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
			0, errorCount, "Metabolite can use Mg²⁺ instead of Mg2+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> ammonia(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "ammonia", "The ammonia formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/ammonia.rq");
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
			0, errorCount, "Metabolite can use NH₃ instead of NH3: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> ammonium(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "ammonium", "The ammonium formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/ammonium.rq");
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
			0, errorCount, "Metabolite can use NH₄⁺ instead of NH4+: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> chloride(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "chloride", "The chloride ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/chloride.rq");
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
			0, errorCount, "Metabolite can use Cl⁻ instead of Cl-: " + errorCount, errors, format
		));
		return assertions;
	}

	public static List<IAssertion> bicarbonate(SPARQLHelper helper, String format) throws Exception {
		Test test = new Test("UnicodeTests", "bicarbonate", "The bicarbonate ion formula can use Unicode", true);
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/unicode/bicarbonate.rq");
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
			0, errorCount, "Metabolite can use HCO₃⁻ instead of HCO3-: " + errorCount, errors, format
		));
		return assertions;
	}

}
