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
import com.example.toletaxifinal.central.CentralTrabajador;
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

public class RegistroTrabajadores extends AppCompatActivity {


    private EditText nombre;
    private EditText apellidos;
    private EditText email;
    private EditText telefono;
    private EditText contraseña;
    private EditText matricula;
    private EditText modelo;

    private String nom="";
    private String ape="";
    private String ema="";
    private String tele="";
    private String contra="";
    private String model="";
    private String matri="";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences mPref;

    private FirebaseAnalytics mFirebaseAnalytics;

    private DatabaseReference mDatabase;
    AlertDialog mDialog;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_trabajadores);

        nombre=findViewById(R.id.eTxtNombreT);
        apellidos=findViewById(R.id.eTxtApellidosT);
        email=findViewById(R.id.eTxtEmailT);
        telefono=findViewById(R.id.eTxtTelefonoT);
        contraseña=findViewById(R.id.eTxtContraseñaT);
        matricula=findViewById(R.id.eTxtMatricula);
        modelo=findViewById(R.id.eTxtModelo);


        this.window=getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle("Registro trabajadores");


        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        //Preferencias
        mPref=getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);


                mDialog=new SpotsDialog.Builder().setContext(RegistroTrabajadores.this).setMessage("Espere un momento").build();


    }

    public void onClickRegistroT(View view) {
        ema=email.getText().toString();
        contra=contraseña.getText().toString();
        nom=nombre.getText().toString();
        ape=apellidos.getText().toString();
        tele=telefono.getText().toString();
        model=modelo.getText().toString();
        matri=matricula.getText().toString();
        //Creacion del registro del trabajdor


        if(!nom.isEmpty() && !ape.isEmpty() &&  !ema.isEmpty() && !contra.isEmpty() &&  !tele.isEmpty() && !model.isEmpty() && !matri.isEmpty()){

            if(contraseña.length() >= 6){
                registroTrabajador();

            }else{
                Toast.makeText(RegistroTrabajadores.this,"La contraseña debe tener 6 o mas caracteres", Toast.LENGTH_SHORT).show();

            }
        }
        else {
            Toast.makeText(RegistroTrabajadores.this,"Debes rellenar el formulario", Toast.LENGTH_SHORT).show();


        }
    }

    private void registroTrabajador(){

        mDialog.show();
        mAuth.createUserWithEmailAndPassword(ema,contra).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if (!task.isSuccessful()) {
                    Toast.makeText(RegistroTrabajadores.this, "No se pudo registrar este usuario", Toast.LENGTH_LONG).show();
                } else {

                    Map<String,Object> map =new HashMap<>();
                    map.put("nombre",nom);
                    map.put("apellidos",ape);
                    map.put("telefono",tele);
                    map.put("modelo",model);
                    map.put("matricula",matri);
                    map.put("email",ema);
                    map.put("contraseña",contra);

                    String id=mAuth.getCurrentUser().getUid();

                    mDatabase.child("Workers").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){

                                Intent intent = new Intent(RegistroTrabajadores.this, CentralTrabajador.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(RegistroTrabajadores.this, "No se pudo registrar los campos", Toast.LENGTH_LONG).show();

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
