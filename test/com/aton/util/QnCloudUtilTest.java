package com.aton.util;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.QnCloudUtil.QnFileBucket;


/**
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年8月22日 下午2:05:56
 */
public class QnCloudUtilTest extends UnitTest{

    /**
     * Test method for {@link com.aton.util.QnCloudUtil#getUploadToken()}.
     */
    @Test
    public void testGetUploadToken() {
        
        String token = QnCloudUtil.generateUploadToken();
        assertTrue(MixHelper.isNotEmpty(token));
        
        for (QnFileBucket bucket : QnFileBucket.values()) {
            token = QnCloudUtil.generateUploadToken(bucket);
            assertTrue(MixHelper.isNotEmpty(token));
        }
    }

}
