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
 * ���{���O�g���n�D���A�N���w���|���ɮױq���a���|�ƻs����w��ftp
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
        PropertyConfigurator.configure("log4j.properties");  // log�]�w
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
        //�R���ɮ�
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
                    logger.debug("�ɮ׸��|: " +file1.getAbsolutePath()+ "  " +lastModified );
                    cnt++;
                }
            }

        }
        logger.info("�R�����ɮ׼�: " +cnt);
       
    

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
        // �ؿ��{�b�M�ŤF�i�H�R��
        return dir.delete();
    }

    /**
     *
     * ��X�ŦX�����ɮ�
     * @return
     */
    private Vector FileSearch()
    {

        File f = new File(path);
        if (f.isDirectory() == false)
        {
            
            logger.error("�ɮ׸��|(file.path)���O�@�ӥؿ��A�Эץ� ");
            return null;
        }

        Vector vecFile = new Vector();

        // �ˬd���
        if (file_exist_month_enable.equals("true"))
        {
            File[] files = f.listFiles();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
            int keepMonth = Integer.parseInt("-" + this.file_exist_month); // �O�d���
            Calendar keepModifyDate = Calendar.getInstance();
            keepModifyDate.add(Calendar.MONTH, keepMonth);
            logger.info("���w�����|: " + this.path );
            logger.info("�O�d" + file_exist_month + "�Ӥ�A�Y " + ft.format(keepModifyDate.getTime()) + "�H�e���ɮ׳��R��");
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
             int keepDay = Integer.parseInt("-" + this.file_exist_day); // �O�d�Ѽ�
             Calendar keepModifyDate = Calendar.getInstance();
             keepModifyDate.add(Calendar.DATE, keepDay);
             logger.info("���w�����|: " + this.path );
             logger.info("�O�d " + file_exist_day + " �Ѫ���ơA�Y " + ft.format(keepModifyDate.getTime()) + "�H�e���ɮ׳��R��");
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
             

        // �ˬd�ɦW����r
        if (this.file_key_enable.equals("true"))
        {
            Vector tmpVecFile = new Vector();
            for (int i = 0; i < vecFile.size(); i++)
            {
                File f1 = (File) vecFile.get(i);
                String fileName = f1.getName();
                if (fileName.indexOf(this.key) != -1) // �ɮצW�ŦXkey��
                {
                    tmpVecFile.add(f1);
                }
            }
            vecFile = tmpVecFile;

        }
        logger.info("�ŦX�����ɮ׼�: " +vecFile.size() );
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
                logger.error("file.exist.month.enable ��file.exist.day.enable ���i�P�ɬ�true");
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
