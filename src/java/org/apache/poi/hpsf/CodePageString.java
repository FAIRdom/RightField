/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;

@Internal
class CodePageString
{
    private final static POILogger logger = POILogFactory
            .getLogger( CodePageString.class );

    private byte[] _value;

    CodePageString( final byte[] data, final int startOffset )
    {
        int offset = startOffset;

        int size = LittleEndian.getInt( data, offset );
        offset += LittleEndian.INT_SIZE;

        _value = LittleEndian.getByteArray( data, offset, size );
        if ( size != 0 && _value[size - 1] != 0 ) {
            // TODO Some files, such as TestVisioWithCodepage.vsd, are currently
            //  triggering this for values that don't look like codepages
            // See Bug #52258 for details
            logger.log(POILogger.WARN, "CodePageString started at offset #" + offset
                        + " is not NULL-terminated" );
//            throw new IllegalPropertySetDataException(
//                    "CodePageString started at offset #" + offset
//                            + " is not NULL-terminated" );
        }
    }

    CodePageString( String string, int codepage )
            throws UnsupportedEncodingException
    {
        setJavaValue( string, codepage );
    }

    String getJavaValue( int codepage ) throws UnsupportedEncodingException
    {
        String result;
        if ( codepage == -1 )
            result = new String( _value, StringUtil.UTF8 );
        else
            result = CodePageUtil.getStringFromCodePage(_value, codepage);
        final int terminator = result.indexOf( '\0' );
        if ( terminator == -1 )
        {
            logger.log(
                    POILogger.WARN,
                    "String terminator (\\0) for CodePageString property value not found."
                            + "Continue without trimming and hope for the best." );
            return result;
        }
        if ( terminator != result.length() - 1 )
        {
            logger.log(
                    POILogger.WARN,
                    "String terminator (\\0) for CodePageString property value occured before the end of string. "
                            + "Trimming and hope for the best." );
        }
        return result.substring( 0, terminator );
    }

    int getSize()
    {
        return LittleEndian.INT_SIZE + _value.length;
    }

    void setJavaValue( String string, int codepage )
            throws UnsupportedEncodingException
    {
        String stringNT = string + "\0";
        if ( codepage == -1 )
            _value = stringNT.getBytes(StringUtil.UTF8);
        else
            _value = CodePageUtil.getBytesInCodePage(stringNT, codepage);
    }

    int write( OutputStream out ) throws IOException
    {
        LittleEndian.putInt( _value.length, out );
        out.write( _value );
        return LittleEndian.INT_SIZE + _value.length;
    }
}
