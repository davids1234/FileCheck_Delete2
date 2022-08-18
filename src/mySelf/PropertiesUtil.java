package mySelf;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author david_chen
 *  此程式是用來取代java.util.Properties的功能，解決中文亂碼的功能。新增getEncdoeProperty函式。
 *  輸入設定檔編碼，即可取得正確的中文字。
 */
public class PropertiesUtil extends Properties
{
    
    
    /**
     * @param key
     * @param encoding : 請輸入該設定檔的編碼 ，若輸入null，表示使用系統預設編碼。一般是輸入"big5" 或 "utf8"
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
