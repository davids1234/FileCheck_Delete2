import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.net.InetSocketAddress;

import mySelf.PropertiesUtil;

import org.apache.log4j.FileAppender;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 此程式是君偉要求的，將指定路徑的檔案從本地路徑複製到指定的ftp
 * 
 * @author David
 * 
 */
public class FileDelete
{

    private static PropertiesUtil prop = new PropertiesUtil();

    static Logger logger = Logger.getLogger(FileDelete.class);
    
    String path = null;

    String key = null;

    String file_exist_month_enable = null;
    String file_exist_day_enable = null;
    String file_exist_day = null;

    String file_exist_month = null;

    String file_key_enable = null;

    public static void main(String[] args)
    {
       
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        PropertyConfigurator.configure("log4j.properties");  // log設定
        logger.info("");
        logger.info("");
        logger.info("========Program Start===========");

        FileDelete fd = new FileDelete();
        if (fd.loadProperty() == false)
        {
            logger.info("========Program STOP===========");
           
            return;
        }
        
        Vector vecFile = fd.FileSearch();
        int cnt = 0;
        //刪除檔案
        if( vecFile != null)
        {
            for (int i = 0; i < vecFile.size(); i++)
            {
                File file1 = (File) vecFile.get(i);
                
                if(file1.getName().equalsIgnoreCase("System Volume Information"))
                    continue;
                String fileName =  file1.getName();
                Calendar cal = Calendar.getInstance();
                long a = file1.lastModified();
                cal.setTimeInMillis(a);
                String lastModified = sf.format( cal.getTime() );
                boolean isOK = deleteDir(file1);
                if(isOK == true)
                {
                    logger.debug("檔案路徑: " +file1.getAbsolutePath()+ "  " +lastModified );
                    cnt++;
                }
            }

        }
        logger.info("刪除的檔案數: " +cnt);
       
    

        logger.info("========Program End===========");
      

    }

    public static boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (!deleteDir(files[i]))
                    return false;
            }
        }
        // 目錄現在清空了可以刪除
        return dir.delete();
    }

    /**
     *
     * 找出符合條件的檔案
     * @return
     */
    private Vector FileSearch()
    {

        File f = new File(path);
        if (f.isDirectory() == false)
        {
            
            logger.error("檔案路徑(file.path)不是一個目錄，請修正 ");
            return null;
        }

        Vector vecFile = new Vector();

        // 檢查日期
        if (file_exist_month_enable.equals("true"))
        {
            File[] files = f.listFiles();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
            int keepMonth = Integer.parseInt("-" + this.file_exist_month); // 保留月份
            Calendar keepModifyDate = Calendar.getInstance();
            keepModifyDate.add(Calendar.MONTH, keepMonth);
            logger.info("指定的路徑: " + this.path );
            logger.info("保留" + file_exist_month + "個月，即 " + ft.format(keepModifyDate.getTime()) + "以前的檔案都刪除");
            for (int i = 0; i < files.length; i++)
            {
                String fileName = files[i].getName();
                long lastModify = files[i].lastModified();
                Calendar fileModifyDate = Calendar.getInstance();
                fileModifyDate.setTimeInMillis(lastModify);

                if (fileModifyDate.before(keepModifyDate))
                {
                    vecFile.add(files[i]);
                }

            }

         }else if(file_exist_day_enable.equals("true"))
         {
             File[] files = f.listFiles();
             SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
             int keepDay = Integer.parseInt("-" + this.file_exist_day); // 保留天數
             Calendar keepModifyDate = Calendar.getInstance();
             keepModifyDate.add(Calendar.DATE, keepDay);
             logger.info("指定的路徑: " + this.path );
             logger.info("保留 " + file_exist_day + " 天的資料，即 " + ft.format(keepModifyDate.getTime()) + "以前的檔案都刪除");
             for (int i = 0; i < files.length; i++)
             {
                 String fileName = files[i].getName();
                 long lastModify = files[i].lastModified();
                 Calendar fileModifyDate = Calendar.getInstance();
                 fileModifyDate.setTimeInMillis(lastModify);

                 if (fileModifyDate.before(keepModifyDate))
                 {
                     vecFile.add(files[i]);
                 }

             }
         }
             

        // 檢查檔名關鍵字
        if (this.file_key_enable.equals("true"))
        {
            Vector tmpVecFile = new Vector();
            for (int i = 0; i < vecFile.size(); i++)
            {
                File f1 = (File) vecFile.get(i);
                String fileName = f1.getName();
                if (fileName.indexOf(this.key) != -1) // 檔案名符合key值
                {
                    tmpVecFile.add(f1);
                }
            }
            vecFile = tmpVecFile;

        }
        logger.info("符合條件的檔案數: " +vecFile.size() );
        return vecFile;
    }

    public boolean loadProperty()
    {
        boolean result = true;

        try
        {
            FileInputStream fin = new FileInputStream("setting.prop");
            prop.load(fin);
            this.path = prop.getEncdoeProperty("file.Path","utf-8");
            
            this.key = prop.getEncdoeProperty("file.key" ,"utf-8");

            this.file_key_enable = prop.getEncdoeProperty("file.key.enable" ,"utf-8");
            this.file_exist_month = prop.getEncdoeProperty("file.exist.month" ,"utf-8");
            this.file_exist_month_enable = prop.getEncdoeProperty("file.exist.month.enable" ,"utf-8");
            this.file_exist_day_enable = prop.getEncdoeProperty("file.exist.day.enable" ,"utf-8");
            this.file_exist_day = prop.getEncdoeProperty("file.exist.day" ,"utf-8");

            if (path == null || key == null || file_key_enable == null || file_exist_month == null || file_exist_month_enable == null || file_exist_day_enable == null || file_exist_day == null)
            {
                logger.error("The data in the property File  is not Correct");
                return false;

            }
            
            this.path = this.path.trim();
            this.key = this.key.trim();
            this.file_key_enable = this.file_key_enable.trim();
            this.file_exist_month = this.file_exist_month.trim();
            this.file_exist_month_enable = this.file_exist_month_enable.trim();
            this.file_exist_day_enable = this.file_exist_day_enable.trim();
            this.file_exist_day = this.file_exist_day.trim();
            
            if(file_exist_month_enable.equals("true") && file_exist_day_enable.equals("true") )
            {
                logger.error("file.exist.month.enable 及file.exist.day.enable 不可同時為true");
                return false;
            }
           

        } catch (FileNotFoundException e)
        {
            result = false;
            logger.error("File not Found(copyReport.prop)");
            e.printStackTrace();
        } catch (IOException e)
        {
            result = false;
            logger.error("load data form Properties File Fail(copyReport.prop)");

            e.printStackTrace();
        }
        return result;
    }
}
