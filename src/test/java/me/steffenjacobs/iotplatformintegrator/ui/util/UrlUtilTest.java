package me.steffenjacobs.iotplatformintegrator.ui.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

/** @author Steffen Jacobs */
public class UrlUtilTest {
	@Test
	public void testSpecificPort() {
		Pair<String, Integer> urlWithPort = UrlUtil.parseUrlWithPort("http://localhost:1234");
		Assert.assertEquals("http://localhost", urlWithPort.getLeft());
		Assert.assertEquals(new Integer(1234), urlWithPort.getRight());
	}

	@Test
	public void testDefaultPort() {
		Pair<String, Integer> urlWithPort = UrlUtil.parseUrlWithPort("http://localhost");
		Assert.assertEquals("http://localhost", urlWithPort.getLeft());
		Assert.assertEquals(new Integer(80), urlWithPort.getRight());
	}
}
