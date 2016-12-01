/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of this application.
 *
 * @author Kaito Yamada
 */
@SpringBootApplication
public class Application {

  /**
   * Main method of this application.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
