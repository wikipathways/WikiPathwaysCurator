/* Copyright (C) 2021  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class BridgeDbTiwidReader {

	/* Reads Tiwid files in the CSV format with three columns.
	 */
	public static Map<String,String> parseCSV(String tiwidResource) {
		return parse(tiwidResource, ",");
	}

	/* Reads Tiwid files in the TSV format with three columns.
	 */
	public static Map<String,String> parseTSV(String tiwidResource) {
		return parse(tiwidResource, "\t");
	}

	/* Reads TSV files with at least enough columns.
	 */
	public static Map<String,String> parseTSV(String tiwidResource, int firstColumn, int secondColumn) {
		return parse(tiwidResource, "\t", firstColumn, secondColumn);
	}

	/* Reads CSV files with at least enough columns.
	 */
	public static Map<String,String> parseCSV(String tiwidResource, int firstColumn, int secondColumn) {
		return parse(tiwidResource, ",", firstColumn, secondColumn);
	}

	/* Reads Tiwid files in a file with with three columns separated by the given delimeter.
	 * Columns are expected to be: deprecated ID, date of deprecation, replacing ID.
	 */
	private static Map<String,String> parse(String tiwidResource, String delim) {
		return parse(tiwidResource, delim, 0, 2);
	}

	/* Reads TSV files in a file with two columns as given in the parameters. The index starts at zero.
	 */
	private static Map<String,String> parse(String resource, String delim, int firstColumn, int secondColumn) {
		Map<String,String> deprecated = new HashMap<String,String>();
		String tiwidData = "";
		try {
			tiwidData = ResourceHelper.resourceAsString(resource);
		} catch (Exception exception) {
			// resource not found. Some files are too big for the repository, and we want the tests to run without them too
			System.out.println("Could not find the resource: " + resource);
			return deprecated;
		}
		BufferedReader reader = new BufferedReader(new StringReader(tiwidData));
		int columns = Math.max(firstColumn, secondColumn);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) continue;
				String fields[] = line.split(delim);
				if (fields.length > columns) {
					deprecated.put(fields[firstColumn], fields[secondColumn]);
				} else {
					deprecated.put(fields[firstColumn], null);
				}
			}
		} catch (IOException e) {
			// blah
		}
		return deprecated;
	}

}
