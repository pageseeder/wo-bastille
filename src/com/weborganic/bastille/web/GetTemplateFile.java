/*
 * Copyright (c) 2011 weborganic systems pty. ltd.
 */
package com.weborganic.bastille.web;

import java.io.File;
import java.io.IOException;

import org.weborganic.berlioz.BerliozException;
import org.weborganic.berlioz.content.Cacheable;
import org.weborganic.berlioz.content.ContentGenerator;
import org.weborganic.berlioz.content.ContentRequest;

import com.topologi.diffx.xml.XMLWriter;

/**
 * This generator returns the XML for a template file.
 * 
 * <p>It is designed to be used as a base class, it cannot be instantiated without arguments.
 * 
 * @author Christophe Lauret
 * @version 0.6.0 - 31 May 2010
 * @since 0.6.0
 */
public abstract class GetTemplateFile implements ContentGenerator, Cacheable {

  /**
   * The template name (eg. "header", "footer", etc...) 
   */
  private final String _name;

  /**
   * Creates a new template file generator for the given template name.
   * 
   * @param name the template name (eg. "header", "footer", etc...) 
   */
  public GetTemplateFile(String name) {
    this._name = name;
  }

  /**
   * {@inheritDoc}
   */
  public final String getETag(ContentRequest req) {
    boolean reload = "true".equals(req.getParameter("reload-conf-properties"));
    return TemplateFile.getETag(this._name, reload);
  }

  /**
   * {@inheritDoc}
   */
  public final void process(ContentRequest req, XMLWriter xml) throws BerliozException, IOException {
    boolean reload = "true".equals(req.getParameter("reload-conf-properties"));
    File f = TemplateFile.getFile(this._name, reload);
    TemplateFile.write(xml, f);
  }
}
