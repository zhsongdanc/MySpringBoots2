package org.example.test;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/27 11:06
 */
public class MatchTest {

    public static void main(String[] args) {
        String pattern = "在岗人力[\\n-]\\d{4}-\\d{2}";
        String pattern2 = "在岗人力[\\n-]\\d{4}-\\d{2}";
        String regex = "[A|B]-(\\d{4})-(\\d{2})";
        System.out.println("A-2023-09".matches(regex));
        System.out.println("B-2023-09".matches(regex));
    }
}
