package com.aton.test;

import play.libs.Codec;

public class Test {
	public static void main(String[] args) {
		 String salt = String.valueOf(Math.random()).substring(2, 10);
		 System.out.println(salt);
         String password = Codec.hexMD5("password" + salt);
         System.out.println(password);
	}
}
