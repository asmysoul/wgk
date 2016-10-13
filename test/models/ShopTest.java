package models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.aton.util.MixHelper;

import enums.Platform;


/**
 * 
 * TODO Comment.
 * 
 * @author youblade
 * @since  v0.2.3
 * @created 2014年10月30日 下午3:08:15
 */
public class ShopTest {

    /**
     * Test method for {@link models.Shop#validateUrl()}.
     */
    @Test
    public void testValidateUrl() {
        
        for (Platform p : Platform.values()) {
            // 无参数，非“/”结尾
            String url = MixHelper.format("http://123.{}.com", p.toString().toLowerCase());
            Shop s = Shop.newInstance(p, url);
            assertTrue(s.validateUrl());
            assertTrue(s.url.startsWith("http://"));
            
            // 无参数，“/”结尾
            url = MixHelper.format("http://123.{}.com/", p.toString().toLowerCase());
            s = Shop.newInstance(p, url);
            assertTrue(s.validateUrl());
            assertTrue(s.url.startsWith("http://"));
            
            // 无参数，非“/”结尾，协议大写
            url = MixHelper.format("HTTP://123.{}.com", p.toString().toLowerCase());
            s = Shop.newInstance(p, url);
            assertTrue(s.validateUrl());
            assertTrue(s.url.toLowerCase().startsWith("http://"));
            
            // 无参数，“/”结尾，协议大写
            url = MixHelper.format("HTTP://123.{}.com/", p.toString().toLowerCase());
            s = Shop.newInstance(p, url);
            assertTrue(s.validateUrl());
            assertTrue(s.url.toLowerCase().startsWith("http://"));
            
            if (p == Platform.TAOBAO) {
                url = "http://babybeilei.taobao.com/shop/view_shop.htm?spm=a1z0e.1.0.0.IS4mge&mytmenu=mdianpu&utkn=g,mnuw4zdfojswy3dbgaytaoi1412909017106&user_number_id=49566801&scm=1028.1.1.20001";
                s = Shop.newInstance(p, url);
                assertTrue(s.validateUrl());
                assertTrue(s.url.toLowerCase().startsWith("http://"));
                
                // 错误的url
                url = "http://babybeilei.taobao2.com/shop/view_shop.htm?spm=a1z0e.1.0.0.IS4mge&mytmenu=mdianpu&utkn=g,mnuw4zdfojswy3dbgaytaoi1412909017106&user_number_id=49566801&scm=1028.1.1.20001";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "http://babybeilei.taobao2.com/";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "http://babybeilei.taobao2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "HTTP://babybeilei.taobao2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                // 特殊值
                url = "http:///babybeilei.taobao2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "http://///babybeilei.taobao2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                
                url = "http://tumei.tmall.com/index.htm?spm=a220o.1000855.w5002-3584492010.2.WI4zbK";
                assertFalse(Shop.newInstance(p, url).validateUrl());
            }
            
            if (p == Platform.TMALL) {
                url = "http://tumei.tmall.com/index.htm?spm=a220o.1000855.w5002-3584492010.2.WI4zbK";
                s = Shop.newInstance(p, url);
                assertTrue(s.validateUrl());
                assertTrue(s.url.toLowerCase().startsWith("http://"));
                
                // 错误的url
                url = "http://babybeilei.taobao.com/shop/view_shop.htm?spm=a1z0e.1.0.0.IS4mge&mytmenu=mdianpu&utkn=g,mnuw4zdfojswy3dbgaytaoi1412909017106&user_number_id=49566801&scm=1028.1.1.20001";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                
                url = "http://babybeilei.tmall2.com/shop/view_shop.htm?spm=a1z0e.1.0.0.IS4mge&mytmenu=mdianpu&utkn=g,mnuw4zdfojswy3dbgaytaoi1412909017106&user_number_id=49566801&scm=1028.1.1.20001";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "http://babybeilei.ssdfds.com/";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "http://babybeilei.tmall2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "HTTP://babybeilei.taobao2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                // 特殊值
                url = "http:///babybeilei.taobao2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
                url = "http://///babybeilei.taobao2.com";
                assertFalse(Shop.newInstance(p, url).validateUrl());
            }
        }
    }

}
