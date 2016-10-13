package com.aton.util;

import java.util.Comparator;

/**
 * 
 * 
 * 比较字符类
 * 
 * @author fufei
 * @since  v2.0
 * @created 2015年1月16日 下午6:49:15
 */
public class MapKeyComparatorUtil implements Comparator<String>{

	@Override
	public int compare(String str1, String str2) {
		return str1.compareTo(str2);
	}

}
