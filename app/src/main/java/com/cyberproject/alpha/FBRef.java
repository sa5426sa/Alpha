package com.cyberproject.alpha;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FBRef {
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
}
