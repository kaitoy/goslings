/*
 * Goslings - Git Repository Visualizer
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.resource;

/**
 * Index.
 *
 * @author Kaito Yamada
 */
public final class Index {

  private final IndexEntry[] entries;

  /**
   * @param entries entries
   */
  public Index(IndexEntry[] entries) {
    if (entries == null) {
      throw new NullPointerException("entries is null.");
    }
    this.entries = entries;
  }

  /**
   * @return entries
   */
  public IndexEntry[] getEntries() {
    return entries;
  }

  /**
   * Index entry
   * @author Kaito Yamada
   */
  public static final class IndexEntry {

    private final String id;
    private final String path;
    private final String mode;
    private final int stage;

    /**
     * @param id object id
     * @param path path to a file
     * @param mode file mode
     * @param stage stage
     */
    public IndexEntry(String id, String path, String mode, int stage) {
      this.id = id;
      this.path = path;
      this.mode = mode;
      this.stage = stage;
    }

    /**
     * @return id
     */
    public String getId() {
      return id;
    }

    /**
     * @return path
     */
    public String getPath() {
      return path;
    }

    /**
     * @return mode
     */
    public String getMode() {
      return mode;
    }

    /**
     * @return stage
     */
    public int getStage() {
      return stage;
    }

  }

}
