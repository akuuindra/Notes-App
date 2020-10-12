package com.indrawirawan.notes;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE = 100;
    ListView listView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv_files);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddFileActivity.class);
                Map<String, Object> data = (Map < String, Object >)
                parent.getAdapter().getItem(position);
                intent.putExtra("Filename", data.get("name").toString());
                Toast.makeText(MainActivity.this, "Anda memilih " + data.get("name"), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        fab = findViewById(R.id.buttonAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFileActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> data = (Map < String, Object >)
                parent.getAdapter().getItem(position);
                tampilkanDialogKonfirmasiHapusCatatan(data.get("name").toString());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                mengambillistFilePadaFolder();
            }
        } else {
            mengambillistFilePadaFolder();
        }
    }

    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
             }
            }else{
            return true;
            }
        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mengambillistFilePadaFolder();
                }
                break;
        }
    }

    void mengambillistFilePadaFolder() {
        String appName = getResources().getString(R.string.app_name);
        String path = Environment.getExternalStorageDirectory().toString() + "/" + appName;
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            String[] filename = new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM YYYY HH:mm:ss");
            ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < files.length; i++) {
                filename[i] = files[i].getName();
                Date lastModDate = new Date(files[i].lastModified());
                dateCreated[i] = simpleDateFormat.format(lastModDate);
                Map<String, Object> listItemMap = new HashMap<>();
                listItemMap.put("name", filename[i]);
                listItemMap.put("date", dateCreated[i]);
                itemDataList.add(listItemMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                    itemDataList, android.R.layout.simple_list_item_2, new String[]{"name", "date"}, new int[]{android.R.id.text1, android.R.id.text2});
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }
       /* @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }
       @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.buttonAdd:
                    Intent intent = new Intent(this, AddFileActivity.class);
                    startActivity(intent);
                    break;
            }
            return super.onOptionsItemSelected(item);
        }*/
        void tampilkanDialogKonfirmasiHapusCatatan ( final String filename){
            new AlertDialog.Builder(this)
                    .setTitle("Hapus catatan ini ?")
                    .setMessage("Apakah anda yakin ingin menghapus catatan " + filename + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            hapusFile(filename);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }

        void hapusFile (String filename){
            String appName = getResources().getString(R.string.app_name);
            String path = Environment.getExternalStorageDirectory().toString() + "/" + appName;
            File file = new File(path, filename);
            if (file.exists()) {
                file.delete();
            }
            mengambillistFilePadaFolder();
        }
    }

/*import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView lvFile;
    static final int REQUEST_CODE_STORAGE = 100;
    static final String LOG_TAG = "Log Activity";
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvFile = findViewById(R.id.lv_files);
        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Click :" + parent.getAdapter().getItem(position));
            }
        });

        fab = findViewById(R.id.buttonAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkStoragePermission())
                getListFiles();
        } else
            getListFiles();
    }

    private boolean checkStoragePermission() {

        if(Build.VERSION.SDK_INT>=23){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                return true;
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        }else {
            return  true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getListFiles();
            }
        }
    }

    private void getListFiles(){
        String appName = getResources().getString(R.string.app_name);
        String path = Environment.getExternalStorageDirectory().toString() + "/" + appName;
        File dir = new File(path);

        if (!dir.exists()){

            dir.mkdirs();
            final String FILENAME = "Notes.txt";
            File dataFile = new File(path, FILENAME);


            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                String text = getResources().getString(R.string.media_unavailable);
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream mOutput = new FileOutputStream(dataFile, false);
                String data = "DATA";
                mOutput.write(data.getBytes());
                mOutput.close();
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        File[] files = dir.listFiles();
        String[] fname = new String[files.length];
        String[] datec = new String[files.length];
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM YYYY HH:mm:ss");
        ArrayList<Map<String, Object>> dataList  = new ArrayList<Map<String, Object>>();

        for (int i=0; i< files.length; i++){
            fname[i] = files[i].getName();
            Date tempDate = new Date(files[i].lastModified());
            datec[i] = dateFormat.format(tempDate);

            Map<String, Object>itemMap = new HashMap<String, Object>();
            itemMap.put("name", fname[i]);
            itemMap.put("date", datec[i]);
            dataList.add(itemMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, dataList, android.R.layout.simple_list_item_2,
                new String[]{"name", "date"}, new int[]{android.R.id.text1, android.R.id.text2});
        lvFile.setAdapter(adapter);



    }

}*/
