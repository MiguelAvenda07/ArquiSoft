package com.udea.lab1arq.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @Test
    void healthDevuelveOk() {
        assertEquals("HEALTH CHECK OK!", new HealthController().health());
    }
}