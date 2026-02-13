package com.example.userauth.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration for controller layer tests.
 * Keeps test wiring minimal and avoids pulling in real app context beans.
 */
@TestConfiguration
public class TestConfig {
    // Intentionally empty. Path is used only to satisfy @ContextConfiguration(classes = {Controller.class, TestConfig.class})
}
