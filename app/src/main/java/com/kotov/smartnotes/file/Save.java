package com.kotov.smartnotes.file;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Save {

    public static File saveFile(String title, String desc) {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/") + "SmartNotes");
        if (!path.exists()) {
            if (path.mkdir()) {

            }
        }
        File file = new File(path, title + ".txt");

        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(String.format("%s\n%s", title, desc).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
