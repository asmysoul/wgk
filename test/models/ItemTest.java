package models;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.MixHelper;

import enums.Platform;
import enums.TaskType;

public class ItemTest extends UnitTest {

    @Test
    public void test() {
        // 淘宝
        _test(Platform.TAOBAO, "http://item.taobao.com/item.html?id=37173999168");

        // TMALL
        _test(
            Platform.TMALL,
            "http://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.6.0Bmir3&id=6920540323&areaId=330100&cat_id=53306006&rn=f51f70edbfa5833c37bd17d5ac3082ed&user_id=324478653&is_b=1");
    }

    private void _test(Platform p, String url) {
        Item item = Item.findByUrl(url, p,TaskType.JHS);
        assertNotNull(item);

        MixHelper.print(item.title);
        assertTrue(MixHelper.isNotEmpty(item.title));

        MixHelper.print(item.price);
        assertTrue(MixHelper.isNotEmpty(item.price));

        MixHelper.print(item.imgUrl);
        assertTrue(MixHelper.isNotEmpty(item.imgUrl));
    }

}
