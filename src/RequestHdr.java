/*
 * RequestHdr.java
 */

import java.io.InputStream;
import java.io.DataInputStream;
import java.util.StringTokenizer;

public class RequestHdr 
{
   
	// HTTP method (eg. GET, POST)
	public String method = new String();
	// URL
	public String url = new String();
	// HTTP version (eg. 1.0 or 1.1)
	public String version = new String();
    // Host name of the client
    public String hostname = new String();
	// User agent (browser name)
	public String userAgent = new String();
	// Requesting document
	public String referer = new String();
	// Date of the modification of the file
    public String ifModifiedSince = new String();
	// MIME types client can accept
	public String accept = new String();
	// Client's authorization
    public String authorization = new String();
	// Content type
	public String contentType = new String();
	// Content length
    public int contentLength = -1;
	// Unrecognized
    public String unrecognized = new String();
	// Carriage return
    static String CR ="\r\n";

// Parses an HTTP request from the browser
	public boolean parse(InputStream In)
    {
       String CR ="\r\n";

       DataInputStream lines;
       StringTokenizer tz;
       try 
	   {
			lines = new DataInputStream(In);
			String le = lines.readLine();
			tz = new StringTokenizer(le);
       }
	   catch (Exception e) 
	   {
           return false;
       }

       // HTTP method line
       method = getToken(tz).toUpperCase();
       url    = getToken(tz);
       version= getToken(tz);
       
       while (true) 
	   {
           try 
		   {
               tz = new StringTokenizer(lines.readLine());
           } 
		   catch (Exception e) 
		   {
               return false;
           }
           String Token = getToken(tz); 
           // Termination
           if (0 == Token.length())
               break;
           
           if (Token.equalsIgnoreCase("USER-AGENT:"))
           {
               // user agent
               userAgent = getRemainder(tz);           
           } 
           else if (Token.equalsIgnoreCase("HOST:"))
           {
           		// http 1.1 requirement
           		// host name of the client
           		hostname = getRemainder(tz);
           }
           else if (Token.equalsIgnoreCase("ACCEPT:")) 
           {
               // accept MIME types (eg. image/jpeg)
               accept += " " + getRemainder(tz);
           } 
           else if (Token.equalsIgnoreCase("REFERER:")) 
           {
               // referer URL
               referer = getRemainder(tz);
           }
           else if (Token.equalsIgnoreCase("AUTHORIZATION:")) 
           { 
               // authenticatation
               authorization=  getRemainder(tz);
           } 
           else if (Token.equalsIgnoreCase("IF-MODIFIED-SINCE:")) 
           {
                // if modified since tag
				String str = getRemainder(tz);
                int index = str.indexOf(";");
                if (index == -1) 
                {
					ifModifiedSince  =str;
               	} 
               	else 
               	{
                   ifModifiedSince  =str.substring(0,index);
                   index = str.indexOf("=");
                   if (index != -1) 
                   {
						str = str.substring(index+1);
                   }
              	}
           } 
           else if (Token.equalsIgnoreCase("CONTENT-LENGTH:")) 
           {
           		// content length
				Token = getToken(tz);
				contentLength =Integer.parseInt(Token);
           } 
           else if (Token.equalsIgnoreCase("CONTENT-TYPE:")) 
           {
           		// content type (eg. text/html)
				contentType = getRemainder(tz);
           } 
           else 
           {   // for everything else (not standard and supported yet)
               unrecognized += Token + " " + getRemainder(tz) + CR;
           }
       }
       return true;
   }
           
	// Rebuilds header
   	public String toString(boolean sendUnknown) 
   	{
       String Request; 

       if (0 == method.length())
            method = "GET";

       Request = method +" "+ url + " HTTP/1.1" + CR;
	   if (0 < hostname.length())
	   		Request +="Host: " + hostname + CR;

       if (0 < userAgent.length())
           Request +="User-Agent: " + userAgent + CR;

       if (0 < referer.length())
           Request+= "Referer:"+ referer  + CR;

       if (0 < ifModifiedSince.length())
           Request+= "If-Modified-Since: " + ifModifiedSince + CR;
           
       if (0 < accept.length())
           Request += "Accept: " + accept + CR;
       else 
           Request += "Accept: */"+"* \r\n";
    
       if (0 < contentType.length())
           Request += "Content-Type: " + contentType   + CR;

       if (0 < contentLength)
           Request += "Content-Length: " + contentLength + CR;

       if (0 != authorization.length())
           Request += "Authorization: " + authorization + CR;

       if (sendUnknown) {
           if (0 != unrecognized.length())
               Request += unrecognized;
       }   

       Request += CR;
       
       return Request;
	}
	// Returns next token
   String  getToken(StringTokenizer tk)
   {
       String str ="";
       if  (tk.hasMoreTokens())
           str =tk.nextToken();
       return str; 
   }
   
	// Returns remainder of the string
   String  getRemainder(StringTokenizer tk){
       String str ="";
       if  (tk.hasMoreTokens())
           str =tk.nextToken();
       while (tk.hasMoreTokens()){
           str +=" " + tk.nextToken();
       }
       return str;
   }

} 
