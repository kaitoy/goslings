/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.resource;

/**
 * Symbolic Reference
 *
 * @author Kaito Yamada
 */
public final class SymbolicReference {

  private final String name;
  private final String referent;

  /**
   * @param name name
   * @param referent referent
   */
  public SymbolicReference(String name, String referent) {
    if (name == null) {
      throw new NullPointerException("name is null.");
    }
    if (referent == null) {
      throw new NullPointerException("referent is null.");
    }
    this.name = name;
    this.referent = referent;
  }

  /**
   * @return name. Never null.
   */
  public String getName() {
    return name;
  }

  /**
   * @return referent. Never null.
   */
  public String getReferent() {
    return referent;
  }

}
