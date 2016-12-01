/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Collected constants of bean qualifiers.
 *
 * @author Kaito
 * @see Qualifier
 */
public final class BeanQualifiers {

  /**
   *
   */
  public static final String DAO_JGIT = "jgit";

  private BeanQualifiers() {
    throw new AssertionError("Don't instantiate me.");
  }

}
