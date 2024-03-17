package org.openautomaker;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ianhudson
 */
public class ScratchpadCodeToRun {

	public ScratchpadCodeToRun() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void runSomeCode() throws Exception {

		String osName = System.getProperty("os.name");

		System.out.println("OSName: " + osName);
		System.out.println("Does it match ^Mac.*?: " + osName.matches("^Mac.*"));

	}

}
