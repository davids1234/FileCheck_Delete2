package mySelf;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author david_chen
 *  ���{���O�ΨӨ��Njava.util.Properties���\��A�ѨM����ýX���\��C�s�WgetEncdoeProperty�禡�C
 *  ��J�]�w�ɽs�X�A�Y�i���o���T������r�C
 */
public class PropertiesUtil extends Properties
{
    
    
    /**
     * @param key
     * @param encoding : �п�J�ӳ]�w�ɪ��s�X �A�Y��Jnull�A��ܨϥΨt�ιw�]�s�X�C�@��O��J"big5" �� "utf8"
     * @return
     * @throws UnsupportedEncodingException
     */
    public  String getEncdoeProperty(String key, String encoding) throws UnsupportedEncodingException
    {
        //
        
      
        String value = this.getProperty(key);
        if (value == null)
            return null;
       
       if(encoding == null )
           encoding = System.getProperty("file.encoding");
       
       encoding = encoding.trim();
       if("".equalsIgnoreCase(encoding))
           encoding = System.getProperty("file.encoding");
       
   
       
       
       value = new String(value.getBytes("ISO8859-1"), encoding);
        
        
        return value;
  
    }
    
    
    
}
