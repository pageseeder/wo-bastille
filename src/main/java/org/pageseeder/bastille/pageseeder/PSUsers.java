/*
 * Copyright 2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.bastille.pageseeder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.pageseeder.bastille.security.Constants;
import org.pageseeder.bastille.security.Obfuscator;
import org.pageseeder.bastille.security.ps.PageSeederAuthenticator;
import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.berlioz.content.ContentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for PageSeeder Users.
 *
 * @author Christophe Lauret
 *
 * @version 0.8.9 - 7 March 2013
 * @since 0.8.1
 */
public final class PSUsers {

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PSUsers.class);

  /**
   * One minute in millis seconds.
   */
  private static final long ONE_MINUTE_IN_MS = 60000;

  /**
   * Caches the PageSeeder users.
   */
  private static final Map<String, PSUser> CACHE = new ConcurrentHashMap<String, PSUser>();

  /**
   * A JSession ID to recycle when not using a PageSeeder User.
   */
  private static volatile PSSession anonymous = null;

  /**
   * Utility class.
   */
  private PSUsers() {
  }

  /**
   * Returns the PageSeeder user that is currently logged in.
   *
   * @param req the content request.
   * @return The PageSeeder user or <code>null</code> if it is not configured properly or could not login.
   */
  public static PSUser getUser(ContentRequest req) {
    HttpSession session = req.getSession();
    Object o = null;
    // Try to get the user from the session (if logged in)
    if (session != null) {
      o = session.getAttribute(Constants.SESSION_USER_ATTRIBUTE);
    }
    // try to cast from PageSeeder user
    if (o instanceof PSUser) return (PSUser)o;
    else return null;
  }

  /**
   * Returns the user from the property stored in the global settings.
   *
   * <p>This class will log the user to PageSeeder to retrieve his info.
   *
   * <p>If the password is using some
   *
   * @param property The property of the PageSeeder user.
   *
   * @return The user or <code>null</code> if it is not configured properly or could not login.
   *
   * @throws IOException Should an error occur while attempting login
   */
  public static PSUser get(String property) throws IOException {
    String username = GlobalSettings.get(property+".username");
    String password = GlobalSettings.get(property+".password");

    // We must have both a username and a password in order to login
    if (username == null) {
      LOGGER.warn("Unable to return user - config property '{}.username' is null.", property);
      return null;
    }

    // Try the cache
    PSUser user = CACHE.get(property);

    // Ensure that the PageSeeder session is still valid and can be used
    if (user == null || !hasValidSession(user) || !user.getUsername().equals(username)) {
      if (password.startsWith("OB1:")) {
        password = Obfuscator.clear(password.substring(4));
      } else {
        LOGGER.warn("Config property '{}.password' left in clear - consider obfuscating.", property);
      }
      user = PageSeederAuthenticator.login(username, password);
      if (user != null) {
        CACHE.put(property, user);
      } else {
        LOGGER.warn("User config property '{}' not setup property - results in null user", property);
      }
    }
    return user;
  }

  /**
   * Indicates whether the session is still valid for the specified session.
   *
   * @param user The PageSeeder session to check.
   *
   * @return <code>true</code> if the session is still valid;
   *         <code>false</code> otherwise.
   */
  public static boolean hasValidSession(PSUser user) {
    if (user == null) return false;
    return isValid(user.getSession());
  }

  /**
   * Indicates whether the session is still valid for the specified session.
   *
   * @param session The PageSeeder session to check.
   *
   * @return <code>true</code> if the session is still valid;
   *         <code>false</code> otherwise.
   */
  public static boolean isValid(PSSession session) {
    if (session == null) return false;
    int minutes = GlobalSettings.get("pageseeder.session.timeout", 60);
    long maxSessionAge = minutes * ONE_MINUTE_IN_MS;
    return session.age() < maxSessionAge;
  }

  /**
   * @return the anonymous
   */
  public static PSSession getAnonymous() {
    return anonymous;
  }

  /**
   * Setting a reusable session for anonymous users.
   *
   * @param session a session to use for anonymous connections.
   */
  public static void setAnonymous(PSSession session) {
    PSUsers.anonymous = session;
  }
}
