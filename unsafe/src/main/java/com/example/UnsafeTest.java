package com.example;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/21 10:50
 */

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import sun.misc.Unsafe;

/**
 * 主要提供一些用于执行低级别、不安全操作的方法，如直接访问系统内存资源、自主管理内存资源等
 */
public class UnsafeTest {

    private static long valueOffset;

    // todo 这里必须是int，不能是Integer，因为Integer是对象
    // todo 而且这里必须用 volatile，不然不能保证原子性
    private int value;

    private static Unsafe unsafe;


    static {

        Field field = null;
        try {
            field = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        try {
            unsafe =  (Unsafe) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            valueOffset = Objects.requireNonNull(unsafe).objectFieldOffset(UnsafeTest.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static void main(String[] args) {
        UnsafeTest unsafeTest = new UnsafeTest();
        unsafeTest.setValue(10);
        System.out.println(unsafeTest.getValue());
        boolean b = unsafe.compareAndSwapInt(unsafeTest, valueOffset, 10, 11);
        System.out.println(b);
        System.out.println("after unsafe,value=" + unsafeTest.getValue());
    }


}





