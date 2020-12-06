package com.inti.gifty;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText emailTextField;
    Button sendButton;
    ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailTextField = findViewById(R.id.email_edit_text);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextField.getText().toString();

                if (checkEmail(email)){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPassword.this, "Email sent!", Toast.LENGTH_SHORT).show();
                                        goBack();
                                    } else{
                                        Toast.makeText(ForgotPassword.this, "Invalid email!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    public boolean checkEmail(String email){
        if (TextUtils.isEmpty(email)){
            emailTextField.setError("Email is required.");
            return false;
        }
        return true;
    }

    public void goBack(){
        startActivity(new Intent(ForgotPassword.this, Login.class));
        finish();
    }
}