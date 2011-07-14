/*
 * Copyright (c) 2011 weborganic systems pty. ltd.
 */
package com.weborganic.bastille.security;

/**
 * An enumeration for the results of authentication methods such as login and logout. 
 * 
 * @author Christophe Lauret
 * @version 0.6.2 - 8 April 2011
 * @since 0.6.2
 */
public enum AuthenticationResult {

  /**
   * The login succeeded and resulted in the user being logged in.
   */
  LOGGED_IN,

  /**
   * The user was already logged in.
   */
  ALREADY_LOGGED_IN,

  /**
   * The login could not proceed because the user provided insufficient details.
   */
  INSUFFICIENT_DETAILS,

  /**
   * The login failed because the user provided incorrect details.
   */
  INCORRECT_DETAILS,

  /**
   * The user has been logged out.
   */
  LOGGED_OUT,

  /**
   * The user was already logged out.
   */
  ALREADY_LOGGED_OUT,

}