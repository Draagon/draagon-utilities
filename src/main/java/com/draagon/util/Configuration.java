/*
 * Copyright Draagon Software, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of Draagon Software, LLC.
 * Use is subject to license terms.
 */

package com.draagon.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class Configuration extends Properties implements InitializingBean
{
  private static final long serialVersionUID = 4319443307567558525L;

  private static Log log = LogFactory.getLog(Configuration.class);

  private Properties overrides = null;
  private boolean ignoreResourceNotFound = false;
  private Resource resource = null;

  public Configuration()
  {
  }

  public Configuration( Properties p )
  {
    super();

    setProperties( p );
  }

  public void afterPropertiesSet() throws IOException {

	  if ( resource != null ) {

	      // If it doesn't exist and we can ignore this error, then return
		  if ( !resource.exists() && isIgnoreResourceNotFound() ) {
			  return;
		  }

		  // Get the input stream for this

		  InputStream is = null;
		  try {
			  is = resource.getInputStream();
			  Properties p = new Properties();
			  p.load( is );

			  // Set the properties that were just loaded
			  setProperties( p );

		  }
		  catch( IOException e ) {
			  if ( e instanceof FileNotFoundException && isIgnoreResourceNotFound() ) {
				  // Do nothing as we can ignore this error
			  } else {
				  throw e;
			  }
		  }
		  finally {
			  if ( is != null ) is.close();
		  }
	  }
  }

  /** Directly sets Properties onto the Configuration object */
  public void setProperties( Properties p ) {
    for( Enumeration<Object> e = p.keys(); e.hasMoreElements(); )
    {
      Object key = e.nextElement();
      Object val = p.get( key );

      put( key, val );
    }
  }

  /** Uses a Spring Resource to load the properties information */
  public void setResource( Resource resource ) throws IOException {
	  this.resource = resource;
  }

  /** Specifies the override Properties object */
  public void setOverrides( Properties overrides ) throws IOException {

	  this.overrides = overrides;
  }

  public Properties getOverrides() {
	  return overrides;
  }

  public String getProperty( String name )
  {
    return (String) get( name );
  }

  public String getProperty( String name, String def )
  {
    String tmp = (String) get( name );
    if ( tmp == null ) return def;
    return tmp;
  }
  /**
   * Return a subset of properties that contain the qualifier in the first
   * portion of their name.
   *
   * @param qualifier - First part of property name; ie, commerce.driver.storm
   *
   * @return Returns a Properties object that starts with qualifier. The
   *          qualifier is stripped from the returned property names.
   */
  public Properties getSubproperties( String qualifier )
  {
    Properties result = new Properties();
    for( Enumeration<Object> e = keys(); e.hasMoreElements(); )
    {
      String key = (String) e.nextElement();
      String val = (String) get( key );

      if ( qualifier.length() < key.length() && key.startsWith( qualifier ))
      {
        String newKey = key.substring( qualifier.length() );
        if ( result.get( newKey ) != null) continue;

        result.put( key.substring( qualifier.length() + 1 ), val );
      }
    }

    return result;
  }

  public Object get( Object name )
  {
	String r = (String) get( (String) name, 0 );
    // System.out.println( "[" + name + "]->[" + r + "]" );
    return r;
  }

  protected Object get( String name, int iter )
  {
    // Make sure we don't get caught in an endless loop
    iter++;
    if ( iter > 10 ) return "<LOOP>";

    // First check the overrides file
    if ( overrides != null ) {
        String s = overrides.getProperty( name );
        if ( s != null ) return s;
    }

    // Retrieve the value from the Properties table
    String tmp = getValue( name );
    if ( tmp == null ) return null;
    // System.out.println( "tmp: " + tmp );

    // Parse all dynamic values
    int i = 0;
    while ( ( i = tmp.indexOf( "${", i )) >= 0 )
    {
      // Check to see if at the end of the String
      if ( i + 2 >= tmp.length() ) break;

      int j = tmp.indexOf( "}", i + 2 );
      if ( j > 0 )
      {
        // Extract the inner string
        String x = tmp.substring( i + 2, j );
        // System.out.println( "x: " + x );

        // Retrieve the value of the dynamic string
        String y = (String) get( x, iter );
        if ( y == null ) y = "";
        // System.out.println( "y: " + y );

        tmp = tmp.substring( 0, i ) + y + tmp.substring( j + 1 );
        // System.out.println( "*tmp: " + tmp );
      }
      else {
        i = i + 2;
      }
    }
    
    return tmp;
  }

  /**
   * Get the value for the specified name
   */
  protected String getValue( String name )
  {
    if ( name.startsWith( "ENV." ))
    {
      name = name.substring( 4 );
      return System.getProperty( name );
    }
    else
      return (String) super.get( name );
  }

  public Enumeration elements()
  {
    return null;
  }

	public boolean isIgnoreResourceNotFound() {
		return ignoreResourceNotFound;
	}

	public void setIgnoreResourceNotFound( boolean ignoreResourceNotFound ) {
		this.ignoreResourceNotFound = ignoreResourceNotFound;
	}

/*  public static void main( String args[] )
  {
    Properties p = new Properties();
    p.put( "host.name", "lambic" );
    p.put( "host.port", "88" );
    p.put( "test1", "http://$(host.name):$(host.port)/" );
    p.put( "test2", "http://$(host.namexx):$(host.port)/" );
    p.put( "test3", "$(host.name).good" );
    p.put( "test4", "good.$(host.name)" );

    Configuration c = new Configuration( p );
    String tmp = null;
    int i = 1;
    while( ( tmp = (String) c.get( "test" + i++ )) != null )
      System.out.println( "--> " + tmp );
  }*/
}
