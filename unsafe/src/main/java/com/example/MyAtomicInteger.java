package com.example;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/21 10:50
 */

import java.lang.reflect.Field;
import java.util.Objects;
import sun.misc.Unsafe;

/**
 * 主要提供一些用于执行低级别、不安全操作的方法，如直接访问系统内存资源、自主管理内存资源等
 */
public class MyAtomicInteger {

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
            valueOffset = Objects.requireNonNull(unsafe).objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
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
//      casIntegerTest();

        /**
         * //分配内存, 相当于C++的malloc函数
         * public native long allocateMemory(long bytes);
         * //扩充内存
         * public native long reallocateMemory(long address, long bytes);
         * //释放内存
         * public native void freeMemory(long address);
         * //在给定的内存块中设置值
         * public native void setMemory(Object o, long offset, long bytes, byte value);
         * //内存拷贝
         * public native void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes);
         * //获取给定地址值，忽略修饰限定符的访问限制。与此类似操作还有: getInt，getDouble，getLong，getChar等
         * public native Object getObject(Object o, long offset);
         * //为给定地址设置值，忽略修饰限定符的访问限制，与此类似操作还有: putInt,putDouble，putLong，putChar等
         * public native void putObject(Object o, long offset, Object x);
         * //获取给定地址的byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果为确定的）
         * public native byte getByte(long address);
         * //为给定地址设置byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果才是确定的）
         * public native void putByte(long address, byte x);
         */
        unsafe.allocateMemory(100);
        unsafe.reallocateMemory(0x22, 100);

//        对垃圾回收停顿的改善。由于堆外内存是直接受操作系统管理而不是JVM，所以当我们使用堆外内存时，即可保持较小的堆内内存规模。从而在GC时减少回收停顿对于应用的影响。
//        提升程序I/O操作的性能。通常在I/O通信过程中，会存在堆内内存到堆外内存的数据拷贝操作，对于需要频繁进行内存间数据拷贝且生命周期较短的暂存数据，都建议存储到堆外内存。

    }

    static  void casIntegerTest() {
        MyAtomicInteger myAtomicInteger = new MyAtomicInteger();
        myAtomicInteger.setValue(10);
        System.out.println(myAtomicInteger.getValue());
        boolean b = unsafe.compareAndSwapInt(myAtomicInteger, valueOffset, 10, 11);
        System.out.println(b);
        System.out.println("after unsafe,value=" + myAtomicInteger.getValue());
    }


}





