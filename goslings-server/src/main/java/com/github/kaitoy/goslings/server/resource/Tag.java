/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.resource;

/**
 * Annotated/Lightweight Tag
 *
 * @author Kaito Yamada
 */
public final class Tag {

  private final String name;
  private final String tagObjectId;
  private final String referentId;

  /**
   * @param name name
   * @param tagObjectId tagObjectId
   * @param referentId referentId
   */
  public Tag(String name, String tagObjectId, String referentId) {
    if (name == null) {
      throw new NullPointerException("name is null.");
    }
    if (referentId == null) {
      throw new NullPointerException("referentId is null.");
    }
    this.name = name;
    this.tagObjectId = tagObjectId;
    this.referentId = referentId;
  }

  /**
   * @return name. Never null.
   */
  public String getName() {
    return name;
  }

  /**
   * @return tagObjectId. Maybe null.
   */
  public String getTagObjectId() {
    return tagObjectId;
  }

  /**
   * @return referentId. Never null.
   */
  public String getReferentId() {
    return referentId;
  }

}
