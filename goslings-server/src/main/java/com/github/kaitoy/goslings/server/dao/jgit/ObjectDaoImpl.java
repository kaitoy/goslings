/*
 * Goslings - Git Repository Visualizer
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao.jgit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.github.kaitoy.goslings.server.BeanQualifiers;
import com.github.kaitoy.goslings.server.dao.DaoException;
import com.github.kaitoy.goslings.server.dao.ObjectDao;
import com.github.kaitoy.goslings.server.resource.Commit;
import com.github.kaitoy.goslings.server.resource.Tree;

/**
 * Implementation of {@link ObjectDao} by JGit.
 *
 * @author Kaito Yamada
 */
@Repository
@Qualifier(BeanQualifiers.DAO_JGIT)
public final class ObjectDaoImpl implements ObjectDao {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectDaoImpl.class);
  private static final RepositoryResolver resolver = RepositoryResolver.getInstance();

  @Override
  public Commit[] getCommits(String token) {
    try {
      return StreamSupport.stream(resolver.getGit(token).log().all().call().spliterator(), false)
               .map(this::convertToCommit)
               .toArray(Commit[]::new);
    } catch (NoHeadException  e) {
      String message
        = new StringBuilder()
            .append("Failed to get commits in the repository ")
            .append(token)
            .append(" because it doesn't have HEAD.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    } catch (GitAPIException e) {
      String message
        = new StringBuilder()
            .append("Failed to get commits in the repository ")
            .append(token)
            .append(" due to an error of the Git command.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    } catch (IOException e) {
      String message
        = new StringBuilder()
            .append("Failed to get commits in the repository ")
            .append(token)
            .append(" due to an I/O error.")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    }
  }

  @Override
  @Cacheable
  public Tree[] getTrees(String token, String[] objectIds) throws DaoException {
    try (RevWalk walk = new RevWalk(resolver.getRepository(token))) {
      List<Tree> trees = new ArrayList<>(objectIds.length);
      for (String objectId: objectIds) {
        try {
          RevObject obj = walk.parseAny(ObjectId.fromString(objectId));
          if (obj.getType() != Constants.OBJ_TREE) {
            String message
              = new StringBuilder()
                  .append("Failed to get a tree in the repository ")
                  .append(token)
                  .append(". ")
                  .append(objectId)
                  .append(" is not a tree.")
                  .toString();
            LOG.error(message + "It's {}.", obj.getClass());
            throw new DaoException(message);
          }
          trees.add(convertToTree(token, (RevTree) obj));
        } catch (MissingObjectException e) {
          String message
            = new StringBuilder()
                .append("Failed to get a tree in the repository ")
                .append(token)
                .append(". ")
                .append(objectId)
                .append(" doesn't exist.")
                .toString();
          LOG.error(message);
          throw new DaoException(message, e);
        } catch (IOException e) {
          String message
            = new StringBuilder()
                .append("Failed to get a tree ")
                .append(objectId)
                .append(" in the repository ")
                .append(token)
                .append(" due to an I/O error.")
                .toString();
          LOG.error(message);
          throw new DaoException(message, e);
        }
      }
      return trees.toArray(new Tree[objectIds.length]);
    }
  }

  @Override
  @Cacheable
  public String getContents(String token, String objectId) {
    RawContents rawContents = getRawContents(token, objectId);
    if (rawContents.type == Constants.OBJ_TREE) {
      try {
        List<TreeEntry> entries = parseTree(rawContents.contents);
        StringBuilder sb = new StringBuilder();
        for (TreeEntry entry: entries) {
          sb.append(entry.mode)
            .append(entry.isTree() ? " tree " : " blob ")
            .append(entry.id)
            .append(" ")
            .append(entry.name)
            .append("\n");
        }
        return sb.toString();
      } catch (IOException e) {
          String message
            = new StringBuilder()
                .append("Filed to get contents of the tree ")
                .append(objectId)
                .append(" in the repository ")
                .append(token)
                .append(" due to an I/O error.")
                .toString();
          LOG.error(message, e);
          throw new DaoException(message, e);
      }
    }
    else {
      return new String(rawContents.contents);
    }
  }

  @Cacheable
  private RawContents getRawContents(String token, String objectId) {
    try {
      ObjectLoader loader = resolver.getRepository(token).open(ObjectId.fromString(objectId));
      return new RawContents(loader.getType(), loader.getBytes());
    } catch (MissingObjectException e) {
      String message
        = new StringBuilder()
            .append("The specified object ")
            .append(objectId)
            .append(" doesn't exist in the repository ")
            .append(token)
            .append(".")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    } catch (IOException e) {
      String message
        = new StringBuilder()
            .append("Failed to get contents of the specified object ")
            .append(objectId)
            .append(" in the repository ")
            .append(token)
            .append(".")
            .toString();
      LOG.error(message, e);
      throw new DaoException(message, e);
    }
  }

  private Commit convertToCommit(RevCommit commit) {
    return new Commit(
             commit.getName(),
             Arrays.stream(commit.getParents())
               .map(parent -> parent.getName())
               .toArray(String[]::new),
             commit.getTree().getName()
           );
  }

  private Tree convertToTree(String token, RevTree tree) {
    byte[] rawContents = getRawContents(token, tree.getName()).contents;
    try {
      List<TreeEntry> entries = parseTree(rawContents);
      Map<String, String> trees = new HashMap<>();
      Map<String, String> blobs = new HashMap<>();
      for (TreeEntry entry: entries) {
        if (entry.isTree()) {
          trees.put(entry.id, entry.name);
        }
        else {
          blobs.put(entry.id, entry.name);
        }
      }
      return new Tree(tree.getName(), trees, blobs);
    } catch (IOException e) {
        String message
          = new StringBuilder()
              .append("Filed to get the tree ")
              .append(tree.getName())
              .append(" in the repository ")
              .append(token)
              .append(" due to an I/O error.")
              .toString();
        LOG.error(message, e);
        throw new DaoException(message, e);
    }
  }

  @Cacheable
  private List<TreeEntry> parseTree(byte[] tree) throws IOException {
    List<TreeEntry> entries = new ArrayList<>();
    ByteArrayInputStream in = new ByteArrayInputStream(tree);
    byte[] mode = new byte[6];
    ByteArrayOutputStream nameStream = new ByteArrayOutputStream();
    byte[] rawId = new byte[20];
    while (in.available() > 0) {
      in.read(mode);
      String modeStr = new String(mode);
      if (modeStr.equals("40000 ")) {
        modeStr = "040000";
      }
      else {
        in.skip(1);
      }

      int nameByte;
      while ((nameByte = in.read()) != 0x00) {
        nameStream.write(nameByte);
      }
      String name = new String(nameStream.toByteArray());

      in.read(rawId);
      String id = DatatypeConverter.printHexBinary(rawId).toLowerCase();

      entries.add(new TreeEntry(modeStr, id, name));
      nameStream.reset();
    }

    return entries;
  }

  private static final class RawContents {

    private final int type;
    private final byte[] contents;

    private RawContents(int type, byte[] contents) {
      this.type = type;
      this.contents = contents;
    }

  }

  private static final class TreeEntry {

    private final String mode;
    private final String id;
    private final String name;

    TreeEntry(String mode, String id, String name) {
      this.mode = mode;
      this.id = id;
      this.name = name;
    }

    private boolean isTree() {
      return mode.equals("040000");
    }

  }

}
