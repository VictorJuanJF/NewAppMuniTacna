package com.example.jimenez.appmunitacna;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LoginActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.buttonLogin)
    Button buttonLogin;
    @BindView(R.id.textViewSignup)
    TextView textViewSignup;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
    }


    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Por favor ingresa tu correo");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Por favor ingresa un correo válido");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Por favor ingresa tu contraseña");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 4) {
            editTextPassword.setError("Ingrese mínimo 4 caracteres");
            editTextPassword.requestFocus();
            return;
        }
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Ingresando")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "El usuario o contraseña fue incorrecto", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }



    @OnClick({R.id.buttonLogin, R.id.textViewSignup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                userLogin();
                break;
            case R.id.textViewSignup:
                finish();
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
}
