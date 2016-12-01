/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.resource;

/**
 * Commit object.
 *
 * @author Kaito Yamada
 */
public final class Commit {

  private final String id;
  private final String[] parentIds;
  private final String treeId;

  /**
   * @param id id
   * @param parentIds parentIds
   * @param treeId treeId
   */
  public Commit(
    String id, String[] parentIds, String treeId
  ) {
    if (id == null) {
      throw new NullPointerException("id is null.");
    }
    if (parentIds == null) {
      throw new NullPointerException("parentIds is null.");
    }
    if (treeId == null) {
      throw new NullPointerException("treeId is null.");
    }
    this.id = id;
    this.parentIds = parentIds;
    this.treeId = treeId;
  }

  /**
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   * @return parentIds. Never null.
   */
  public String[] getParentIds() {
    return parentIds;
  }

  /**
   * @return treeId. Never null.
   */
  public String getTreeId() {
    return treeId;
  }

}
