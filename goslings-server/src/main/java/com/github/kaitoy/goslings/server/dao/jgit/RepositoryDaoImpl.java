/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao.jgit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.github.kaitoy.goslings.server.BeanQualifiers;
import com.github.kaitoy.goslings.server.dao.DaoException;
import com.github.kaitoy.goslings.server.dao.RepositoryDao;
import com.github.kaitoy.goslings.server.resource.Index;
import com.github.kaitoy.goslings.server.resource.Index.IndexEntry;

/**
 * Implementation of {@link RepositoryDao} by JGit.
 *
 * @author Kaito Yamada
 */
@Repository
@Qualifier(BeanQualifiers.DAO_JGIT)
public final class RepositoryDaoImpl implements RepositoryDao {

  private static final Logger LOG = LoggerFactory.getLogger(RepositoryDaoImpl.class);
  private static final RepositoryResolver resolver = RepositoryResolver.getInstance();

  @Override
  public String getToken(String uri) {
    return resolver.getToken(uri);
  }

  @Override
  public long getIndexLastModified(String token) {
    try {
      long lastModified = resolver.getRepository(token).getIndexFile().lastModified();
      if (lastModified == 0L) {
        String message
          = new StringBuilder()
              .append("Failed to get the time the index in the repository")
              .append(token)
              .append(" was last modified due to an I/O error.")
              .toString();
        LOG.error(message);
        throw new DaoException(message);
      }
      return lastModified;
    } catch (NoWorkTreeException e) {
      String message
        = new StringBuilder()
            .append("The repository ")
            .append(token)
            .append(" is bare and so doesn't have index.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    }
  }

  @Override
  public Index getIndex(String token) {
    try {
      DirCache index = resolver.getRepository(token).readDirCache();
      int numEntries = index.getEntryCount();
      List<IndexEntry> entries = new ArrayList<>(numEntries);
      for (int i = 0; i < numEntries; i++) {
        DirCacheEntry dce = index.getEntry(i);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dce.getFileMode().copyTo(baos);
        entries.add(
          new IndexEntry(
            dce.getObjectId().getName(),
            dce.getPathString(),
            baos.toString(),
            dce.getStage()
          )
        );
      }
      return new Index(entries.toArray(new IndexEntry[numEntries]));
    } catch (NoWorkTreeException e) {
      String message
        = new StringBuilder()
            .append("The repository ")
            .append(token)
            .append(" is bare and so doesn't have index.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    } catch (CorruptObjectException e) {
      String message
        = new StringBuilder()
            .append("Filed to get index of the repository ")
            .append(token)
            .append(" due to an internal error.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    } catch (IOException e) {
      String message
        = new StringBuilder()
            .append("Filed to get index of the repository ")
            .append(token)
            .append(" due to an I/O error.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    }

  }

  @Override
  public String getIndexContents(String token) {
    Index index = getIndex(token);
    StringBuilder sb = new StringBuilder();
    for (IndexEntry entry :index.getEntries()) {
      sb.append(entry.getMode()).append(" ")
        .append(entry.getId()).append(" ")
        .append(entry.getStage()).append("\t")
        .append(entry.getPath()).append("\n");
    }
    return sb.toString();
  }

}
