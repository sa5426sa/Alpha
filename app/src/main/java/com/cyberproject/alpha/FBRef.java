package com.cyberproject.alpha;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FBRef {
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static CollectionReference refImageGallery = firestore.collection("imageGallery");
}
