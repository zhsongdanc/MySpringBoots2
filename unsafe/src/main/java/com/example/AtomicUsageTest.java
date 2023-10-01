package com.example;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/21 12:10
 */
public class AtomicUsageTest {

    public static void main(String[] args) {
//        objectTest();
//        integerArrayTest();
        objectArrayTest();
    }

    static void objectTest() {
        Person p = new Person();
        p.setAge(20);
        p.setName("szh");
        Person p2 = new Person();
        p2.setAge(25);
        p2.setName("demus");

        AtomicReference<Person> personAtomicReference = new AtomicReference<>();
        personAtomicReference.set(p);
        System.out.println(personAtomicReference.get());

        personAtomicReference.compareAndSet(p, p2);
        System.out.println(personAtomicReference.get());
    }


    static void integerArrayTest() {
        int[] array1 = {2,4,1};
        int[] array2 = {6,7,8};
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(array1);

        atomicIntegerArray.compareAndSet(1,4,44);
        System.out.println(atomicIntegerArray.get(1));
    }

    static void objectArrayTest() {
        Person p = new Person();
        p.setAge(20);
        p.setName("szh");
        Person p2 = new Person();
        p2.setAge(25);
        p2.setName("demus");
        Person[] people = new Person[2];
        people[0] = p;
        people[1] = p2;

        AtomicReferenceArray<Person> atomicReferenceArray = new AtomicReferenceArray<Person>(people);
        atomicReferenceArray.compareAndSet(0, p, p2);
        System.out.println(atomicReferenceArray.get(0));
    }

    static void stampedReferenceTest() {
//        AtomicStampedReference<Person> p = new AtomicStampedReference<Person>();
    }

}
