/* Copyright (C) 2020-2021  Egon Willighagen <egon.willighagen@gmail.com>
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

public class AssertNotSame implements IAssertion {

	private Object expectedValue;
	private Object value;
	private String message;
	private String details;
	private String testClass;
	private String test;

	public AssertNotSame(String testClass, String test, Object expectedValue, Object value, String message) {
		this.testClass = testClass;
		this.test = test;
		this.expectedValue = expectedValue;
		this.value = value;
		this.message = message;
		this.details = "";
	}

	public AssertNotSame(String testClass, String test, Object expectedValue, Object value, String message, String details) {
		this(testClass, test, expectedValue, value, message);
		this.details = details;
	}

	public Object getExpectedValue() {
		return this.expectedValue;
	}

	public Object getValue() {
		return this.value;
	}

	public String getMessage() {
		return this.message;
	}

	public String getTestClass() {
		return testClass;
	}

	public String getTest() {
		return test;
	}

	public String getDetails() {
		return this.details;
	}

}
