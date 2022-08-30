package com.example.toletaxifinal.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.toletaxifinal.R;
import com.example.toletaxifinal.central.CentralUsuario;
import com.example.toletaxifinal.recuperarcontraseña.RestablecerContra;
import com.example.toletaxifinal.registros.RegistroUsuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;


public class LoginUsuario extends AppCompatActivity {

    private EditText emailLogin;
    private EditText passwLogin;

    private FirebaseAuth mAuth;


    private String usuario;
    private String contraseña;
    private DatabaseReference mDatabase;

    SharedPreferences mPref;

    AlertDialog mDialog;

    private boolean correcto=false;

    private Window window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_usuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.window=getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle("Login cliente");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();




        emailLogin=findViewById(R.id.edtTxtEmailT2);
        passwLogin=findViewById(R.id.editTxtPaswT2);

        mDialog=new SpotsDialog.Builder().setContext(LoginUsuario.this).setMessage("Espere un momento").build();
        correcto=false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
        if(authStateListener!=null) {
            mAuth.removeAuthStateListener(authStateListener);
        }*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    public void onClickTxtvRegistro(View view) {
        Intent intent =new Intent(this, RegistroUsuarios.class);
        startActivity(intent);
    }

    public void btnEntrar(View view) {
         usuario=emailLogin.getText().toString();
         contraseña=passwLogin.getText().toString();

        if(!usuario.isEmpty() && !contraseña.isEmpty()){
            if(contraseña.length()>6){
                LoginUser();
            }
            else{
                Toast.makeText(LoginUsuario.this,"La contraseña debe tener al menos 6 caracteres",Toast.LENGTH_SHORT).show();

            }


        }else{
            Toast.makeText(LoginUsuario.this,"Complete lo campos",Toast.LENGTH_SHORT).show();
        }

    }

    private void LoginUser(){
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        mDatabase.child("Users").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String a=dataSnapshot.child("email").getValue().toString();
                                if(usuario.equals(a)){
                                    correcto=true;
                                    Log.e("Ha entrado en true",usuario);

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        if(correcto==true){
                            break;
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDialog.show();
        mAuth.signInWithEmailAndPassword(usuario, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Log.e("Ha entrado ",usuario);
                    entrarCentral();
                }
                else{
                    Log.e("No ha entrado ",usuario);
                    Toast.makeText(LoginUsuario.this, "La contraseña o El correo son incorrectos", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });



    }
    public void entrarCentral(){
        Intent intent = new Intent(LoginUsuario.this, CentralUsuario.class);
        startActivity(intent);
        finish();
    }

    public void onClickRestablecer(View view) {
        startActivity(new Intent(LoginUsuario.this, RestablecerContra.class));
    }

}
