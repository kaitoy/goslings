/*
 * Goslings - Git Repository Visualizer
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao;

import com.github.kaitoy.goslings.server.resource.Index;

/**
 * DAO to handle Git repository itself.
 *
 * @author Kaito Yamada
 */
public interface RepositoryDao {

  /**
   * Get the token for the repository at the given URI.
   * A token can be used to specify a repository when using DAOs such as {@link ObjectDao}.
   * When this method returns a token, the corresponding repository is ready to access via
   * DAOs (i.e. its clone has completed, or so).
   *
   * @param uri URI of the repository
   * @return token. Never null.
   * @throws DaoException if any errors.
   */
  public String getToken(String uri) throws DaoException;

  /**
   * Get the time that the index in the repository was last modified.
   *
   * @param token the token that corresponds to the repository.
   * @return A long value representing the time the file was last modified,
   *         measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
   * @throws DaoException if any errors.
   */
  public long getIndexLastModified(String token) throws DaoException;

  /**
   * Get the index of the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return index. Never null.
   * @throws DaoException if any errors.
   */
  public Index getIndex(String token) throws DaoException;

  /**
   * Get contents of the index of the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return contents of the index. Never null.
   * @throws DaoException if any errors.
   */
  public String getIndexContents(String token) throws DaoException;

}
