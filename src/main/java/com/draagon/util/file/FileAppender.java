package com.draagon.util.file;

/**
 * Title:        File Appending Utility
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      Draagon Software LLC
 * @author Doug Mealing
 * @version 1.0
 */

import java.io.*;

public class FileAppender
{
  public static void append( File f, String data )
    throws IOException
  {
    append( f, data.getBytes() );
  }

  public static void append( File file, byte [] b )
    throws IOException
  {
    RandomAccessFile raf = null;
    FileOutputStream fos = null;

    long length = file.length();

    if ( length > 0 )
    {
      raf = new RandomAccessFile( file, "rw" );
      raf.seek( length );
      fos = new FileOutputStream( raf.getFD() );
    }
    else
      fos = new FileOutputStream( file );

    fos.write( b );

    fos.close();

    if (raf != null) raf.close();
  }
}
