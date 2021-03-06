package com.akfu.common.util;

import java.util.StringTokenizer;

public class StringsUtil {
	/**
     * Convert a TCP/IP address string into a byte array
     * 
     * @param addr String
     * @return byte[]
     */
    public final static byte[] asBytes(String addr) {
      
      // Convert the TCP/IP address string to an integer value
      
      int ipInt = parseNumericAddress(addr);
      if ( ipInt == 0)
        return null;
      
      // Convert to bytes
      
      byte[] ipByts = new byte[4];
      
      ipByts[3] = (byte) (ipInt & 0xFF);
      ipByts[2] = (byte) ((ipInt >> 8) & 0xFF);
      ipByts[1] = (byte) ((ipInt >> 16) & 0xFF);
      ipByts[0] = (byte) ((ipInt >> 24) & 0xFF);
      
      // Return the TCP/IP bytes
      
      return ipByts;
    }
  /**
   * Check if the specified address is a valid numeric TCP/IP address and return as an integer value
   * 
   * @param ipaddr String
   * @return int
   */
  public final static int parseNumericAddress(String ipaddr) {
  
    //  Check if the string is valid
    
    if ( ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15)
      return 0;
      
    //  Check the address string, should be n.n.n.n format
    
    StringTokenizer token = new StringTokenizer(ipaddr,".");
    if ( token.countTokens() != 4)
      return 0;

    int ipInt = 0;
    
    while ( token.hasMoreTokens()) {
      
      //  Get the current token and convert to an integer value
      
      String ipNum = token.nextToken();
      
      try {
        
        //  Validate the current address part
        
        int ipVal = Integer.valueOf(ipNum).intValue();
        if ( ipVal < 0 || ipVal > 255)
          return 0;
          
        //  Add to the integer address
        
        ipInt = (ipInt << 8) + ipVal;
      }
      catch (NumberFormatException ex) {
        return 0;
      }
    }
    
    //  Return the integer address
    
    return ipInt;
  }
}
