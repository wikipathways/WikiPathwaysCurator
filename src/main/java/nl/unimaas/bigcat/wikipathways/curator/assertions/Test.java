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
package nl.unimaas.bigcat.wikipathways.curator.assertions;

import java.net.MalformedURLException;
import java.net.URL;

public class Test {

	public final static boolean HAS_LINK_TO_DOCS = true;

	private String testClass;
	private String test;
	private String title;
	private URL defaultLinkToDocs = null;

	public Test(String testClass, String test, String title) {
		this.testClass = testClass;
		this.test = test;
		this.title = title == null ? testClass + "." + test : title;
	}

	public Test(String testClass, String test) {
		this(testClass, test, null);
	}

	public String getClassName() {
		return this.testClass;
	}

	public String getTestName() {
		return this.test;
	}

	public String getTitle() {
		return this.title;
	}

	public URL getDefaultLinkToDocs() {
		try {
			if (defaultLinkToDocs == null) defaultLinkToDocs = new URL("https://wikipathways.github.io/WikiPathwaysCurator/" + this.testClass + "/" + this.test);
			return defaultLinkToDocs;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error while creating the default URL: " + e.getMessage());
		} 
	}
}
