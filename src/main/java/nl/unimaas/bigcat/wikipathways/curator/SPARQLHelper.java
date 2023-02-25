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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;

public class SPARQLHelper {

	private Model model = null;
	private String endpoint = null;
	
	public SPARQLHelper(Model model) {
		this.model = model;
	}

	public SPARQLHelper(String endpoint) {
		this.endpoint = endpoint;
	}

	public StringMatrix sparql(String queryString) throws Exception {
		if (model != null) return SPARQLHelper.sparql(model, queryString);
		if (endpoint != null) return SPARQLHelper.sparql(endpoint, queryString);
		return null;
	}

	public static StringMatrix sparql(Model model, String queryString)
			throws Exception {
		StringMatrix table = null;

		// now the Jena part
		Query query = QueryFactory.create(queryString);
        PrefixMapping prefixMap = query.getPrefixMapping();
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();
		table = convertIntoTable(prefixMap, results);

		return table;
	}

	public long size() {
		if (endpoint != null) return 1000000; // TODO: return the actual triple count?
		return this.model.size();
	}
	
	public static StringMatrix sparql(String endpoint, String queryString)
			throws Exception {
		StringMatrix table = null;

		// use Apache for doing the SPARQL query
		HttpClientBuilder builder = HttpClientBuilder.create();
		String proxyString = System.getenv("http_proxy");
		if (proxyString != null) {
			URL proxyURL = new URL(proxyString);
			HttpHost proxy = new HttpHost(proxyURL.getHost(), proxyURL.getPort());
			builder.setProxy(proxy);
		}
		try (CloseableHttpClient httpclient = builder.build()) {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("query", queryString));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			HttpPost httppost = new HttpPost(endpoint);
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			StatusLine status = response.getStatusLine(); 
			HttpEntity responseEntity = response.getEntity();
			if (status.getStatusCode() != 200) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				responseEntity.writeTo(buffer);
				throw new Exception("Invalid SPARQL result: " + status.getReasonPhrase() + ": " + buffer.toString());
			}
			InputStream in = responseEntity.getContent();

			// now the Jena part
			ResultSet results = ResultSetFactory.fromXML(in);
			table = convertIntoTable(null, results);

			in.close();
		} catch (IOException exception) {
			throw exception;
		}
		return table;
	}

	private static StringMatrix convertIntoTable(
			PrefixMapping prefixMap, ResultSet results) {
		StringMatrix table = new StringMatrix();
		int rowCount = 0;
		while (results.hasNext()) {
			rowCount++;
			QuerySolution soln = results.nextSolution();
			Iterator<String> varNames = soln.varNames();
			while (varNames.hasNext()) {
				String varName = varNames.next();
				int colCount = -1;
				if (table.hasColumn(varName)) {
					colCount = table.getColumnNumber(varName);
				} else {
					colCount = table.getColumnCount() + 1;
					table.setColumnName(colCount, varName);
				}
				RDFNode node = soln.get(varName);
				if (node != null) {
					if (node.isResource()) {
						Resource resource = (Resource)node;
						table.set(rowCount, colCount,
							resource.getURI()
						);
					} else if (node.isLiteral()) {
						Literal literal = (Literal)node;
						table.set(rowCount, colCount, "" + literal.getValue());
					}
				}
			}
		}
		return table;
	}

	public static String[] split(PrefixMapping prefixMap, Resource resource) {
		String uri = resource.getURI();
		if (uri == null) {
			return new String[] {null, null};
		}
		if (prefixMap == null) {
			return new String[] {uri,null};
		}
		Map<String,String> prefixMapMap = prefixMap.getNsPrefixMap();
		Set<String> prefixes = prefixMapMap.keySet();
		String[] split = { null, null };
		for (String key : prefixes){
			String ns = prefixMapMap.get(key);
			if (uri.startsWith(ns)) {
				split[0] = key;
				split[1] = uri.substring(ns.length());
				return split;
			}
		}
		split[1] = uri;
		return split;
	}

	/**
	 * Updates the matrix context column values to point to the classic WikiPathways website.
	 *
	 * @param table      SPARQL results
	 * @param columnName Column to replace the webpage URL for
	 */
	public static StringMatrix classicify(StringMatrix table, String columnName) {
		if (table.getRowCount() == 0) {
		} else {
			for (int i=1; i<=table.getRowCount(); i++) {
				String updatedURL = table.get(i, columnName)
					.replaceAll("/www.wikipathways.org/", "/classic.wikipathways.org/")
					.replaceAll("/wikipathways.org/", "/classic.wikipathways.org/");
				table.set(i, columnName, updatedURL);
			}
		}
		return table;
	}
}
