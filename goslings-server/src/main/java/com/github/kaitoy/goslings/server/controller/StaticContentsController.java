/*
 * Goslings - Git Objects Browser
 * https://github.com/kaitoy/goslings
 * MIT licensed
 *
 * Copyright (C) 2016 Kaito Yamada
 */

package com.github.kaitoy.goslings.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The controller for static web contents
 *
 * @author Kaito Yamada
 */
@Controller
@RequestMapping(method=RequestMethod.GET)
public final class StaticContentsController {

  /**
   * @return path to the index.html
   */
  @RequestMapping(path="/")
  public String root() {
      return "/index.html";
  }

//  /**
//   * @return path to the error.html
//   */
//  @RequestMapping(path="/error")
//  public String error() {
//      return "/error.html";
//  }

}
