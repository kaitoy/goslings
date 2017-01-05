/*
 * Goslings - Git Repository Visualizer
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao;

import com.github.kaitoy.goslings.server.resource.Commit;
import com.github.kaitoy.goslings.server.resource.Tree;

/**
 * DAO to handle Git object's contents.
 *
 * @author Kaito Yamada
 */
public interface ObjectDao {

  /**
   * Get all commits in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of commits. Never null.
   * @throws DaoException if any errors.
   */
  public Commit[] getCommits(String token) throws DaoException;

  /**
   * Get tree objects.
   * @param token the token that corresponds to the repository.
   * @param objectIds object IDs.
   * @return a list of tree objects. Never null.
   * @throws DaoException if any errors.
   */
  public Tree[] getTrees(String token, String[] objectIds) throws DaoException;

  /**
   * Get contents of the specified Git object in the specified repository.
   *
   * @param token the token that corresponds to the repository.
   * @param objectId objectId
   * @return contents of the object. Never null.
   * @throws DaoException if any errors.
   */
  public String getContents(String token, String objectId) throws DaoException;

}
