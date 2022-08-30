package com.example.toletaxifinal.registros;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.toletaxifinal.R;
import com.example.toletaxifinal.central.CentralUsuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class RegistroUsuarios extends AppCompatActivity {

    private EditText nombre;
    private EditText apellidos;
    private EditText email;
    private EditText telefono;
    private EditText contraseña;


    private String nom="";
    private String ape="";
    private String ema="";
    private String tele="";
    private String contra="";

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences mPref;

    private FirebaseAnalytics mFirebaseAnalytics;

    private DatabaseReference mDatabase;
    AlertDialog mDialog;

    private Window window;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuarios);
        nombre=findViewById(R.id.eTxtNombreU);
        apellidos=findViewById(R.id.eTxtApellidosU);
        email=findViewById(R.id.eTxtEmailU);
        telefono=findViewById(R.id.eTxtTelefonoU);
        contraseña=findViewById(R.id.eTxtContraseñaU);


        this.window=getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle("Registro usuarios");


        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        mDialog=new SpotsDialog.Builder().setContext(RegistroUsuarios.this).setMessage("Espere un momento").build();

    }

    public void onClickRegistroU(View view) {

        ema=email.getText().toString();
        contra=contraseña.getText().toString();
        nom=nombre.getText().toString();
        ape=apellidos.getText().toString();
        tele=telefono.getText().toString();
        //Creacion del registro del usuario

            if(!nom.isEmpty() && !ape.isEmpty() &&  !ema.isEmpty() && !contra.isEmpty() &&  !tele.isEmpty()){

                if(contraseña.length() >= 6){

                    registroUsuario();

                }else{
                    Toast.makeText(RegistroUsuarios.this,"La contraseña debe tener 6 o mas caracteres", Toast.LENGTH_SHORT).show();

                }
            }else {
                Toast.makeText(RegistroUsuarios.this,"Debes rellenar el formulario", Toast.LENGTH_SHORT).show();


            }
   }


    private void registroUsuario(){

        mDialog.show();
        mAuth.createUserWithEmailAndPassword(ema,contra).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if (!task.isSuccessful()) {
                    Toast.makeText(RegistroUsuarios.this, "No se pudo registrar este usuario", Toast.LENGTH_LONG).show();
                } else {

                    Map<String,Object> map =new HashMap<>();
                    map.put("nombre",nom);
                    map.put("apellidos",ape);
                    map.put("telefono",tele);
                    map.put("email",ema);
                    map.put("contraseña",contra);

                    String id=mAuth.getCurrentUser().getUid();

                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if(task2.isSuccessful()){
                                        Intent intent = new Intent(RegistroUsuarios.this, CentralUsuario.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(RegistroUsuarios.this, "No se pudo registrar los campos", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });




                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if(authStateListener!=null) {
            mAuth.removeAuthStateListener(authStateListener);
        }*/
    }
}
