/*
 * Goslings - Git Repository Visualizer
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.controller;

import java.time.LocalDateTime;

/**
 * Error information
 *
 * @author Kaito Yamada
 */
public final class ErrorInfo {

  private final String dateTime;
  private final String invokedUrl;
  private final String message;

  /**
   * @param invokedUrl invokedUrl
   * @param ex ex
   */
  public ErrorInfo(String invokedUrl, Exception ex) {
    if (invokedUrl == null) {
      throw new NullPointerException("invokedUrl is null.");
    }
    if (ex == null) {
      throw new NullPointerException("ex is null.");
    }
    this.dateTime = LocalDateTime.now().toString();
    this.invokedUrl = invokedUrl;
    this.message = ex.getMessage();
  }

  /**
   * @return dateTime
   */
  public String getDateTime() {
    return dateTime;
  }

  /**
   * @return the URL which invoked the error.
   */
  public String getInvokedUrl() {
    return invokedUrl;
  }

  /**
   * @return message
   */
  public String getMessage() {
    return message;
  }

}
