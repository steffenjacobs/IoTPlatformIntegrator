package me.steffenjacobs.iotplatformintegrator.ui.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.service.manage.util.UrlUtil;

/** @author Steffen Jacobs */
public class UrlUtilTest {
	@Test
	public void testSpecificPort() {
		Pair<String, Integer> urlWithPort = UrlUtil.parseUrlWithPort("http://localhost:1234");
		Assert.assertEquals("http://localhost", urlWithPort.getLeft());
		Assert.assertEquals(Integer.valueOf(1234), urlWithPort.getRight());
	}

	@Test
	public void testDefaultPort() {
		Pair<String, Integer> urlWithPort = UrlUtil.parseUrlWithPort("http://localhost");
		Assert.assertEquals("http://localhost", urlWithPort.getLeft());
		Assert.assertEquals(Integer.valueOf(80), urlWithPort.getRight());
	}
}
