package com.cyberproject.alpha;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FBRef {
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static CollectionReference referenceImageStamp = firestore.collection("imageStamp");
    public static CollectionReference referenceImageFull = firestore.collection("imageFull");
    public static CollectionReference referenceImageGallery = firestore.collection("imageGallery");
}
