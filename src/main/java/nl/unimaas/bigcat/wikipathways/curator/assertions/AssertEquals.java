package nl.unimaas.bigcat.wikipathways.curator.assertions;

public class AssertEquals implements IAssertion {

	private Object expectedValue;
	private Object value;
	private String message;
	private String details;
	private String testClass;
	private String test;

	public AssertEquals(String testClass, String test, Object expectedValue, Object value, String message) {
		this.testClass = testClass;
		this.test = test;
		this.expectedValue = expectedValue;
		this.value = value;
		this.message = message;
		this.details = "";
	}

	public AssertEquals(String testClass, String test, Object expectedValue, Object value, String message, String details) {
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
