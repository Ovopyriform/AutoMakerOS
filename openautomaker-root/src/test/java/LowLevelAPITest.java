
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Ian
 */
public class LowLevelAPITest
{
	static
	{
		//        // Set the libertySystems config file property..
		//        // The property is set in this static initializer because the configuration is loaded before the test is run.
		//        URL applicationURL = LowLevelAPITest.class.getResource("/");
		//        String configDir = applicationURL.getPath();
		//        String configFile = configDir + "Root.configFile.xml";
		//        System.setProperty("libertySystems.configFile", configFile);
	}

	//    @ClassRule
	//    public static final ResourceTestRule resources = ResourceTestRule.builder()
	//            .setMapper(Jackson.newObjectMapper())
	//            .addResource(new LowLevelAPI())
	//            .build();

	//    private final QueryFirmwareVersion firmwareQuery = (QueryFirmwareVersion) RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.QUERY_FIRMWARE_VERSION);

	@Before
	public void setup()
	{
		//        when(dao.fetchPerson(eq("blah"))).thenReturn(person);
	}

	@After
	public void tearDown()
	{
		//        // we have to reset the mock after each test because of the
		//        // @ClassRule, or use a @Rule as mentioned below.
		//        reset(dao);
	}

	@Test
	public void testWriteData()
	{
		//        QueryFirmwareVersion fw = (QueryFirmwareVersion) RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.QUERY_FIRMWARE_VERSION);
		//        fw.setSequenceNumber(33);
		//
		//        Entity requestEntity = Entity.entity(fw, MediaType.APPLICATION_JSON_TYPE);
		//        Invocation invocation = resources.client().target("/12345678/printerControl/writeData")
		//                .request().buildPost(requestEntity);
		//        Response response = invocation.invoke();
	}
}
