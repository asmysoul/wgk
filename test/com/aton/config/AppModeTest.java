package com.aton.config;

import java.util.List;

import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

import com.aton.util.MixHelper;

public class AppModeTest extends UnitTest {

	@Test
	public void test() {
		
		List< AppMode> list  = (List<AppMode>) Fixtures.loadYamlAsList(Config.getProperty("app.mode.yml"));
		assertTrue(MixHelper.isNotEmpty(list));
		
		AppMode appMode = list.get(0);
		assertNotNull(appMode);
		assertTrue(AppMode.Mode.ONLINE == appMode.mode);
		assertFalse(appMode.mockFrontend);
		assertFalse(appMode.disableJob);
	}
	
	@Test
	public void test_get(){
		
		AppMode.switchMode(AppMode.Mode.DEV);
		AppMode appMode = AppMode.get();
		
		assertNotNull(appMode);
		assertTrue(AppMode.Mode.DEV == appMode.mode);
		assertTrue(appMode.mockFrontend);
		assertTrue(appMode.disableJob);
	}

}
