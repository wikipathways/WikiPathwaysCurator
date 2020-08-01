package nl.unimaas.bigcat.wikipathways.curator.assertions;

public class AssertNotNull implements IAssertion {

	private Object value;
	private String testClass;
	private String test;

	public AssertNotNull(String testClass, String test, Object value) {
		this.value = value;
		this.testClass = testClass;
		this.test = test;
	}

	public Object getValue() {
		return this.value;
	}

	public String getMessage() {
		return "Value was unexpectedly null";
	}

	public String getTestClass() {
		return testClass;
	}

	public String getTest() {
		return test;
	}

}
