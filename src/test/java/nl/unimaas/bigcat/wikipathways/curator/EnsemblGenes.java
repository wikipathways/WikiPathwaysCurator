/* Copyright (C) 2015,2018  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EnsemblGenes {

	@SuppressWarnings({ "serial" })
	private static final Map<String,String> deprecated = new HashMap<String,String>() {{
        put("ENSG00000011177", "ENSG00000011177.5");
        put("ENSG00000079782", "ENSG00000079782.8");
        put("ENSG00000096087", "ENSG00000096087.5");
        put("ENSG00000105663.1", "ENSG00000272333");
        put("ENSG00000105663", "ENSG00000272333");
        put("ENSG00000132142.1", "ENSG00000278540"); 
        put("ENSG00000132142", "ENSG00000278540");
        put("ENSG00000172070.5", "ENSG00000271303");
        put("ENSG00000172070", "ENSG00000271303");
        put("ENSG00000175818", "ENSG00000175818.6");
        put("ENSG00000184674", "ENSG00000184674.8");
        put("ENSG00000187919", "ENSG00000187919.5");
        put("ENSG00000197592", "ENSG00000197592.3");
        put("ENSG00000198339.3", "ENSG00000276180");
        put("ENSG00000198339", "ENSG00000276180");
        put("ENSG00000198981.2", "ENSG00000284032");
        put("ENSG00000198981", "ENSG00000284032");
        put("ENSG00000199004", "ENSG00000284190"); // old MIR21 identifier
        put("ENSG00000199066.1", "ENSG00000284544");
        put("ENSG00000199066", "ENSG00000284544");
        put("ENSG00000199097", "ENSG00000284231");
        put("ENSG00000207603.1", "ENSG00000284179");
        put("ENSG00000207603", "ENSG00000284179");
        put("ENSG00000207694", "ENSG00000284453");
        put("ENSG00000207700", "ENSG00000276365");
        put("ENSG00000207718", "ENSG00000283785");
        put("ENSG00000207745", "ENSG00000284536");
        put("ENSG00000207748", "ENSG00000283797");
        put("ENSG00000207764", "ENSG00000284508");
        put("ENSG00000207795", "ENSG00000283904");
        put("ENSG00000207865", "ENSG00000284357");
        put("ENSG00000207875", "ENSG00000284520");
        put("ENSG00000207929", "ENSG00000284112");
        put("ENSG00000207936", "ENSG00000283733");
        put("ENSG00000207939", "ENSG00000284567");
        put("ENSG00000207949", "ENSG00000283844");
        put("ENSG00000207966", "ENSG00000207966.1");
        put("ENSG00000207968", "ENSG00000207968.2");
        put("ENSG00000208034", "ENSG00000284538");
        put("ENSG00000208035", "ENSG00000284182");
        put("ENSG00000209707", "ENSG00000209707.1");
        put("ENSG00000211589", "ENSG00000211589.1");
        put("ENSG00000213052", "ENSG00000213052.1");
        put("ENSG00000214699", "ENSG00000214699.1");
        put("ENSG00000221035", "ENSG00000221035.1");
        put("ENSG00000255604", "ENSG00000109072");
        put("ENSG00000262852", "ENSG00000170276");
        put("ENSG00000266658", "ENSG00000266658.2");
        put("ENSG00000273895", "ENSG00000284027");
        put("ENSG00000274160", "ENSG00000274160.1");
        put("ENSG00000274250", "ENSG00000283797");
        put("ENSG00000274784", "ENSG00000284231");
        put("ENSG00000275042", "ENSG00000284536");
        put("ENSG00000275534", "ENSG00000283762");
        put("ENSG00000275668", "ENSG00000284214");
        put("ENSG00000275739", "ENSG00000284112");
        put("ENSG00000275802", "ENSG00000284375");
        put("ENSG00000276018", "ENSG00000283705");
        put("ENSG00000276752", "ENSG00000284203");
        put("ENSG00000276792", "ENSG00000283927");
        put("ENSG00000277328", "ENSG00000283844");
        put("ENSG00000277934", "ENSG00000284033");
        put("ENSG00000282827", "ENSG00000164053 ");
        put("ENSG00000283147", "ENSG00000283147.1");
        put("", "");
	}};

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
			String parseErrors = OPSWPRDFFiles.getParseErrors();
			Assertions.assertNotNull(parseErrors);
			Assertions.assertEquals(0, parseErrors.length(), parseErrors.toString());
		}
	}

	@BeforeEach
	public static void waitForIt() throws InterruptedException { TimeUnit.MICROSECONDS.wait(500); }

	@Tag("outdated")
	@Test
	public void outdatedIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/allEnsemblIdentifiers.rq");
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			    ? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		        : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertNotSame(0, table.getColumnCount());
			String errors = "";
			int errorCount = 0;
			if (table.getRowCount() > 0) {
				for (int i=1; i<=table.getRowCount(); i++) {
					String identifier = table.get(i, "identifier");
					if (deprecated.containsKey(identifier)) {
						errors += table.get(i, "homepage") + " " + table.get(i, "label") + " " + table.get(i, "identifier") +
							  " is deprecated and possibly replaced by " + deprecated.get(identifier) + "; \n";
						errorCount++;
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Deprecated Ensembl identifiers:\n" + errors
			);
		});
	}

	@Test
	public void wrongEnsemblIDForHumanSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Human.rq");
		Assertions.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for human for: " + System.getProperty("SUBSETPREFIX"));
		Assertions.assertTimeout(Duration.ofSeconds(50), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
			    ? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
		        : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			String errors = "";
			int errorCount = 0;
			if (table.getRowCount() > 0) {
				for (int i=1; i<=table.getRowCount(); i++) {
					String identifier = table.get(i, "identifier");
					if (!identifier.contains("LRG_482")) { // in pathway WP3658
						identifier = identifier.trim();
						if (!identifier.isEmpty()) {
							try {
								Integer.parseInt(identifier);
							} catch (NumberFormatException exception) {
								errors += table.get(i, "homepage") + " -> " + table.get(i, "label") +
										", " + table.get(i, "identifier") + "\n ";
								errorCount++;
							}
						}
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Ensembl identifiers for wrong species for a human pathway:\n" + errors
			);
		});
	}

	@Test
	public void wrongEnsemblIDForRatSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Rat.rq");
		Assertions.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for rat for: " + System.getProperty("SUBSETPREFIX"));
		Assertions.assertTimeout(Duration.ofSeconds(50), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(
				0, table.getRowCount(), "Ensembl identifiers for wrong species for a rat pathway:\n" + table
			);
		});
	}

	@Test
	public void wrongEnsemblIDForMouseSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Mouse.rq");
		Assertions.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for mouse for: " + System.getProperty("SUBSETPREFIX"));
		Assertions.assertTimeout(Duration.ofSeconds(50), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			String errors = "";
			int errorCount = 0;
			if (table.getRowCount() > 0) {
				for (int i=1; i<=table.getRowCount(); i++) {
					String identifier = table.get(i, "identifier");
					String pathwayPage = table.get(i, "homepage");
					if (!pathwayPage.contains("WP2080") && // this pathway has a few of human mirs
							!pathwayPage.contains("WP2087") && // this pathway has a few of human mirs
							!pathwayPage.contains("WP3632")) { // this pathway has a few human genes (pinged Freddie)
						identifier = identifier.trim();
						if (!identifier.isEmpty()) {
							try {
								Integer.parseInt(identifier);
							} catch (NumberFormatException exception) {
								errors += table.get(i, "homepage") + " -> " + table.get(i, "label") +
										", " + table.get(i, "identifier") + "\n ";
								errorCount++;
							}
						}
					}
				}
			}
			Assertions.assertEquals(
				0, errorCount, "Ensembl identifiers for wrong species for a mouse pathway:\n" + errors
			);
		});
	}

	@Test
	public void wrongEnsemblIDForCowSpecies() throws Exception {
		String sparql = ResourceHelper.resourceAsString("genes/ensemblGenesWrongSpecies_Cow.rq");
		Assertions.assertNotNull(sparql);
		System.out.println("Wrong Ensembl gene for cow for: " + System.getProperty("SUBSETPREFIX"));
		Assertions.assertTimeout(Duration.ofSeconds(50), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertEquals(
				0, table.getRowCount(), "Ensembl identifiers for wrong species for a cow pathway:\n" + table
			);
		});
	}

}
