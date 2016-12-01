/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.controller;

/**
 * This is thrown if the server receives a request with illegal parameters.
 * The message in this instance is supposed to be for users and is sent to client using
 * {@link ErrorInfo}.
 *
 * @author Kaito Yamada
 */
public final class BadRequestException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 721031396637761940L;

  /**
   *
   */
  public BadRequestException() {
    super();
  }

  /**
   * @param message an error message for users.
   */
  public BadRequestException(String message){
    super(message);
  }

  /**
   * @param message an error message for users.
   * @param cause cause
   */
  public BadRequestException(String message, Throwable cause){
      super(message, cause);
  }

  /**
   * @param cause cause
   */
  public BadRequestException(Throwable cause){
      super(cause);
  }

}
