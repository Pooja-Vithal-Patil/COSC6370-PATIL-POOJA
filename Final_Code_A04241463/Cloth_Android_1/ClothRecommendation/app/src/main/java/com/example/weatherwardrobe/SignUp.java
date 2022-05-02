package com.example.weatherwardrobe;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String useridd;


    EditText cityEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseDatabase = FirebaseDatabase.getInstance("https://cloth-308e0-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference();
        FAuth = FirebaseAuth.getInstance();

        final EditText name = findViewById(R.id.name);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.txtPassword);
        final EditText confirmPassword = findViewById(R.id.password);
        final LoginSqliteHelper sqliteHelper = new LoginSqliteHelper(this);
        cityEdit = findViewById(R.id.city);

        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
        findViewById(R.id.top_curve).startAnimation(top_curve_anim);
        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edittext_anim);
        email.startAnimation(top_curve_anim);
        password.startAnimation(top_curve_anim);
        password.startAnimation(top_curve_anim);
        confirmPassword.startAnimation(top_curve_anim);
        name.startAnimation(top_curve_anim);
        cityEdit.startAnimation(top_curve_anim);


        findViewById(R.id.city).startAnimation(top_curve_anim);
        findViewById(R.id.logo).startAnimation(editText_anim);
        findViewById(R.id.appName).startAnimation(editText_anim);
        findViewById(R.id.alreadyHaveAccount).startAnimation(editText_anim);

        findViewById(R.id.alreadyHaveAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignIn.class));
            }
        });


        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Not Valid Email", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().equals(confirmPassword.getText().toString()) && (!password.getText().toString().isEmpty())) {

                    final String username = email.getText().toString();
                    final String Email = email.getText().toString();
                    final String Password = password.getText().toString();


                    databaseReference.child("Users").orderByChild("Username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(SignUp.this, "Username already exists. Please try other username.", Toast.LENGTH_SHORT).show();


                            } else {
                                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                                mDialog.setCancelable(false);
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.setMessage("Registering please wait...");
                                mDialog.show();

                                FAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            useridd = FAuth.getCurrentUser().getUid();

                                            addUsers(username, Email, Password, cityEdit.getText().toString());
//                                            addPrivateDetails(useridd,email,gender,birth,mobileno);
                                            addPasswords(Password);

                                            mDialog.dismiss();

                                            FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                                        builder.setMessage("Registered Successfully,Please Verify your Email");
                                                        builder.setCancelable(false);
                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                dialog.dismiss();

                                                                Toast.makeText(getApplicationContext(), "Verification email has send to your registered email. !!", Toast.LENGTH_SHORT).show();

//                                                                String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobileno;
//                                                                Intent b = new Intent(Registration.this, Login.class);
//                                                                b.putExtra("phonenumber", phonenumber);
//                                                                startActivity(b);

                                                            }
                                                        });
                                                        AlertDialog alert = builder.create();
                                                        alert.show();

                                                    } else {
                                                        mDialog.dismiss();
                                                        Toast.makeText(SignUp.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                    }
                                                }
                                            });


                                        } else {
                                            mDialog.dismiss();
                                            Toast.makeText(SignUp.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    //Check in the database is there any user associated with  this email
//                    if (!sqliteHelper.isEmailExists(Email)) {
//
//                        //Email does not exist now add new user to database
//                        sqliteHelper.addUser(new User(null, UserName, Email, Password));
//                        Toast.makeText(getApplicationContext(), "User created successfully! Please Login ", Toast.LENGTH_SHORT).show();
//                        finish();
//                        startActivity(new Intent(getApplicationContext(), SignIn.class));
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "User already exists with same email  ", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_SHORT).show();
//                }
                }

            }
        });

    }


    //******************************FUNCTIONS TO ADD DATA'S TO FIREBASE*************************
    public void addUsers(String username, String Email, String Password, String City) {

        Map map = new HashMap();
        map.put("name", username);
        map.put("password", Password);
        map.put("city", City);

        databaseReference.child("Users").child(useridd).setValue(map);
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        String loc = City;
        edit.putString("location", loc);
        edit.commit();


    }

    public void addPasswords(String passwords) {

        Passwords pass = new Passwords(passwords);
        databaseReference.child("Passwords").child(useridd).setValue(pass);

    }

}