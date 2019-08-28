package com.victorm.backend.spring;

import com.victorm.backend.GameServer;

import javax.annotation.PreDestroy;

public class TerminateBean {

    @PreDestroy
    public void onDestroy() {
        GameServer.stop();
    }
}
