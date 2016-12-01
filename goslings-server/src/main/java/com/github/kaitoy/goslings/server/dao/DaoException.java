/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao;

import com.github.kaitoy.goslings.server.controller.ErrorInfo;

/**
 * This is thrown in DAO classes and handled by an exception handler.
 * The message in this instance is supposed to be for users and is sent to client using
 * {@link ErrorInfo}.
 *
 * @author Kaito Yamada
 */
public final class DaoException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -2017426633314347619L;

  /**
   *
   */
  public DaoException() {
    super();
  }

  /**
   * @param message an error message for users.
   */
  public DaoException(String message){
    super(message);
  }

  /**
   * @param message an error message for users.
   * @param cause cause
   */
  public DaoException(String message, Throwable cause){
      super(message, cause);
  }

  /**
   * @param cause cause
   */
  public DaoException(Throwable cause){
      super(cause);
  }

}
