/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.dao.jgit;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.WeakReferenceMonitor;

import com.github.kaitoy.goslings.server.dao.DaoException;

/**
 * This class resolves tokens to Git repositories.
 *
 * @author Kaito Yamada
 */
final class RepositoryResolver {

  private static final Logger LOG = LoggerFactory.getLogger(RepositoryResolver.class);
  private static final RepositoryResolver INSTANCE = new RepositoryResolver();
  private static final String REPOS_DIR_PROP = "com.github.kaitoy.goslings.server.reposDir";
  private static final String REPOS_DIR;

  /*
   * Mapping from tokens to Git objects. (cache for Git objects.)
   */
  private static final Map<String, Git> GITS = new ConcurrentHashMap<>();

  /*
   * Cache for tokens that are ready to use.
   */
  private static final Set<String> READY_TOKENS = Collections.synchronizedSet(new HashSet<>());

  /*
   * Mapping from tokens to lock objects (mutexes for git clone command for each repo).
   */
  private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();

  static {
    String reposDir = System.getProperty(REPOS_DIR_PROP);
    if (reposDir != null && !reposDir.isEmpty()) {
      REPOS_DIR = reposDir;
    }
    else {
      REPOS_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "goslings").toString();
    }
    new File(REPOS_DIR).mkdir();
  }

  private RepositoryResolver() {}

  /**
   * @return the singleton instance of this class.
   */
  static RepositoryResolver getInstance() { return INSTANCE; }

  /**
   * Get the token for the repository at the given URI.
   * If the URI points to a local directory, this create a symlink of it in the repositories
   * directory before returning the token.
   * If the URI points to a remote repository, this clones it in the repositories
   * directory before returning the token.
   *
   * @param uri URI of the repository
   * @return token. Never null.
   * @throws DaoException if any errors.
   */
  String getToken(String uri) {
    Token token = new Token(uri);
    if (READY_TOKENS.contains(token.tokenString)) {
      return token.tokenString;
    }

    if (token.isLocal) {
      return processLocalRepository(token);
    }
    else {
      return processRemoteRepository(token);
    }
  }

  private String processLocalRepository(Token token) {
    String tokenString = token.tokenString;
    String uri = token.uri;

    if (!new File(uri).exists()) {
      throw new DaoException(uri + " doesn't exist.");
    }

    try {
      Files.createSymbolicLink(Paths.get(REPOS_DIR, tokenString), Paths.get(uri));
      READY_TOKENS.add(tokenString);
      return tokenString;
    } catch (FileAlreadyExistsException e) {
      READY_TOKENS.add(tokenString);
      return tokenString;
    } catch (FileSystemException e) {
      LOG.error(
        "Goslings server seems not to be running as Administrator. "
          + "Failed to create a symlink of a repo {} due to: ",
        uri, e
      );
      throw new DaoException(
              "The server doesn't have enough privileges to handle the local URI: " + uri,
              e
            );
    } catch (IOException e) {
      LOG.error("Failed to create a symlink of a repo {} due to: ", uri, e);
      throw new DaoException(
              "An I/O error occured in the server during handling the local URI: " + uri,
              e
            );
    }
  }

  private String processRemoteRepository(Token token) {
    String tokenString = token.tokenString;
    String uri = token.uri;
    File repo = new File(REPOS_DIR, tokenString);
    Path lockFilePath = Paths.get(repo.getAbsolutePath() + ".lock");

    Object lock;
    synchronized (LOCKS) {
      lock = LOCKS.get(tokenString);
      if (lock == null) {
        lock = new Object();
        LOCKS.put(tokenString, lock);
      }
    }

    synchronized (lock) {
      try (
        FileChannel fc = FileChannel.open(
                           lockFilePath,
                           StandardOpenOption.CREATE,
                           StandardOpenOption.WRITE
                         );
        FileLock fileLock = fc.lock()
      ) {
        if (repo.exists()) {
          // Another process or thread has already cloned the repo.
          READY_TOKENS.add(tokenString);
          return tokenString;
        }

        Git git = Git.cloneRepository()
                    .setURI(uri)
                    .setBare(true)
                    .setDirectory(repo)
                    .call();
        GITS.put(tokenString, git);
        READY_TOKENS.add(tokenString);
        return tokenString;
      } catch (GitAPIException e) {
        LOG.error("Failed to clone a repo {} due to: ", uri, e);
        throw new DaoException(
                "The server failed to clone the repository. Please confirm the URL: " + uri,
                e
              );
      } catch (IOException e) {
        LOG.error("Failed to lock a repo {} due to: ", uri, e);
        throw new DaoException(
                "The server failed to clone the repository due to an I/O error. URI: " + uri,
                e
              );
      }
    }
  }

  /**
   * Get the {@link Git} instance which corresponds to the repository specified by the given token.
   *
   * @param token token
   * @return a {@link Git} instance. Never null.
   * @throws DaoException if any errors.
   */
  Git getGit(String token) {
    if (GITS.containsKey(token)) {
      return GITS.get(token);
    }

    File gitDir = Paths.get(REPOS_DIR, token).toFile();
    Git git = null;
    try {
      Repository repo
        = new FileRepositoryBuilder()
            .setGitDir(gitDir)
            .readEnvironment()
            .findGitDir()
            .build();
      git = new Git(repo);
      WeakReferenceMonitor.monitor(git, () -> repo.close());
      GITS.put(token, git);
      return git;
    } catch (IOException e) {
      LOG.error("Failed to build a repo {}", gitDir, e);
      throw new DaoException(
              "The server couldn't find a repository by the token: " + token,
              e
            );
    }
  }

  /**
   * Get the {@link Repository} instance which corresponds to the repository specified by the
   * given token.
   *
   * @param token token
   * @return a {@link Repository} instance. Never null.
   * @throws DaoException if any errors.
   */
  Repository getRepository(String token) {
    return getGit(token).getRepository();
  }

  private static final class Token {

    private final String uri;
    private final boolean isLocal;
    private final String tokenString;

    private Token(String uri) {
      this.uri = uri;

      String rawToken;
      boolean local;
      try {
        // TODO Does this work if SSH URL is passed?
        // e.g. ssh://git@github.com/kaitoy/pcap4j.git, git@github.com:kaitoy/pcap4j.git, etc.
        // TODO File URL should be treated as local.
        // e.g. file:///path/to/repo.git, file://C:/Users/Kaito/Desktop/pcap4j, etc.
        URL url = new URL(uri);
        StringBuilder sb
          = new StringBuilder()
              .append(url.getHost())
              .append("/")
              .append(url.getPath());
        rawToken = sb.toString();
        local = false;
      } catch (MalformedURLException e) {
        // Assume uri is a path to local repo.
        rawToken = new File(uri).getAbsolutePath();
        local = true;
      }
      this.isLocal = local;

      try {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(rawToken.getBytes());
        byte[] digest = md.digest();
        this.tokenString
          = IntStream.range(0, digest.length)
              .mapToObj(i -> String.format("%02x", digest[i] & 0xFF))
              .collect(Collectors.joining());
      } catch (NoSuchAlgorithmException e) {
        throw new AssertionError("Never gets here.");
      }
    }

  }

}
