package nl.unimaas.bigcat.wikipathways.curator;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotSame;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class JUnitTests {

	/**
	 * Translated abstract assertions to JUnit5 assertions.
	 *
	 * @param assertions
	 */
	public void performAssertions(List<IAssertion> assertions) {
		for (IAssertion assertion : assertions) {
			if (assertion instanceof AssertEquals) {
				AssertEquals typedAssertion = (AssertEquals)assertion;
				Assertions.assertEquals(
					typedAssertion.getExpectedValue(),
					typedAssertion.getValue(),
					typedAssertion.getMessage() + ":\n" + typedAssertion.getDetails()
				);
			} else if (assertion instanceof AssertNotSame) {
				AssertNotSame typedAssertion = (AssertNotSame)assertion;
				Assertions.assertNotSame(
					typedAssertion.getExpectedValue(),
					typedAssertion.getValue(),
					typedAssertion.getMessage() + ":\n" + typedAssertion.getDetails()
				);
			} else if (assertion instanceof AssertNotNull) {
				AssertNotNull typedAssertion = (AssertNotNull)assertion;
				Assertions.assertNotNull(typedAssertion.getValue());
			} else {
				Assertions.assertTrue(false, "Unrecognized assertion type: " + assertion.getClass().getName());
			}
		}
	}
	
}
