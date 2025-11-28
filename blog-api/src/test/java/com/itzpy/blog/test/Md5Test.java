package com.itzpy.blog.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Md5Test {
    @Value("${jwt.salt}")
    private String salt;

    @Test
    public void test() {
        String str = "d75fe88a936230067727251f41216371";
        System.out.println(str.length());
    }


    @Test
    public void test1() {
       String password =  DigestUtils.md5Hex(123456 +  salt);
       System.out.println(password);
       //e10adc3949ba59abbe56e057f20f883e
    }
}
