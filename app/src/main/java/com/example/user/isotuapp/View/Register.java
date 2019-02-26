package com.example.user.isotuapp.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    Button buttonRegister;
    TextView toLogin;
    private EditText username,password,retype,email;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog pDialog;
    int completeProfile;
    String userString = "";
    String emailString = "";
    String imageString = "";
    String fullnameString = "";
    String nim = "";
    String fakultas = "";
    String jurusan = "";
    String nohp = "";
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        completeProfile = 0 ;
        mFirebaseAuth=FirebaseAuth.getInstance();

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        retype = (EditText) findViewById(R.id.retype_password);
        email = (EditText) findViewById(R.id.email);



        buttonRegister = (Button) findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameString = username.getText().toString().trim();
                String passwordString = password.getText().toString().trim();
                String confirm_password = retype.getText().toString().trim();
                final String emailString  = email.getText().toString().trim();

                if (TextUtils.isEmpty(usernameString)){
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordString)){
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirm_password)){
                    Toast.makeText(getApplicationContext(), "Enter conformation password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(emailString)){
                    Toast.makeText(getApplicationContext(), "Password does'nt match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()< 6){
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!confirm_password.equals(passwordString)){
                    Toast.makeText(getApplicationContext(), "Password and Confirmation Password does'nt match!", Toast.LENGTH_SHORT).show();
//                    pDialog.dismiss();
                }else{
                    displayLoader();
                    mFirebaseAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Register.this, "Authentication failed : Account already exist", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d("LOL", "onComplete: success");
                                final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                                final HashMap<String, Object> user= new HashMap<>();
                                userString = currentUser.getDisplayName();
                                final String uidString = currentUser.getUid();
                                final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
                                dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User us = dataSnapshot.getValue(User.class);
                                        user.put("username",usernameString);
                                        user.put("email",emailString);
                                        user.put("image", imageString);
                                        user.put("fullname", fullnameString);
                                        user.put("nim",nim);
                                        user.put("fakultas",fakultas);
                                        user.put("jurusan",jurusan);
                                        user.put("nohp",nohp);
                                        user.put("completeProfile",completeProfile);
                                        user.put("Uid",uidString);

                                        dbf.setValue(user);
                                        Toast.makeText(Register.this, "Register Success", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this,CompleteProfile.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    });
                }
            }
        });



        toLogin = (TextView) findViewById(R.id.to_login);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,CompleteProfile.class);
                startActivity(intent);
            }
        });
    }
    private void displayLoader() {
        pDialog = new ProgressDialog(Register.this);
        pDialog.setMessage("Create Account...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        int d = R.drawable.custom_progress_dialog;
        Drawable drawable = getResources().getDrawable(d);
        pDialog.setIndeterminateDrawable(drawable);
        pDialog.show();
    }
}
