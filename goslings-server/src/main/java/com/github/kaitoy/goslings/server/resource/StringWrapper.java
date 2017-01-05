/*
 * Goslings - Git Repository Visualizer
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.resource;

/**
 * This wraps a String object so it can be sent to client as JSON.
 *
 * @author Kaito Yamada
 */
public final class StringWrapper {

  private final String text;

  /**
   * @param text text
   */
  public StringWrapper(String text) {
    if (text == null) {
      throw new NullPointerException("text is null.");
    }
    this.text = text;
  }

  /**
   * @return text. Never null.
   */
  public String getText() {
    return text;
  }

}
