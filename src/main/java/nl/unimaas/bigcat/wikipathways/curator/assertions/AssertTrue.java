package nl.unimaas.bigcat.wikipathways.curator.assertions;

public class AssertTrue implements IAssertion {

	private boolean isTrue;
	private String message;
	private String details;
	private String testClass;
	private String test;

	public AssertTrue(String testClass, String test, boolean isTrue, String message) {
		this.isTrue = isTrue;
		this.testClass = testClass;
		this.test = test;
		this.message = message;
		this.details = "";
	}

	public AssertTrue(String testClass, String test, boolean isTrue, String message, String details) {
		this(testClass, test, isTrue, message);
		this.details = details;
	}

	public Object getValue() {
		return isTrue;
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
