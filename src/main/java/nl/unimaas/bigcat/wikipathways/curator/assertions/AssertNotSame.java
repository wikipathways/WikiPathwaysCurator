package nl.unimaas.bigcat.wikipathways.curator.assertions;

public class AssertNotSame implements IAssertion {

	private Object expectedValue;
	private Object value;
	private String message;
	private String testClass;
	private String test;

	public AssertNotSame(String testClass, String test, Object expectedValue, Object value, String message) {
		this.testClass = testClass;
		this.test = test;
		this.expectedValue = expectedValue;
		this.value = value;
		this.message = message;
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

}
