package com.pw.codeset;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        SPIService spiService = new SpiImpl1();
//        spiService.execute();




//        //策略模式
//        Content content = new Content(new SpiImpl1());
//        content.executeSPIService();



        //SPI机制 策略模式+IOC注入+配置文件
        ServiceLoader<SPIService> loader = ServiceLoader.load(SPIService.class);
        Iterator<SPIService> iterator = loader.iterator();
        //完成注入，并调用实现类
        while (iterator.hasNext()) {//一次拿到一个实现类的全名
            SPIService spi = iterator.next();// 通过反射new一个实现类对象
            spi.execute();
        }

    }
}