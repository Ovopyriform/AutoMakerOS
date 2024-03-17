
package celtech.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openautomaker.base.camera.CameraInfo;

/**
 *
 * @author tonya
 */
public class CameraInfoStringConverterTest {
	@Test
	public void testStringConverter() {
		// creating a Stream of strings
		List<CameraInfo> l = new ArrayList<>();
		CameraInfo ci = new CameraInfo();
		ci.setCameraName("Logitech C920");
		ci.setCameraNumber(0);
		ci.setServerIP("1.1.1.1");
		ci.setUdevName("/dev/video0");
		l.add(ci);
		ci = new CameraInfo();
		ci.setCameraName("Logitech C920");
		ci.setCameraNumber(1);
		ci.setServerIP("1.1.1.1");
		ci.setUdevName("/dev/video1");
		l.add(ci);
		ci = new CameraInfo();
		ci.setCameraName("Logitech StreamCam");
		ci.setCameraNumber(3);
		ci.setServerIP("1.1.1.1");
		ci.setUdevName("/dev/video3");
		l.add(ci);

		CameraInfoStringConverter cisc = new CameraInfoStringConverter(() -> {
			return l;
		});

		String cs0 = cisc.toString(l.get(0));
		CameraInfo ci1 = cisc.fromString("[1] Logitech C920");
		assertEquals(cs0, "[0] Logitech C920");
		assertEquals(ci1, l.get(1));
	}
}
