/*
 * Copyright 2003 Draagon Software LLC. All Rights Reserved.
 *
 * This software is the proprietary information of Draagon Software LLC.
 * Use is subject to license terms.
 */
package com.draagon.util.web;

import java.util.Map;

public class URLConstructor
{
  public static String constructURL( String url, Map params )
  {
    StringBuilder out = new StringBuilder();

    // Extract the possible fields for insertion
    int i = 0;
    while( true )
    {
      int j = url.indexOf( "${", i );
      if ( j >= 0 )
      {
        out.append( url.substring( i, j ));

        int k = url.indexOf( "}", j );
        if ( k >= 0 )
        {
          // Increment to the next search segment
          i = k + 1;

          // Get the MetaField
          String name = url.substring( j + 2, k );
          String value = (String) params.get( name );

          if ( value == null ) value = "";

          out.append( value );

          continue;
        }
      }

      out.append( url.substring( i ));
      break;
    }

    return out.toString();
  }
}
