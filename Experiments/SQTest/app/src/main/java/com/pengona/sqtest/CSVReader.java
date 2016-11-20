package com.pengona.sqtest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.view.Display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class CSVReader {

        InputStream inputStream;

        public CSVReader(InputStream is) {
            this.inputStream = is;
        }

        public List<String[]> read() {
            List<String[]> resultList = new ArrayList<String[]>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String csvLine;
                //while((csvLine = reader.readLine()) != null) {
                for(int i=0; i<20; i++) {
                   //String[] row = csvLine.split(",");
                    String[] row = reader.readLine().split(",");
                    resultList.add(row);

                }
                //}
            } catch(IOException ex) {
                throw new RuntimeException("Error in reading CSV file:" + ex);
            } finally {
                try{
                    inputStream.close();
                } catch(IOException e) {
                    throw new RuntimeException("Error while closing input stream: " + e);
                }
            }
            return resultList;
        }

}
