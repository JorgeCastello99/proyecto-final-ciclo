package com.example.toletaxifinal.login;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.example.toletaxifinal.central.CentralTrabajador;
import com.example.toletaxifinal.recuperarcontraseña.RestablecerContra;
import com.example.toletaxifinal.registros.RegistroTrabajadores;
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

public class LoginTrabajador extends AppCompatActivity {

    private EditText emailLogin;
    private EditText passwLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;

    private String usuario;
    private String contraseña;


    private Window window;
    private AlertDialog mDialog;
    private boolean correcto=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_trabajador);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.window=getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle("Login trabajador");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));


        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        emailLogin=findViewById(R.id.edtTxtEmailT);
        passwLogin=findViewById(R.id.editTxtPaswT);

        mDialog=new SpotsDialog.Builder().setContext(LoginTrabajador.this).setMessage("Espere un momento").build();

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
        if(authStateListener!=null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    public void onClickTxtvRegistro(View view) {
        Intent intent =new Intent(this, RegistroTrabajadores.class);
        startActivity(intent);
    }


    public void onClickRestablecer(View view) {
        startActivity(new Intent(LoginTrabajador.this, RestablecerContra.class));
    }

    public void btnEntrarT(View view) {

        usuario=emailLogin.getText().toString();
        contraseña=passwLogin.getText().toString();

        if(!usuario.isEmpty() && !contraseña.isEmpty()){
            if(contraseña.length()>6){
                LoginUser();
            }
            else{
                Toast.makeText(LoginTrabajador.this,"La contraseña debe tener al menos 6 caracteres",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginTrabajador.this,"Complete lo campos",Toast.LENGTH_SHORT).show();
        }
    }

    private void LoginUser() {


        mDatabase.child("Workers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        mDatabase.child("Workers").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String e=dataSnapshot.child("email").getValue().toString();
                                if(usuario.equals(e)){
                                    Log.e("ha entrado en el true",usuario);
                                    correcto=true;

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
        mAuth.signInWithEmailAndPassword(usuario,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful() ){



                    Log.e("Ha entrado en signin ",usuario);

                    Intent intent = new Intent(LoginTrabajador.this, CentralTrabajador.class);
                    startActivity(intent);
                    finish();

                }else{
                    Log.e("No ha entrado ",usuario);
                    Toast.makeText(LoginTrabajador.this,"La contraseña o El correo son incorrectos",Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });



    }

    
}
