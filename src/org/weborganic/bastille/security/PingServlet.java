/*
 * This file is part of the Bastille library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.bastille.security;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.weborganic.bastille.util.Resources;


/**
 * A Servlet to allow another ping servlet to ping it.
 *
 * @author Christophe Lauret
 * @version 0.8.11 - 8 March 2013
 * @since 9 January 2011
 */
public final class PingServlet extends HttpServlet {

  /**
   * As per requirement for the <code>Serializable</code> interface.
   */
  private static final long serialVersionUID = -3343755604269705856L;

  /**
   * URL of resource of the ping.
   */
  private static final String RESOURCE_URL = "/org/weborganic/bastille/security/resource/tick.png";

  /**
   * If the content type is specified.
   */
  private String _contentType = null;

  /**
   * The corresponding data.
   */
  private byte[] _data = null;

  @Override
  public void init(ServletConfig config) throws ServletException {
    String content = config.getInitParameter("content-type");
    if ("image/png".equals(content)) {
      this._contentType = content;
      this._data = Resources.getResource(RESOURCE_URL);
      if (this._data == null)
        LoggerFactory.getLogger(PingServlet.class).warn("Unable to get resource "+RESOURCE_URL);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    // If the data is defined and found
    if (this._data != null) {

      // Set the headers
      res.setContentType(this._contentType);
      res.setContentLength(this._data.length);

      // Copy the data
      ServletOutputStream out = res.getOutputStream();
      out.write(this._data);
      out.close();

    } else {
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);
      res.setContentLength(0);
    }

  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    doGet(req, res);
  }

}
