/* Copyright (C) 2021-2024  Egon Willighagen <egon.willighagen@gmail.com>
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
	private boolean hasDocs;
	private String customLinkToDocs;

	public Test(String testClass, String test, String title, String customLinkToDocs) {
		this.testClass = testClass;
		this.test = test;
		this.title = title == null ? testClass + "." + test : title;
		this.customLinkToDocs = customLinkToDocs;
		this.hasDocs = true;
	}

	public Test(String testClass, String test, String title, boolean hasDocs) {
		this(testClass, test, title, null);
		this.hasDocs = hasDocs;
	}

	public Test(String testClass, String test, String title) {
		this(testClass, test, title, null);
	}

	public Test(String testClass, String test, boolean hasDocs) {
		this(testClass, test, null, hasDocs);
	}

	public Test(String testClass, String test) {
		this(testClass, test, null, false);
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

	public URL getLinkToDocs() {
		if (!hasLinkToDocs()) throw new NullPointerException("This assertion does not have a link to documentation");
		try {
			URL url = this.customLinkToDocs != null
				? new URL(this.customLinkToDocs)
				: new URL("https://www.wikipathways.org/WikiPathwaysCurator/" + this.testClass + "/" + this.test);
			return url;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error while creating the default URL: " + e.getMessage());
		}
	}

	public boolean hasLinkToDocs() {
		return this.hasDocs;
	}

}
