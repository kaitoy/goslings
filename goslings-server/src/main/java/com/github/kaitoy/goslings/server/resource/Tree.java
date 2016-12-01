/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.resource;

import java.util.Map;

/**
 * Tree object.
 *
 * @author Kaito Yamada
 */
public final class Tree {

  private final String id;
  private final Map<String, String> trees;
  private final Map<String, String> blobs;

  /**
   * @param id id
   * @param trees trees
   * @param blobs blobs
   */
  public Tree(String id, Map<String, String> trees, Map<String, String> blobs) {
    if (id == null) {
      throw new NullPointerException("id is null.");
    }
    if (trees == null) {
      throw new NullPointerException("trees is null.");
    }
    if (blobs == null) {
      throw new NullPointerException("blobs is null.");
    }
    this.id = id;
    this.trees = trees;
    this.blobs = blobs;
  }

  /**
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   * @return trees (mapping from object IDs to directory names). Never null.
   */
  public Map<String, String> getTrees() {
    return trees;
  }

  /**
   * @return blobs (mapping from object IDs file names). Never null.
   */
  public Map<String, String> getBlobs() {
    return blobs;
  }

}
