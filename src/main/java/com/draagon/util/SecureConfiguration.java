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

public class SecureConfiguration extends Configuration
{
  private static final long serialVersionUID = 4319443307567558525L;

  private static Log log = LogFactory.getLog(SecureConfiguration.class);

  private Properties overrides = null;
  private boolean ignoreResourceNotFound = false;
  private Resource resource = null;

  public SecureConfiguration()
  {
  }

  public SecureConfiguration( Properties p )
  {
    super();
    setProperties( p );
  }

  @Override
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

    //decrypt all encrypted values
    while ( ( i = tmp.indexOf( "$enc{", i )) >= 0 )
    {
      // Check to see if at the end of the String
      if ( i + 5 >= tmp.length() ) break;

      int j = tmp.indexOf( "}", i + 5 );
      if ( j > 0 )
      {
        // Extract the inner string
        String x = tmp.substring( i + 5, j );
        // System.out.println( "x: " + x );

        // decode the string
        try
        {
        String y = EncryptionUtil.decrypt (x) ;
        if ( y == null ) y = "";
        // System.out.println( "y: " + y );

        tmp = tmp.substring( 0, i ) + y + tmp.substring( j + 1 );
        // System.out.println( "*tmp: " + tmp );
        }
        catch (Exception exception)
        {
        log.error ("exception occurred during decrypt: ", exception) ;
        }
      }
      else {
        i = i + 5;
      }
    }
    return tmp;
  }
}
