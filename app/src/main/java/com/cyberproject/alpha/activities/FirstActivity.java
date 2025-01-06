package com.cyberproject.alpha.activities;

import static com.cyberproject.alpha.FBRef.auth;

import static com.cyberproject.alpha.Tags.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cyberproject.alpha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class FirstActivity extends AppCompatActivity {

    EditText editTextEmailAddress, editTextPassword;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        button = findViewById(R.id.button);
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void createUser(View view) {
        String email = editTextEmailAddress.getText().toString();
        String password = editTextPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Connecting..");
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.i(tagFirst, "createUserWithEmailAndPassword:success");
                        Toast.makeText(FirstActivity.this, "User created successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(FirstActivity.this, "Password is too weak!", Toast.LENGTH_SHORT).show();
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(FirstActivity.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                        } else if (exception instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(FirstActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                        } else if (exception instanceof FirebaseNetworkException) {
                            Toast.makeText(FirstActivity.this, "Network error. Check your connection and try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FirstActivity.this, "An unknown error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    public void onClick(View view) {
        createUser(view);
    }
}