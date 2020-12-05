package com.inti.gifty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    FirebaseAuth authRef;
    DatabaseReference dbUsers;

    TextView loginHereText;
    EditText nameTextField, emailTextField, passwordTextField, retypeTextField;
    Button registerButton;
    ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRef = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        nameTextField = findViewById(R.id.name_edit_text);
        emailTextField = findViewById(R.id.email_edit_text);
        passwordTextField = findViewById(R.id.password_edit_text);
        retypeTextField = findViewById(R.id.retype_password__edit_text);
        loginHereText = findViewById(R.id.login_text_button);
        registerButton = findViewById(R.id.register_button);
        loadingBar = findViewById(R.id.loading_bar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTextField.getText().toString().trim();
                String email = emailTextField.getText().toString().trim();
                String password = passwordTextField.getText().toString().trim();
                String retype = retypeTextField.getText().toString().trim();

                if (checkCredentials(name, email, password, retype)){
                    loadingBar.setVisibility(View.VISIBLE);
                    final User newUser = new User(name, email, "null");

                    authRef.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Account created!", Toast.LENGTH_LONG).show();
                                        dbUsers.child(task.getResult().getUser().getUid()).setValue(newUser);
                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    loadingBar.setVisibility(View.INVISIBLE);
                                }
                            });

                }
            }
        });

        loginHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
    }

    public boolean checkCredentials(String name, String email, String password, String retype){
        if (TextUtils.isEmpty(name)){
            nameTextField.setError("Name is required.");
            return false;
        }

        if (TextUtils.isEmpty(email)){
            emailTextField.setError("Email is required.");
            return false;
        } else {
            String regexPattern = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            if (!Pattern.matches(regexPattern, email)){
                emailTextField.setError("Email is invalid.");
                return false;
            }
        }

        if (TextUtils.isEmpty(password)){
            passwordTextField.setError("Password is required.");
            return false;
        } else {
            String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
            if (!Pattern.matches(passwordPattern, password)){
                passwordTextField.setError("Password must be at least 8 characters, include a letter and number.");
                return false;
            }
        }

        if (TextUtils.isEmpty(retype)){
            retypeTextField.setError("Password is required.");
            return false;
        } else {
            if (!retype.equals(password)){
                retypeTextField.setError("Password do not match.");
                return false;
            }
        }
        nameTextField.setError(null);
        emailTextField.setError(null);
        passwordTextField.setError(null);
        retypeTextField.setError(null);

        return true;
    }
}