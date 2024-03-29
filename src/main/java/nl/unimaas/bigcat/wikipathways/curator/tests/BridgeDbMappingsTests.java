/* Copyright (C) 2020  Egon Willighagen <egon.willighagen@gmail.com>
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
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotSame;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class BridgeDbMappingsTests {

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(hasSomeEntrezGeneMappings(helper));
		assertions.addAll(hasSomeHMDBMappings(helper));
		assertions.addAll(hasSomeChemSpiderMappings(helper));
		assertions.addAll(hasSomeEnsemblMappings(helper));
		assertions.addAll(hasSomeUniprotMappings(helper));
		return assertions;
	}

	public static List<IAssertion> hasSomeEntrezGeneMappings(SPARQLHelper helper) throws Exception {
		Test test = new Test("BridgeDbMappingsTests", "hasSomeEntrezGeneMappings");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/hasEntrezGeneMappings.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertNotSame(test, 
			0, table.getRowCount(), "Expected some mapped Entrez Genes identifiers, but found zero"
		));
		return assertions;
	}

	public static List<IAssertion> hasSomeHMDBMappings(SPARQLHelper helper) throws Exception {
		Test test = new Test("BridgeDbMappingsTests", "hasSomeHMDBMappings");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/hasHMDBMappings.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertNotSame(test, 
			0, table.getRowCount(), "Expected some mapped HMDB identifiers, but found zero"
		));
		return assertions;
	}

	public static List<IAssertion> hasSomeChemSpiderMappings(SPARQLHelper helper) throws Exception {
		Test test = new Test("BridgeDbMappingsTests", "hasSomeChemSpiderMappings");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/hasChemSpiderMappings.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertNotSame(test, 
			0, table.getRowCount(), "Expected some mapped ChemSpider identifiers, but found zero"
		));
		return assertions;
	}

	public static List<IAssertion> hasSomeEnsemblMappings(SPARQLHelper helper) throws Exception {
		Test test = new Test("BridgeDbMappingsTests", "hasSomeEnsemblMappings");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/hasEnsemblMappings.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertNotSame(test, 
			0, table.getRowCount(), "Expected some mapped Ensembl identifiers, but found zero"
		));
		return assertions;
	}

	public static List<IAssertion> hasSomeUniprotMappings(SPARQLHelper helper) throws Exception {
		Test test = new Test("BridgeDbMappingsTests", "hasSomeUniprotMappings");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("general/hasUniprotMappings.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		assertions.add(new AssertNotSame(test, 
			0, table.getRowCount(), "Expected some mapped UniProt identifiers, but found zero"
		));
		return assertions;
	}

}
