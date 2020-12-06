package com.inti.gifty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Arrays;

public class Login extends AppCompatActivity {

    FirebaseAuth authRef;
    DatabaseReference dbUsers;
    private CallbackManager callBackManager;
    private GoogleSignInClient googleSignInClient;
    private ValueEventListener dbListener;

    TextView registerHereText, forgotPasswordText, errorMessageText;
    EditText emailTextField, passwordTextField;
    Button loginButton, facebookLoginButton, gmailLoginButton;
    ProgressBar loadingBar;

    private int GOOGLE_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(Login.this);
        authRef = FirebaseAuth.getInstance();

        emailTextField = findViewById(R.id.email_edit_text);
        passwordTextField = findViewById(R.id.password_edit_text);
        registerHereText = findViewById(R.id.register_text_button);
        forgotPasswordText = findViewById(R.id.forgot_password_text);
        errorMessageText = findViewById(R.id.login_error_text);
        loginButton = findViewById(R.id.login_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        gmailLoginButton = findViewById(R.id.gmail_login_button);
        loadingBar = findViewById(R.id.loading_bar);

        if (authRef.getCurrentUser() != null){
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin();
            }
        });

        registerHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
                finish();
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin();
            }
        });

        gmailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gmailLogin();
            }
        });

    }

    private void emailLogin(){
        errorMessageText.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
        String email = emailTextField.getText().toString().trim();
        String password = passwordTextField.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            authRef.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        errorMessageText.setVisibility(View.VISIBLE);
                    }
                    loadingBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            loadingBar.setVisibility(View.INVISIBLE);
        }
    }

    private void facebookLogin(){
        loadingBar.setVisibility(View.VISIBLE);
        callBackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile"));

        LoginManager.getInstance().registerCallback(callBackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                    }
                });
    }

    private void gmailLogin(){
        Intent gmailSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(gmailSignInIntent, GOOGLE_TAG);
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void handleFacebookAccessToken(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        authRef.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()){
                    final FirebaseUser currentUser = authRef.getCurrentUser();
                    dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                    dbListener = dbUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean userExists = false;
                            for (DataSnapshot user : snapshot.getChildren()){
                                if (user.getKey().equals(currentUser.getUid())){
                                    userExists = true;
                                    break;
                                }
                            }
                            if (!userExists) {
                                User newUser = new User(currentUser.getDisplayName(), currentUser.getEmail(), "null");
                                dbUsers.child(currentUser.getUid()).setValue(newUser);
                            }
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void handleGmailSignInResults(Task<GoogleSignInAccount> googleTask){
        try {
            GoogleSignInAccount acc = googleTask.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
            authRef.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loadingBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()){
                        final FirebaseUser currentUser = authRef.getCurrentUser();
                        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                        dbListener = dbUsers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean userExists = false;
                                for (DataSnapshot user : snapshot.getChildren()){
                                    if (user.getKey().equals(currentUser.getUid())){
                                        userExists = true;
                                        break;
                                    }
                                }
                                if (!userExists) {
                                    User newUser = new User(currentUser.getDisplayName(), currentUser.getEmail(), "null");
                                    dbUsers.child(currentUser.getUid()).setValue(newUser);
                                }
                                Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (ApiException e) {
            Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callBackManager != null)
                callBackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_TAG){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGmailSignInResults(task);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbListener != null) {
            dbUsers.removeEventListener(dbListener);
        }
    }
}