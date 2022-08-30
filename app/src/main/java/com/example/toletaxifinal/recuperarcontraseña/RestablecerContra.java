package com.example.toletaxifinal.recuperarcontraseña;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RestablecerContra extends AppCompatActivity {

    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private EditText edt;
    private String usuario="";
    private Window window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_contra);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.window=getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));

        mAuth= FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);
        edt=findViewById(R.id.emailPass);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    private void resetPassw(){
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Toast.makeText(RestablecerContra.this,"El correo se envió correctamente",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RestablecerContra.this,"Nose pudo enviar el correo",Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }


    public void restablecer(View view) {
        usuario= edt.getText().toString();
        if(!usuario.isEmpty()){
            mDialog.setMessage("Espere un momento...");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            resetPassw();
        }
        else{
            Toast.makeText(RestablecerContra.this,"Introduce el email en el cuadro de texto de arriba",Toast.LENGTH_SHORT).show();
        }
    }
}
