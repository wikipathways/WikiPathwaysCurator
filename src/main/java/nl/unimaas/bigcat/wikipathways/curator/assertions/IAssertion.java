package nl.unimaas.bigcat.wikipathways.curator.assertions;

public interface IAssertion {

	public String getTestClass();
	public String getTest();
	public String getMessage();

	public Object getValue();

}
