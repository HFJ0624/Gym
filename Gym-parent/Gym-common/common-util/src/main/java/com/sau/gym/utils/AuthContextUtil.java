package com.sau.gym.utils;

import com.sau.gym.model.entity.user.User;

/**
 * 作者:hfj
 * 功能:完成该功能需要使用到ThreadLocal，ThreadLocal是jdk所提供的一个线程工具类，叫做线程变量，
 * 意思是ThreadLocal中填充的变量属于当前线程，该变量对其他线程而言是隔离的，
 * 也就是说该变量是当前线程独有的变量，使用该工具类可以实现在同一个线程进行数据的共享。
 * 日期: 2026/3/5 9:24
 */
public class AuthContextUtil {

    //创建一个ThreadLocal对象
    private static final ThreadLocal<User> threadlocal = new ThreadLocal<>();

    //定义存储数据的静态方法
    public static void set(User user){
        threadlocal.set(user);
    }

    //定义获取数据的方法
    public static User get(){
        return threadlocal.get();
    }

    //删除数据的方法
    public static void remove() {
        threadlocal.remove();
    }

}
