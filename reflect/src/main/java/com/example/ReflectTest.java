package com.example;

import java.lang.reflect.Field;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/21 10:56
 */
public class ReflectTest {

    public static void main(String[] args) throws Exception{
        // declared 用于获取当前类的所有字段
        // 普通的 获取当前类和父类的所有public方法
        String str = "abcd";
        Field field =String.class.getDeclaredField("value");
        field.setAccessible(true);
        char[] value = (char[]) field.get(str);
        System.out.println(new String(value));


    }
}
