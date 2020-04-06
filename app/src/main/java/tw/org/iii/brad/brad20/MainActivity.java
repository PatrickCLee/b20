package tw.org.iii.brad.brad20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooser;

public class MainActivity extends AppCompatActivity
    implements FileChooser.ChooserListener{     //*2 實做

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //*0 要求權限 https://developer.android.com/training/permissions/requesting
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)  {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        123);
            }
         else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        init();
    }

    private void init() {

    }
    public void test1(View view) {    //*1  //https://github.com/MostafaNasiri/AndroidFileChooser
        FileChooser.Builder builder = new FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER, this)
                .setMultipleFileSelectionEnabled(false)     //*6改為false取消畫面下方按鈕
                .setFileFormats(new String[] {".jpg"})
                .setListItemsTextColor(R.color.colorPrimary)
                .setPreviousDirectoryButtonIcon(R.drawable.ic_prev_dir)
                .setDirectoryIcon(R.drawable.ic_directory)
                .setFileIcon(R.drawable.ic_file)

                // And more...
                ;
        try {
            FileChooser fileChooserFragment = builder.build();      //*4 也是從github上a來的
            getSupportFragmentManager().beginTransaction()          //*5
                    .add(R.id.container,fileChooserFragment)    //因為此處為add,所以一直按會重疊,正常使用應該用replace
                    .commit();
        } catch (ExternalStorageNotAvailableException e) {
            e.printStackTrace();
        }
    }
    public void upload(View view) {     //*10 檔案上傳需包執行序
        new Thread(){
            @Override
            public void run() {
                doUpload();             //*11 拉出去寫
            }
        }.start();
    }

    private void doUpload(){            //*11
        try {
            MultipartUtility mu = new MultipartUtility(             //*12 創出物件,建構參考從網路找來的MultipartUtility.java
//                    "http://10.0.103.206:8080/PTL/PTL07_d2p1",
                    "http://192.168.0.16:8080/AndroidClass/Brad07",
                    "",
                    "UTF-8");
            mu.addFormField("prefix","android");  //prefix為後端定義(見Brad07, 後方隨意命名
            mu.addFilePart("upload",uploadFile);    //upload為後端定義(見Brad07, uploadFile為選擇到的檔案 見下方
            mu.finish();
            Log.v("brad","Upload OK");
        } catch (IOException e) {
            Log.v("brad",e.toString());
        }
    }

    File uploadFile;                        //*7

    @Override
    public void onSelect(String path) {     //*3 必須實做的方法
        uploadFile = new File(path);        //*7
        Log.v("brad",path);             //*3
    }


}
