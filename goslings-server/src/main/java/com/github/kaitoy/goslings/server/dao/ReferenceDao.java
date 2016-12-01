/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao;

import com.github.kaitoy.goslings.server.resource.Branch;
import com.github.kaitoy.goslings.server.resource.SymbolicReference;
import com.github.kaitoy.goslings.server.resource.Tag;

/**
 * DAO to handle references.
 *
 * @author Kaito Yamada
 */
public interface ReferenceDao {

  /**
   * Get all branches in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of branches. Never null.
   * @throws DaoException if any errors.
   */
  public Branch[] getBranches(String token) throws DaoException;

  /**
   * Get all tags in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of tags. Never null.
   * @throws DaoException if any errors.
   */
  public Tag[] getTags(String token) throws DaoException;

  /**
   * Get HEAD, ORIG_HEAD, FETCH_HEAD, and MERGE_HEAD in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of symbolic references. Never null.
   * @throws DaoException if any errors.
   */
  public SymbolicReference[] getSymbolicReferences(String token) throws DaoException;

  /**
   * Get contents of a (symbolic) reference file.
   *
   * @param token the token that corresponds to the repository.
   * @param refFullName the full name of the reference. (e.g. HEAD, ref/heads/master, etc.)
   * @return contents of the reference. Never null.
   * @throws DaoException if any errors.
   */
  public String getContents(String token, String refFullName) throws DaoException;

}
