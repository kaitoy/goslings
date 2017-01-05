/*
 * Goslings - Git Repository Visualizer
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao.jgit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.kaitoy.goslings.server.BeanQualifiers;
import com.github.kaitoy.goslings.server.dao.DaoException;
import com.github.kaitoy.goslings.server.dao.ReferenceDao;
import com.github.kaitoy.goslings.server.resource.Branch;
import com.github.kaitoy.goslings.server.resource.SymbolicReference;
import com.github.kaitoy.goslings.server.resource.Tag;

/**
 * Implementation of {@link ReferenceDao} by JGit.
 *
 * @author Kaito Yamada
 */
@org.springframework.stereotype.Repository
@Qualifier(BeanQualifiers.DAO_JGIT)
public final class ReferenceDaoImpl implements ReferenceDao {

  private static final Logger LOG = LoggerFactory.getLogger(ReferenceDaoImpl.class);
  private static final RepositoryResolver resolver = RepositoryResolver.getInstance();
  private static final String[] SYMBOLIC_REFS
    = new String[] { "HEAD", "ORIG_HEAD", "FETCH_HEAD", "MERGE_HEAD" };

  @Override
  public Branch[] getBranches(String token) {
    try {
      return StreamSupport.stream(resolver.getGit(token).branchList().call().spliterator(), false)
               // JGit mix up the detached HEAD with branches,
               // so filter it out firstly.
               .filter(ref -> !ref.getName().equals("HEAD"))
               .map(this::convertToBranch)
               .toArray(Branch[]::new);
    } catch (GitAPIException e) {
      String message
        = new StringBuilder()
            .append("Failed to get branches in the repository ")
            .append(token)
            .append(" due to an error of the Git command.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    }
  }

  /**
   * This method converts a {@link Ref} object to a {@link Branch} object.
   *
   * @param ref a {@link Ref} object representing a branch.
   * @return a new {@link Branch} instance.
   */
  private Branch convertToBranch(Ref ref) {
    return new Branch(ref.getName(), ref.getObjectId().getName());
  }

  @Override
  public Tag[] getTags(String token) {
    try {
      return StreamSupport.stream(resolver.getGit(token).tagList().call().spliterator(), false)
               .map(this::convertToTag)
               .toArray(Tag[]::new);
    } catch (GitAPIException e) {
      String message
        = new StringBuilder()
            .append("Failed to get tags in the repository ")
            .append(token)
            .append(" due to an error of the Git command.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    }
  }

  /**
   * this method converts a {@link Ref} object to {@link Tag} object.
   *
   * @param ref a {@link Ref} object representing a tag.
   * @return a new {@link Tag} instance.
   */
  private Tag convertToTag(Ref ref) {
    ObjectId peeledObjId = ref.getPeeledObjectId();
    if (peeledObjId == null) {
      // lightweight tag
      return new Tag(ref.getName(), null, ref.getObjectId().getName());
    }
    else {
      // annotated tag
      return new Tag(ref.getName(), ref.getObjectId().getName(), peeledObjId.getName());
    }
  }

  @Override
  public SymbolicReference[] getSymbolicReferences(String token) {
    Repository repo = resolver.getRepository(token);
    return Arrays.stream(SYMBOLIC_REFS)
      .map(refName -> {
        try {
          Ref ref = repo.findRef(refName);
          if (ref == null) {
            return null;
          }

          if (ref.isSymbolic()) {
            // attached to a branch
            return new SymbolicReference(ref.getName(), ref.getTarget().getName());
          }
          else {
            // detached
            return new SymbolicReference(ref.getName(), ref.getObjectId().getName());
          }
        } catch (IOException e) {
          String message
            = new StringBuilder()
                .append("Failed to find HEAD in the repository ")
                .append(token)
                .append(" due to an I/O error.")
                .toString();
          LOG.error(message, e);
          throw new DaoException(message, e);
        }
      })
      .filter(ref -> ref != null)
      .toArray(SymbolicReference[]::new);
  }

  @Override
  public String getContents(String token, String refFullName) {
    File gitDir = resolver.getRepository(token).getDirectory();
    if (gitDir == null) {
      String message
        = new StringBuilder()
            .append("Failed to get contents of the ref ")
            .append(refFullName)
            .append("in the repository ")
            .append(token)
            .append(". The repository is not local.")
            .toString();
      LOG.error(message);
      throw new DaoException(message);
    }

    File refFile = new File(resolver.getRepository(token).getDirectory(), refFullName);
    if (!refFile.exists()) {
      String message
        = new StringBuilder()
            .append("Failed to get contents of the ref ")
            .append(refFullName)
            .append("in the repository ")
            .append(token)
            .append(". The ref doesn't exist.")
            .toString();
      LOG.error(message);
      throw new DaoException(message);
    }
    try {
      byte[] contents = Files.readAllBytes(refFile.toPath());
      return new String(contents);
    } catch (IOException e) {
      String message
        = new StringBuilder()
            .append("Failed to get contents of the ref ")
            .append(refFullName)
            .append("in the repository ")
            .append(token)
            .append(" due to an I/O error.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    }
  }

}
