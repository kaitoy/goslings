/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.github.kaitoy.goslings.server.BeanQualifiers;
import com.github.kaitoy.goslings.server.dao.DaoException;
import com.github.kaitoy.goslings.server.dao.ObjectDao;
import com.github.kaitoy.goslings.server.dao.ReferenceDao;
import com.github.kaitoy.goslings.server.dao.RepositoryDao;
import com.github.kaitoy.goslings.server.resource.Branch;
import com.github.kaitoy.goslings.server.resource.Commit;
import com.github.kaitoy.goslings.server.resource.Index;
import com.github.kaitoy.goslings.server.resource.StringWrapper;
import com.github.kaitoy.goslings.server.resource.SymbolicReference;
import com.github.kaitoy.goslings.server.resource.Tag;
import com.github.kaitoy.goslings.server.resource.Tree;

/**
 * REST API v1 Controller
 *
 * @author Kaito Yamada
 */
@RestController
@RequestMapping(
  path="/v1",
  method=RequestMethod.GET
)
public final class RestApiV1Controller {

  private static final String URI_PREFIX_PROP = "com.github.kaitoy.goslings.server.uriPrefix";
  private static final String uriPrefix;

  @Autowired
  @Qualifier(BeanQualifiers.DAO_JGIT)
  private RepositoryDao repositoryDao;

  @Autowired
  @Qualifier(BeanQualifiers.DAO_JGIT)
  private ObjectDao objectDao;

  @Autowired
  @Qualifier(BeanQualifiers.DAO_JGIT)
  private ReferenceDao referenceDao;

  static {
    uriPrefix = System.getProperty(URI_PREFIX_PROP);
  }

  /**
   * API to get a repository token for the given URI.
   *
   * @param uri URI of the repository.
   * @return a repository token corresponding to a single Git repository. Never null.
   * @throws DaoException if an error occurred in DAO.
   * @throws BadRequestException if uri is invalid.
   */
  @RequestMapping(path="tokens")
  public StringWrapper getToken(@RequestParam("uri") String uri) {
    if (uri == null || uri.isEmpty()) {
      throw new BadRequestException("The required parameter 'uri' is not set.");
    }
    if (uriPrefix != null && !uriPrefix.isEmpty() && !uri.startsWith(uriPrefix)) {
      throw new BadRequestException("URI has to start with " + uriPrefix);
    }
    return new StringWrapper(repositoryDao.getToken(uri));
  }

  /**
   * API to get all commits in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of commits. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/objects/commits")
  public Commit[] getCommits(@PathVariable String token) {
    return objectDao.getCommits(token);
  }

  /**
   * API to get all branches in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of branches. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/refs/branches")
  public Branch[] getBranches(@PathVariable String token) {
    return referenceDao.getBranches(token);
  }

  /**
   * API to get all tags in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of tags. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/refs/tags")
  public Tag[] getTags(@PathVariable String token) {
    return referenceDao.getTags(token);
  }

  /**
   * API to get HEAD, ORIG_HEAD, FETCH_HEAD, and MERGE_HEAD in the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return a list of symbolic references. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/symrefs")
  public SymbolicReference[] getSymrefs(@PathVariable String token) {
    return referenceDao.getSymbolicReferences(token);
  }

  /**
   * API to get contents of a Git object.
   *
   * @param token the token that corresponds to the repository.
   * @param objectId object ID
   * @param req HTTP request
   * @param res HTTP response
   * @return contents of the object. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/objects/{objectId:[0-9a-f]{40}}/contents")
  public StringWrapper getObjectContents(
    @PathVariable String token,
    @PathVariable String objectId,
    HttpServletRequest req,
    HttpServletResponse res
  ) {
    res.setHeader("Cache-Control", "public");
    res.setHeader("ETag", objectId);
    if (req.getHeader("if-none-match") != null) {
      res.setStatus(HttpStatus.NOT_MODIFIED.value());
      return null;
    }
    return new StringWrapper(objectDao.getContents(token, objectId));
  }

  /**
   * API to get contents of a reference file.
   *
   * @param token the token that corresponds to the repository.
   * @param type the type the reference. (i.e. heads, remotes, or tags)
   * @param name the name the reference. (e.g. master)
   * @return contents of the reference. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/refs/{type:heads|remotes|tags}/{name}/contents")
  public StringWrapper getRefContents(
    @PathVariable String token,
    @PathVariable String type,
    @PathVariable String name
  ) {
    StringBuilder sb
      = new StringBuilder()
          .append("refs/")
          .append(type)
          .append("/")
          .append(name);
    return new StringWrapper(referenceDao.getContents(token, sb.toString()));
  }

  /**
   * API to get contents of a symbolic reference file.
   *
   * @param token the token that corresponds to the repository.
   * @param name the name the reference. (e.g. H)
   * @return contents of the reference. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/symrefs/{name}/contents")
  public StringWrapper getSymrefContents(
    @PathVariable String token,
    @PathVariable String name
  ) {
    return new StringWrapper(referenceDao.getContents(token, name));
  }

  /**
   * API to get the index of the repository.
   *
   * @param token the token that corresponds to the repository.
   * @return index. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/index")
  public Index getIndex(@PathVariable String token) {
    return repositoryDao.getIndex(token);
  }

  /**
   * API to get contents of the index of the repository.
   *
   * @param token the token that corresponds to the repository.
   * @param req Web request
   * @param res HTTP response
   * @return contents of the index. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/index/contents")
  public StringWrapper getIndexContents(
    @PathVariable String token,
    WebRequest req,
    HttpServletResponse res
  ) {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
    calendar.set(1983, 12, 14);
    res.setHeader("Expires", sdf.format(calendar.getTime()));
    res.setHeader("Cache-Control", "max-age=0");
    if (req.checkNotModified(repositoryDao.getIndexLastModified(token))) {
      return null;
    }
    return new StringWrapper(repositoryDao.getIndexContents(token));
  }

  /**
   * API to get tree objects.
   *
   * @param token the token that corresponds to the repository.
   * @param objectIds object IDs
   * @return a list of tree objects. Never null.
   * @throws DaoException if an error occurred in DAO.
   */
  @RequestMapping(path="{token}/objects/trees/{objectIds:[0-9a-f]{40}(?:,[0-9a-f]{40})*}")
  public Tree[] getTree(@PathVariable String token, @PathVariable String[] objectIds) {
    return objectDao.getTrees(token, objectIds);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(DaoException.class)
  ErrorInfo handleDaoException(HttpServletRequest req, Exception ex) {
    return new ErrorInfo(req.getRequestURL().toString(), ex);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  ErrorInfo handleBadRequestException(HttpServletRequest req, Exception ex) {
    return new ErrorInfo(req.getRequestURL().toString(), ex);
  }

}
