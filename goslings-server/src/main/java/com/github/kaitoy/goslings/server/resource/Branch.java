/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.resource;

/**
 * Branch
 *
 * @author Kaito Yamada
 */
public final class Branch {

  private final String name;
  private final String referentId;

  /**
   * @param name name
   * @param referentId referentId
   */
  public Branch(String name, String referentId) {
    if (name == null) {
      throw new NullPointerException("name is null.");
    }
    if (referentId == null) {
      throw new NullPointerException("referentId is null.");
    }
    this.name = name;
    this.referentId = referentId;
  }

  /**
   * @return name. Never null.
   */
  public String getName() {
    return name;
  }

  /**
   * @return referentId. Never null.
   */
  public String getReferentId() {
    return referentId;
  }

}
