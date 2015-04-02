package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Bin on 2015/3/3.
 * 简单的写文件操作，适用于将小数据量（如url）写到文件上
 */
public class FileIO {
    public static void write(File fileToWrite, String message) {
        File output = fileToWrite;
        FileOutputStream out = null;
        String newline = System.getProperty("line.separator");
        if(!output.exists()) {
            try {
                output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out = new FileOutputStream(output, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.write(message.getBytes());
            out.write(newline.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
