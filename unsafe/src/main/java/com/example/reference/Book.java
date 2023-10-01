package com.example.reference;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/21 14:43
 */

import com.example.Person;
import java.lang.ref.WeakReference;

/**
 * name属性是弱饮用，该怎么写？
 */
public class Book {

    public Book(WeakReference<String> name, double price) {
        this.name = name;
        this.price = price;
    }

    private WeakReference<String> name;
    private double price;


    public static void main(String[] args) {
        testWithGc();

    }

    static void testWithoutGc() {
        // Person{age=0, name='null'}
        // Person{age=0, name='null'}
        Person p = new Person();
        WeakReference<Person> weakReference = new WeakReference<>(p);
        System.out.println(weakReference.get());

        p = null;
        System.out.println(weakReference.get());
    }

    static void testWithGc() {
//        Person{age=0, name='null'}
//        null
        Person p = new Person();
        WeakReference<Person> weakReference = new WeakReference<>(p);
        System.out.println(weakReference.get());

        p = null;
        System.gc();
        System.out.println(weakReference.get());
    }
}

class Book2 extends WeakReference<String>{

    private double price;
    private String name;

    public Book2(String name, double price) {
        super(name);
        this.price = price;
    }
}
