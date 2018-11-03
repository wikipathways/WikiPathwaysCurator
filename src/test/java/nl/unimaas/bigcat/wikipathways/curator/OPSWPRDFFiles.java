/* Copyright (C) 2013,2018  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.n3.turtle.TurtleParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OPSWPRDFFiles {

	private static Model loadedData = null;
	private static boolean locked = false;
	private static String parseErrors = "";
	
	public static Model loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			return null;
		}

		if (loadedData != null) return loadedData;

		while (locked) Thread.sleep(1000);

		if (loadedData != null) return loadedData;

		locked = true;

		String folder = "/tmp/doesntexist/";
		if (System.getProperty("OPSWPRDF") != null) {
			folder = System.getProperty("OPSWPRDF");
			folder = folder.replace(".", "/");
			folder = folder.replace("_", " ");
			folder = folder.replace("[", "(");
			folder = folder.replace("]", ")");
		}
		System.out.println("OPSWPRDF folder: " + folder);

		String subsetPrefix = "wp111";
		if (System.getProperty("SUBSETPREFIX") != null) {
			subsetPrefix = System.getProperty("SUBSETPREFIX");
		}
		System.out.println("WP subset: " + subsetPrefix);

		List<File> files = findAllFiles(folder, subsetPrefix);
		System.out.println("Testing these files: " + files);
		StringBuffer parseFailReport = new StringBuffer();
		String directory = "target/UnitTest" ;
		File tbdFolder = new File(directory);
		tbdFolder.mkdir();
		loadedData = ModelFactory.createDefaultModel();
		for (File file : files) {
			try {
				loadedData.read(new FileInputStream(file), "", "TURTLE");
			} catch (FileNotFoundException exception) {
				parseFailReport.append(file.getName())
			    .append(": not found\n");
			} catch (TurtleParseException exception) {
				parseFailReport.append(file.getName())
				    .append(": ").append(exception.getMessage())
				    .append('\n');
			} catch (RiotException exception) {
				parseFailReport.append(file.getName())
			        .append(": ").append(exception.getMessage())
			        .append('\n');
			}
		}
		locked = false;
		parseErrors = parseFailReport.toString();
		return loadedData;
	}

	private static List<File> findAllFiles(String folder, String subsetPrefix) {
		List<File> files = new ArrayList<File>();

		File root = new File(folder);
		File[] list = root.listFiles();
        if (list == null) return Collections.emptyList();

        for ( File file : list ) {
            if ( file.isDirectory() ) {
            	files.addAll(findAllFiles(file.getAbsolutePath(), subsetPrefix));
            } else {
            	String name = file.getName();
            	if (name.toLowerCase().endsWith(".ttl") && name.toLowerCase().startsWith(subsetPrefix)) {
            		if (!testOrTutorial(name)) files.add(file);
            	}
            }
        }
		return files;
	}

	@SuppressWarnings("serial")
	private static final List<String> pathwaysToIgnore = new ArrayList<String>() {{
		add("WP4"); // the edit playground
		add("WP2582"); // the metabolite tests
	}};

	private static boolean testOrTutorial(String filename) {
		for (String pathway : pathwaysToIgnore) {
			if (filename.contains(pathway + "_")) return true;
		}
		return false;
	}

	@Test
	public void testLoadingRDF() throws InterruptedException {
		loadData();
		if (parseErrors.length() > 0) Assertions.fail(parseErrors.toString());
	}

}
