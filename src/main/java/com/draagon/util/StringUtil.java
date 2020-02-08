package com.draagon.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

public class StringUtil {

    public static String htmlEscape(String s) {
        
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\'':
                    sb.append("&#39");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String htmlEscape(Object o) {
        return htmlEscape(o.toString());
    }

    public static String insertString(String s, String stringToLookFor, String stringToInsert, boolean caseSensitive) {
        int tmpInt = -1;
        if (caseSensitive) {
            tmpInt = s.indexOf(stringToLookFor);
        } else {
            tmpInt = s.toLowerCase().indexOf(stringToLookFor.toLowerCase());
        }
        if (tmpInt > -1) {
            StringBuilder sb1 = new StringBuilder(s);
            sb1.insert(tmpInt + stringToLookFor.length(), stringToInsert);
            return sb1.toString();
        } else {
            return s;
        }
    }

    public static String swapString(String s, String oldString, String newString) {
        String[] tmpArray1 = {oldString};
        String[] tmpArray2 = {newString};
        return swapString(s, tmpArray1, tmpArray2);
    }

    public static String swapString(String s, String[] oldStrings, String[] newStrings) {
        return s;
    }

    public static String strTran(String str, String from, String to) {
        String strRetVal = null;
        String tmp = null;
        String tmp2 = null;
        int cat = -1;
        cat = str.indexOf(from);
        while (cat != -1) {
            tmp = str.substring(0, cat);
            tmp2 = str.substring(cat + from.length());
            str = tmp + to + tmp2;
            cat = str.indexOf(from, cat + to.length());
        }
        strRetVal = str;
        return strRetVal;
    }

    /*public static String sqlEscape(String s) {
        StringBuilder sb = new StringBuilder(s);
        s = sb.toString();
        return s;

    }*/

    /**
     * Returns a formatted string for the array. ie; [item1,item2,itemN]
     */
    public static String makeString(Object[] array) {
        StringBuilder buffer = new StringBuilder("[");
        if (array == null) {
            buffer.append("null");
        } else {
            for (int i = 0; i < array.length; i++) {
                buffer.append(array[i]);
                if (i != (array.length - 1)) {
                    buffer.append(",");
                }
            }
        }
        buffer.append("]");
        return buffer.toString();
    }

    /**
     * Returns an array parsed from inStr by the specified delimiter.
     *
     * @param inStr - The string to be parsed.
     * @param delimeter - The delimeter to use for parsing.
     */
    public static String[] makeArray(String inStr, String delimeter) {
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(inStr, delimeter);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(token);
        }
        String[] result = new String[list.size()];
        Iterator ci = list.iterator();
        for (int i = 0; i < result.length && ci.hasNext(); i++) {
            result[i] = (String) ci.next();
        }
        return result;
    }

    /**
     * Returns a formatted string for the iterator. ie; [item1,item2,itemN]
     */
    public static String makeString(Iterator iter) {
        StringBuilder buffer = new StringBuilder("[");
        if (iter == null) {
            buffer.append("null");
        } else {
            while (iter.hasNext()) {
                buffer.append(iter.next());
                if (iter.hasNext()) {
                    buffer.append(",");
                }
            }
        }
        buffer.append("]");
        return buffer.toString();
    }

    /**
     * Returns a formatted string for the Enumeration. ie; [item1,item2,itemN]
     */
    public static String makeString(Enumeration enumeration) {
        StringBuilder buffer = new StringBuilder("[");
        if (enumeration == null) {
            buffer.append("null");
        } else {
            while (enumeration.hasMoreElements()) {
                buffer.append(enumeration.nextElement());
                if (enumeration.hasMoreElements()) {
                    buffer.append(",");
                }
            }
        }
        buffer.append("]");
        return buffer.toString();
    }

    /**
     * Returns an array of strings of the specified size.
     *
     * @param size - length of the string to be put in each item of the array.
     */
    public static String[] breakString(String str, int size) {
        int len = str.length();
        String[] result;

        if (size > len) {
            result = new String[1];
            result[0] = str;
            return result;
        }
        int arLen = len / size;
        // Add one to accomodate remaining characters in remainder.
        if (len % size != 0) {
            arLen++;
        }
        result = new String[arLen];
        int beg = 0;
        int end = size;
        for (int i = 0; i < arLen; i++) {
            if (end < len) {
                result[i] = str.substring(beg, end);
            } else {
                result[i] = str.substring(beg);
            }
            beg = end;
            end += size;
        }
        return result;
    }

    public static int indexOf(StringBuilder buffer, String subStr) {
        return indexOf(buffer, subStr, null);
    }

    /**
     * Does the same as String.indexOf. This is needed to index a changing
     * StringBuilder.
     */
    public static int indexOf(StringBuilder buffer, String subStr, String termChars) {
        boolean foundString = false;
        for (int i = 0; i < buffer.length(); i++) {
            for (int j = 0; j < subStr.length(); j++) {
//                System.out.println((i+j)+") "+buffer.charAt(i+j)+" "+j+") "+subStr.charAt(j));
                if (buffer.charAt(i + j) == subStr.charAt(j)) {
                    foundString = true;
                } else {
                    foundString = false;
                    break;
                }
            }
            if (foundString && termChars == null) {
//             System.out.println("Found="+i+" len="+subStr.length());
                return i;
            } else if (foundString
                    && termChars.indexOf(buffer.charAt(i + subStr.length())) >= 0) {
                return i;
            }
        }
        return -1;
    }
}
