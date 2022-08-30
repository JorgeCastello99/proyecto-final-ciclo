package com.example.toletaxifinal.provider;


import com.example.toletaxifinal.cloudmesagging.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class TokenProvider {

    DatabaseReference mDatabaseReference;

    public TokenProvider() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Tokens");
    }
    public void crear(final String idUser){
        if (idUser ==null) return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Token token = new Token(instanceIdResult.getToken());
                mDatabaseReference.child(idUser).setValue(token);
            }
        });
    }
    public DatabaseReference getToken(String idUser) {
        return mDatabaseReference.child(idUser);
    }
}
