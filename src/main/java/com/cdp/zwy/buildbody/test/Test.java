package com.cdp.zwy.buildbody.test;

import cn.hutool.crypto.digest.BCrypt;

/**
 * @author zwy
 * @version 1.0
 * @description: Test
 * @date 2026/2/17 09:58
 */
public class Test {
public static void main(String[] args) {
    System.out.println(BCrypt.hashpw("123456"));
}
}
