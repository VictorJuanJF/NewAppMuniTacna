package com.example.jimenez.appmunitacna;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimenez.appmunitacna.Clases.Usuario;
import com.example.jimenez.appmunitacna.objects.FirebaseReferences;
import com.example.jimenez.appmunitacna.objects.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "FirebaseData";
    ProgressBar progressBar;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.etNombres)
    EditText etNombres;
    @BindView(R.id.etDNI)
    EditText etDNI;
    @BindView(R.id.buttonSignUp)
    Button buttonSignUp;
    @BindView(R.id.textViewLogin)
    TextView textViewLogin;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.etDireccion)
    EditText etDireccion;
    @BindView(R.id.etCelular)
    EditText etCelular;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference reference = database.getReference(FirebaseReferences.USERS_REFERENCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();


    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nombre=etNombres.getText().toString().trim();
        String dni=etDNI.getText().toString().trim();
        String direccion=etDireccion.getText().toString().trim();
        String celular=etCelular.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Ingresa tu correo");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Por favor, ingresa un correo válido");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Ingresa una contraseña");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Ingresa un mínimo de 4 caracteres");
            editTextPassword.requestFocus();
            return;
        }

        if (nombre.isEmpty()) {
            etNombres.setError("Ingresa tus nombres y apellidos");
            etNombres.requestFocus();
            return;
        }
        if (dni.isEmpty()) {
            etDNI.setError("Ingresa tu DNI");
            etDNI.requestFocus();
            return;
        }
        if (direccion.isEmpty()) {
            etDireccion.setError("Ingresa una dirección");
            etDireccion.requestFocus();
            return;
        }
        if (celular.isEmpty()) {
            etCelular.setError("Ingresa tu número de celular");
            etCelular.requestFocus();
            return;
        }
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Registrando")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String userId=reference.push().getKey();
        Usuario currentUserData=new Usuario(userId,nombre,email,celular,dni,direccion);
        Global.setCurrentDataUser(currentUserData);
        Global.setUserKey(userId);

        registerUserIntoDatabase(currentUserData);


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Ya estabas registrado!", Toast.LENGTH_SHORT).show();
                        dialog.cancel();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void registerUserIntoDatabase(final Usuario currentUserData) {


        //Saving user into firebase db
        reference.orderByChild("correo").equalTo(Global.getCurrentDataUser().getCorreo()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i(TAG, "dataSnapshot value = " + dataSnapshot.getValue());
                        if(!dataSnapshot.exists()){
                            // User Exists

                            reference.child(Global.getUserKey()).setValue(currentUserData);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("ErrorFirebase", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }

    @OnClick({R.id.buttonSignUp, R.id.textViewLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;
            case R.id.textViewLogin:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
