package com.cyberproject.alpha;

import static com.cyberproject.alpha.FBRef.refImageGallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class SecondActivity extends AppCompatActivity {

    ImageView imageView;

    Button button2, button3;

    String lastGallery;
    Bitmap imageBitmap;

    DocumentReference refImage;
    File localFile;

    static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 102, REQUEST_PICK_IMAGE = 301;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        imageView = findViewById(R.id.imageView);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
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
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (s.equals("Pick Image")) {
            intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery(View view) {
        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentGallery, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("MMddyyyyhhmmss");
            Uri image = data.getData();
            if (image != null) {
                lastGallery = dateFormat.format(date);
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                    addImage(imageBitmap, refImageGallery, lastGallery);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void readImage(View view) {
        int id = view.getId();
        refImage = refImageGallery.document(lastGallery);
        try {
            localFile = File.createTempFile(lastGallery, "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ProgressDialog pd = ProgressDialog.show(this, "Image Download Dialog", "Downloading Image...", true);
        refImage.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Blob blob = (Blob) documentSnapshot.get("imageData");
                    byte[] bytes = blob.toBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.w("SecondActivity", "No such document exists.");
                }
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SecondActivity.this, "Image download failed.", Toast.LENGTH_SHORT).show();
                Log.e("SecondActivity", "image download error:", e);
            }
        });
    }

    public void addImage(Bitmap image, CollectionReference ref, String name) {
        ProgressDialog pd;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        if (imageBytes.length > 1040000) {
            pd = ProgressDialog.show(this, "Compress Dialog", "Image file size is too large! Compressing...");
            int quality = 100;
            while (imageBytes.length > 1040000) {
                quality -=5;
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

        pd = ProgressDialog.show(this, "Upload Image Dialog", "Uploading image...", true);
        ref.document(name).set(imageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("SecondActivity", "DocumentSnapshot written successfully.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("SecondActivity", "Error writing document:", e);
            }
        });
        pd.dismiss();
    }

    public void onClick2(View view) {
        openGallery(view);
    }

    public void onClick3(View view) {
        readImage(view);
    }
}