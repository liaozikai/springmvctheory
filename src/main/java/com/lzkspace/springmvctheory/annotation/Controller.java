package com.lzkspace.springmvctheory.annotation;

import java.lang.annotation.*;

/**
 * @author : liaozikai
 * file : Controller.java
 */
@Documented // 拥有该注解的java文件通过javadoc命令可生成html文件，方便查看其api
@Target(ElementType.TYPE)// 作用于类上，即Controller不能标注在非类的元素上
@Retention(RetentionPolicy.RUNTIME) // 注解不仅存在class文件中，而且存在于jvm加载class文件过程中，即其生命周期贯穿整个jvm编译过程（前端编译和后端编译，详细可参考大神http://www.hollischuang.com/archives/2322）
public @interface Controller {
    /**
     * 阿里开发手册建议接口类不需添加public修饰符，因为接口内方法默认是public
     * 用于标注了Controller的类的名称
     * @return
     */
    String value();
}
