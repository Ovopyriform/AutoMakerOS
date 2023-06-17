
import static org.junit.Assert.assertEquals;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.OPENAUTOMAKER;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xyz.openautomaker.environment.OpenAutoMakerEnv;
import xyz.openautomaker.root.RootUUID;

/**
 *
 * @author Ian
 */
public class RootUUIDTest
{
	private static String AUTOMAKER_ROOT_UUID = OPENAUTOMAKER + ".root.uuid";

	@Before
	public void setup()
	{
	}

	@After
	public void tearDown()
	{
	}

	@Test
	public void testRootUUID()
	{
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();

		// Checks that a UUID is generated and stored in the properties
		String actual = RootUUID.get();
		String expected = env.getProperty(AUTOMAKER_ROOT_UUID);

		assertEquals(actual, expected);

		env.removeProperty(AUTOMAKER_ROOT_UUID);
	}
}
