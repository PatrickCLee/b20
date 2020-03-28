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
    implements FileChooser.ChooserListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //要求權限 https://developer.android.com/training/permissions/requesting
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)  {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
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
    public void test1(View view) {      //https://github.com/MostafaNasiri/AndroidFileChooser
        FileChooser.Builder builder = new FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER, this)
                .setMultipleFileSelectionEnabled(false)     //9改為false取消畫面下方按鈕
                .setFileFormats(new String[] {".jpg"})
                .setListItemsTextColor(R.color.colorPrimary)
                .setPreviousDirectoryButtonIcon(R.drawable.ic_prev_dir)
                .setDirectoryIcon(R.drawable.ic_directory)
                .setFileIcon(R.drawable.ic_file)

                // And more...
                ;
        try {
            FileChooser fileChooserFragment = builder.build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,fileChooserFragment)    //因為此處為add,所以一直按會重疊,正常使用應該用replace
                    .commit();
        } catch (ExternalStorageNotAvailableException e) {
            e.printStackTrace();
        }
    }
    public void upload(View view) {
        new Thread(){
            @Override
            public void run() {
                doUpload();
            }
        }.start();
    }

    private void doUpload(){
        try {
            MultipartUtility mu = new MultipartUtility(
                    "http://10.0.103.206:8080/PTL/PTL07_d2p1",
                    "",
                    "UTF-8");
            mu.addFormField("prefix","android");
            mu.addFilePart("upload",uploadFile);
            mu.finish();
            Log.v("brad","Upload OK");
        } catch (IOException e) {
            Log.v("brad",e.toString());
        }
    }

    File uploadFile;

    @Override
    public void onSelect(String path) {
        uploadFile = new File(path);
        Log.v("brad",path);
    }


}
