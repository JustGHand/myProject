package com.pw.codeset;

/**
 * 策略
 */
public class SpiImpl1 implements SPIService{
    @Override
    public void execute() {
        System.out.println("SpiImpl1 execute");
    }
}
