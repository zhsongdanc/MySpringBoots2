package com.example;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/21 14:20
 */
public class UnsafeMethodTest {

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
}
