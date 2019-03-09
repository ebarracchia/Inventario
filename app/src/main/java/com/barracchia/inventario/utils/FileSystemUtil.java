package com.barracchia.inventario.utils;

import android.os.Environment;

import com.barracchia.inventario.MyApplication;
import com.barracchia.inventario.R;

import java.io.File;

public class FileSystemUtil {
    public static File getFilePath() {
        return getFilePath(MyApplication.getContext().getResources().getString(R.string.file_name));
    }

    public static File getFilePath(String filename) {
        // Get the path of external storage directory. Here we used download directory to read CSV
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //Read the specific PDF document from the download directory
        File filePath = new File(downloadDir + "/" + filename);

        return filePath;
    }
}
