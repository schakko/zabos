package de.ecw.zabos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility-Klasse für den Umgang mit Dateien
 * 
 * @author ckl
 */
public class FileUtils
{
    /**
     * Erstellt eine neue Datei
     * 
     * @param _filename
     */
    synchronized public static void touch(String _filename)
    {
        File file = new File(_filename);
        if (true)// if (f.canWrite())
        {
            try
            {
                FileOutputStream ofs = new FileOutputStream(file);
                try
                {
                    ofs.write('!');
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    ofs.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

}
