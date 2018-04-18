package com.example.adyu.photomemory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.adyu.photomemory.app.AppValues.EXTERNAL_PRIVATE_MEMORY;
import static com.example.adyu.photomemory.app.AppValues.EXTERNAL_PUBLIC_MEMORY;
import static com.example.adyu.photomemory.app.AppValues.INTERNAL_PRIVATE_MEMORY;
import static com.example.adyu.photomemory.app.AppValues.REGISTER_PARAMETER;
import static com.example.adyu.photomemory.app.AppValues.TAG;

public class MainView extends AppCompatActivity {
    String target = "";
    @SuppressLint("SimpleDateFormat")
    String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    private void init() {
        Button btPrivateInternal = findViewById(R.id.btPrivateInternal);
        Button btPublicInternal = findViewById(R.id.btPublicInternal);
        Button btPublicExternal = findViewById(R.id.btPublicExternal);
        btPrivateInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                target = INTERNAL_PRIVATE_MEMORY;
                takePhoto();
            }
        });
        btPublicInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                target = EXTERNAL_PRIVATE_MEMORY;
                takePhoto();
            }
        });
        btPublicExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStoragePermissionGranted();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        init();
    }

    private void takePhoto() {
        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(i, REGISTER_PARAMETER);
    }

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission granted");
            } else {
                Log.v(TAG, "Permission denied");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            target = EXTERNAL_PUBLIC_MEMORY;
            takePhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REGISTER_PARAMETER) {
                Bundle b = data.getExtras();
                Bitmap photo = null;
                if (b != null) {
                    photo = (Bitmap) b.get("data");
                }
                FileOutputStream fos = null;
                try {
                    switch (target) {
                        case INTERNAL_PRIVATE_MEMORY:
                            fos = new FileOutputStream(getFilesDir() + "/" + date + ".jpg");
                            Log.v("xyzyx", getFilesDir() + "/" + date + ".jpg");
                            break;
                        case EXTERNAL_PRIVATE_MEMORY:
                            fos = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + date + ".jpg");
                            Log.v("xyzyx", getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + date + ".jpg");
                            break;
                        case EXTERNAL_PUBLIC_MEMORY:
                            fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + date + ".jpg");
                            Log.v("xyzyx", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + date + ".jpg");
                            break;
                    }
                    if (photo != null) {
                        photo.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    }
                } catch (FileNotFoundException ignored) {
                }
            }
        }
    }
}
