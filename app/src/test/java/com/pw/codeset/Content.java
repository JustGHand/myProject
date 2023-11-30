package com.pw.codeset;

public class Content {
    private SPIService mSpiService;

    public Content(SPIService spiService) {
        this.mSpiService = spiService;
    }

    public void executeSPIService() {
        mSpiService.execute();
    }
}
