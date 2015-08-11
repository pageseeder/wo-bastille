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
package org.pageseeder.bastille.security.ps;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.pageseeder.bastille.pageseeder.PSUser;
import org.pageseeder.bastille.security.Constants;
import org.pageseeder.bastille.security.Obfuscator;
import org.pageseeder.bastille.security.User;
import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.berlioz.content.ContentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a PageSeeder User.
 *
 * @deprecated Use {@link PSUser} instead.
 *
 * @author Christophe Lauret
 *
 * @version 0.8.1 - 19 December 2012
 * @since 0.6.2
 */
@Deprecated
public final class PageSeederUser extends PSUser implements User, Serializable {

  /**
   *As per requirement for the {@link Serializable} interface.
   */
  private static final long serialVersionUID = 4229779545161938662L;

  /**
   * Create a new PageSeeder user.
   *
   * @param id The PageSeeder user ID.
   */
  public PageSeederUser(Long id) {
    super(id);
  }

  // Setters
  // ----------------------------------------------------------------------------------------------

  /**
   * @param username the PageSeeder username for this user.
   */
  @Override
  protected void setUsername(String username) {
    super.setUsername(username);
  }

  /**
   * @param jsessionid the PageSeeder username for this user.
   */
  @Override
  protected void setJSessionId(String jsessionid) {
    super.setJSessionId(jsessionid);
  }

  /**
   * @param firstname the PageSeeder username for this user.
   */
  @Override
  protected void setFirstname(String firstname) {
    super.setFirstname(firstname);
  }

  /**
   * @param surname the PageSeeder username for this user.
   */
  @Override
  protected void setSurname(String surname) {
    super.setSurname(surname);
  }

  /**
   * @param email the PageSeeder username for this user.
   */
  @Override
  protected void setEmail(String email) {
    super.setEmail(email);
  }

  /**
   * @param groups the PageSeeder groups the user is a member of.
   */
  @Override
  protected void setMemberOf(List<String> groups) {
    super.setMemberOf(groups);
  }

  /**
   * Returns the PageSeeder user that is currently logged in.
   *
   * @deprecated Use {@link org.pageseeder.bastille.pageseeder.PSUsers#getUser(ContentRequest)} instead
   *
   * @param req the content request.
   * @return The PageSeeder user or <code>null</code> if it is not configured properly or could not login.
   */
  @Deprecated
  public static PageSeederUser getUser(ContentRequest req) {
    HttpSession session = req.getSession();
    Object o = null;
    // Try to get the user from the session (if logged in)
    if (session != null) {
      o = session.getAttribute(Constants.SESSION_USER_ATTRIBUTE);
    }
    // try to cast from PageSeeder user
    if (o instanceof PageSeederUser) return (PageSeederUser)o;
    else return null;
  }

  /**
   * Returns the user from the property stored in the global settings.
   *
   * <p>This class will log the user to PageSeeder to retrieve his info.
   *
   * @deprecated Use {@link org.pageseeder.bastille.pageseeder.PSUsers#get(String)} instead.
   *
   * @param property The property of the PageSeeder user.
   *
   * @return The user or <code>null</code> if it is not configured properly or could not login.
   *
   * @throws IOException Should an error occur while attempting login
   */
  @Deprecated
  public static PageSeederUser get(String property) throws IOException {
    String username = GlobalSettings.get(property+".username");
    String password = GlobalSettings.get(property+".password");
    if (password.startsWith("OB1:")) {
      password = Obfuscator.clear(password.substring(4));
    } else {
      Logger logger = LoggerFactory.getLogger(PSUser.class);
      logger.warn("Config property \""+property+".password\" left in clear - consider obfuscating.");
    }
    PageSeederUser user = PageSeederAuthenticator.login(username, password);
    return user;
  }

}
