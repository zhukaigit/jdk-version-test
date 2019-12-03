package com.zk.jdk.version;

import org.junit.Test;

import java.lang.reflect.Field;

/**
 * 1、new String(new char[]{'你', '好'})
 * 这个的构造方法只会在堆中创建一个对象。
 *
 * 2、String s2 = new String("你好")
 * jdk1.6：视情况创建1个或2个对象。
 * 第一步：首先该构造方法入参即是引号括起来的字符串对象"你好"，故先在字符串池中判断是否存在"你好"字符串对象，
 *        若不存在，则在常量池中创建一个"你好"字符串对象（A对象），若存在则不再创建
 * 第二步：然后在堆中创建一个"你好"字符串对象（B对象），将B对象的引用返给s2
 *
 * jdk1.7：视情况创建1个或2个对象。
 * 第一步：首先该构造方法入参即是引号括起来的字符串对象"你好"，故先在字符串池中判断是否存在"你好"字符串的引用，
 *        若不存在，则在堆中创建一个"你好"字符串对象（A对象），将A对象的引用存入到字符串池中，若存在则不再创建；
 * 第二步：然后再在堆中创建一个"你好"字符串对象（B对象），将B对象的引用返给s2
 *
 * 3、String s3 = new String("abc");
 *   String s4 = s3.intern();
 * 作用：主要是为了提高字符串的复用，节约内存
 * jdk1.6：intern()被调用后，判断字符串池中是否存在"abc"字符串对象，不存在则创建，存在则不创建，最后返回字符串池中的字符串对象的引用给s4
 * jdk1.7：intern()被调用后，判断字符串池中是否存在"abc"字符串的引用，不存在则在堆中创建一个"abc"字符串对象，并将该对象的引用存入字符串池中，
 * 存在则不创建，最后返回字符串池的引用给s4
 */
public class StringTest {


    // ============================= String.value的地址测试 =============================
    @Test
    public void test1() {
        String s1 = "112";
        String s2 = "11" + "2"; // 编译器编译是优化为"112"
        System.out.println(s1 == s2);// true

        String temp = "2";
        String s3 = "11" + temp; // 因为temp为变量，编译时无法优化，底层实现详解test2
        System.out.println(s1 == s3);// false

        final String temp2 = "2";
        String s4 = "11" + temp2; // 因为temp为常量，编译时优化为"112"，故返回的是常量池引用
        System.out.println(s1 == s4);// true

        String s5 = new String("112");
        s5 = s5.intern();// 是的s5的指针指向常量池中的对象
        System.out.println(s1 == s5);// true

        String s6 = new String("112");
        s6.intern();// 是的s5的指针指向常量池中的对象
        System.out.println(s1 == s6);// false

    }

    /**
     * 1、String相加底层原理
     * 2、String效率底的原因及其示例
     */
    @Test
    public void test2() {
        String s1 = "a";
        String s2 = "b";
        
        // 底层：String result = new StringBuilder("12").append(s1).append("3").append("4").append(s2).toString()
        // 由此可见，编译器会优化前面字符串常量，而中间的则不会优化
        String result = "1" + "2" + s1 + "3" + "4" + s2;

        // 为什么说String效率要比StringBuilder，如下示例，总是在不断的创建和销毁StringBuilder对象
        String s = "";
        for (int i = 0; i < 100; i++) {
            s = s + i;
        }
    }


    /**
     * 多数equals的string对象，其value属性是==的
     */
    @Test
    public void test3() {
        String s1 = "112"; // 返回常量池的引用
        String s2 = new String("112"); // 返回堆中的引用
        String s3 = new String("112"); // 返回堆中的引用
        String s4 = "11" + "2"; // 编译器自动优化为"12"

        // 验证s1,s2,s3的value指向的是同一个char[]数组对象
        System.out.println(getValue(s1)==getValue(s2));// true
        System.out.println(getValue(s1)==getValue(s3));// true
        System.out.println(getValue(s1)==getValue(s4));// true

        String temp1 = "2";
        String s5 = "11" + temp1;
//        s5 = new StringBuilder("11").append(temp1).toString(); // String s5 = "11" + temp1的底层实现
        // 通过源代码可知，s5的value指向的char[]是新new出来的，而且s5也是new新new出来的
        System.out.println(getValue(s1) == getValue(s5));// false

        final String temp2 = "2";
        String s6 = "11" + temp2; // 底层：因为temp2是常量，故编译时优化成s6 = "112"。故s6.value=="112".value
        System.out.println(getValue(s1)==getValue(s6));// true

    }

    // ============================= String的地址测试 =============================

    @Test
    public void test5() {
        String s1 = "你好";
        String s2 = new String(new char[]{'你', '好'});
        System.out.println(s1 == s2);
    }

    @Test
    public void test6() {
        String s2 = new String(new char[]{'你', '好'});
        String s1 = "你好";
        System.out.println(s1 == s2);
    }

    @Test
    public void test7() {
        String s2 = new String(new char[]{'你', '好'});
        s2.intern();
        String s1 = "你好";
        System.out.println(s1 == s2);
    }

    @Test
    public void test8() {
        String s0 = "你好";
        String s2 = new String(new char[]{'你', '好'});
        s2.intern();
        String s1 = "你好";
        System.out.println(s1 == s2);
    }

    @Test
    public void test9() {
        String s2 = new String(new char[]{'你', '好'});
        String s1 = s2.intern();
        System.out.println(s1 == s2);
    }

    @Test
    public void test10() {
        // jdk1.7: str2直接用new String（“你好”）创建，"你好"这字符串在一出现就自动创建成对象存放到常量池中，所以常量池里面存放的是"你好"字符串的引用，并不是str7创建的对象的引用。
        String s2 = new String("你好");
        String s1 = s2.intern();
        System.out.println(s1 == s2);
    }

    @Test
    public void test11() {
        String s0 = "你好";
        String s2 = new String(new char[]{'你', '好'});
        String s1 = s2.intern();
        System.out.println(s1 == s2);
    }


    private Object getValue(String s) {
        try {
            Field f = s.getClass().getDeclaredField("value");
            f.setAccessible(true);
            return f.get(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
