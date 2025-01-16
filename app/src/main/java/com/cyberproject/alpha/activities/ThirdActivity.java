package com.cyberproject.alpha.activities;

import static com.cyberproject.alpha.FBRef.*;
import static com.cyberproject.alpha.Tags.*;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cyberproject.alpha.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThirdActivity extends AppCompatActivity {

    TextView textView3;
    ImageView imageView2;
    Button button4, button5;

    String lastStamp;
    Bitmap imageBitmap;
    File localFile;
    DocumentReference refImage;
    static final int REQUEST_CAMERA_PERMISSION = 101;
    static final int REQUEST_STAMP_CAPTURE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        imageView2 = findViewById(R.id.imageView2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String s = item.getTitle().toString();
        Intent intent;
        if (s.equals("Register")) {
            intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
        } else if (s.equals("Pick Image")) {
            intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        } else if (s.equals("Snap Image")) {
            intent = new Intent(this, ThirdActivity.class);
            startActivity(intent);
        } else if (s.equals("Reminder")) {
            intent = new Intent(this, FourthActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void takeStamp(View view) {
        Intent takePicIntent = new Intent();
        takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicIntent, REQUEST_STAMP_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy_hh-mm-ss");
            Bundle extras = data.getExtras();
            if (extras != null) {
                lastStamp = dateFormat.format(date);
                imageBitmap = (Bitmap) extras.get("data");
                addImage(imageBitmap, refImageStamp, lastStamp);
            }
        }
    }

    public void addImage(Bitmap image, CollectionReference ref, String name) {
        ProgressDialog pd;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        Log.d(tagThird + ": image compression", String.valueOf(imageBytes.length));
        if (imageBytes.length > 1040000) {
            pd = ProgressDialog.show(this, "Image Compress", "Image file size is too large! Compressing...", true);
            int quality = 100;
            while (imageBytes.length > 1040000) {
                quality -= 5;
                baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                imageBytes = baos.toByteArray();
            }
            pd.dismiss();
        }

        Blob blob = Blob.fromBytes(imageBytes);
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("imageName", name);
        imageMap.put("imageData", blob);

        pd = ProgressDialog.show(this, "Upload Image", "Uploading image...", true);
        ref.document(name).set(imageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(tagThird, "DocumentSnapshot written successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(tagThird, "error writing document:", e);
            }
        });
        pd.dismiss();
    }

    public void readImage(@NonNull View view) {
        refImage = refImageStamp.document(lastStamp);
        try {
            localFile = File.createTempFile(lastStamp, "png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final ProgressDialog pd = ProgressDialog.show(this, "Image Download", "Downloading image...", true);
        refImage.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Blob blob = (Blob) documentSnapshot.get("imageData");
                    byte[] bytes = blob.toBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView2.setImageBitmap(bitmap);
                } else {
                    Log.e(tagThird, "no such document exists");
                }
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(ThirdActivity.this, "image download failed", Toast.LENGTH_SHORT).show();
                Log.e(tagThird, "download error", e);
            }
        });
    }

    public void onClick4(View view) {
        takeStamp(view);
    }

    public void onClick5(View view) {
        readImage(view);
    }
}