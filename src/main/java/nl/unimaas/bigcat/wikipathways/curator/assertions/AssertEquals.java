package nl.unimaas.bigcat.wikipathways.curator.assertions;

public class AssertEquals implements IAssertion {

	private Object expectedValue;
	private Object value;
	private String message;

	public AssertEquals(Object expectedValue, Object value, String message) {
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

}
