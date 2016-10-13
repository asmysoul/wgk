package com.aton.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.UnitTest;


public class SmsUtilTest extends UnitTest{

    @Test
    public void testSend() {
        
        SmsUtil111.send("18922326039", "测试中文003x【热力】");
    }

}
