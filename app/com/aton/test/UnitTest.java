package com.aton.test;

import org.junit.After;
import org.junit.Before;

import play.test.BaseTest;

import com.aton.util.MixHelper;

public class UnitTest extends BaseTest {

	@Before
	public void beforeAll(){
		MixHelper.print("~");
		MixHelper.print("----------Test Method Start---------");
		MixHelper.print("~");
		MixHelper.print("~");
	}

	@After
	public void afterAll(){
		MixHelper.print("~");
		MixHelper.print("~");
		MixHelper.print("----------Test Method End---------");
		MixHelper.print("~");
	}
}
