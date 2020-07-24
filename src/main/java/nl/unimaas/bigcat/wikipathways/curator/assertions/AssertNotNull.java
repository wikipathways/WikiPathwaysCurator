package nl.unimaas.bigcat.wikipathways.curator.assertions;

public class AssertNotNull implements IAssertion {

	private Object value;

	public AssertNotNull(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}

}
