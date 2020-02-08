/*
 * Copyright 2001 Draagon Software LLC. All Rights Reserved.
 *
 * This software is the proprietary information of Draagon Software LLC.
 * Use is subject to license terms.
 */

package com.draagon.util;


public class Param
{
  private String mName = null;
  private String mValue = null;

  public Param( String name, String value )
  {
    setName( name );
    setValue( value );
  }

  public void setName( String name ) { mName = name; }
  public String getName() { return mName; }

  public void setValue( String value ) { mValue = value; }
  public String getValue() { return mValue; }
}

