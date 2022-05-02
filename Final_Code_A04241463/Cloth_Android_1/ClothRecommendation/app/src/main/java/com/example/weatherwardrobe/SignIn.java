package com.example.weatherwardrobe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    EditText email;
    EditText password;
    FirebaseAuth FAuth;
    EditText city;
    private ImageView top_curve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        final LoginSqliteHelper sqliteHelper = new LoginSqliteHelper(this);

        findViewById(R.id.alreadyHaveAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });

        city = findViewById(R.id.city);


        FAuth = FirebaseAuth.getInstance();


        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
        findViewById(R.id.top_curve).startAnimation(top_curve_anim);
        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edittext_anim);
        email.startAnimation(top_curve_anim);
        password.startAnimation(top_curve_anim);
        findViewById(R.id.city).startAnimation(top_curve_anim);
        findViewById(R.id.logo).startAnimation(editText_anim);
        findViewById(R.id.appName).startAnimation(editText_anim);
        findViewById(R.id.forgotPassword).startAnimation(editText_anim);


        findViewById(R.id.logIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check user input is correct or not
                if (validate()) {


                    if (city.getText().toString().isEmpty() == false) {
                        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString("location", city.getText().toString());
                        edit.commit();
                    }

                    //Get values from EditText fields
                    String Email = email.getText().toString();
                    String Password = password.getText().toString();


                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setCancelable(false);
                    mDialog.setMessage("Logging in...");
                    mDialog.show();
                    FAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                mDialog.dismiss();
                                if (FAuth.getCurrentUser().isEmailVerified()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                    Intent z = new Intent(SignIn.this, MainActivity.class);
                                    startActivity(z);
                                    finish();


                                } else {
                                    Toast.makeText(SignIn.this, "Please Verify your Email", Toast.LENGTH_LONG).show();
                                    ;
                                }

                            } else {

                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

//                    //Authenticate user
//                    User currentUser = sqliteHelper.Authenticate(new User(null, null, Email, Password));
//
//                    //Check Authentication is successful or not
//                    if (currentUser != null) {
//                        Toast.makeText(getApplicationContext(), "Successfully Logged in!", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Failed to log in , please try again", Toast.LENGTH_LONG).show();
//                    }
                }

            }
        });


        findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = email.getText().toString();

                if (Email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email Address", Toast.LENGTH_LONG).show();

                } else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(Email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Reset password link has sent to your email address", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });


    }

    //This method is used to validate input given by user
    public boolean validate() {
        boolean valid = false;

        //Get values from EditText fields
        String Email = email.getText().toString();
        String Password = password.getText().toString();

        //Handling validation for Email field
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            valid = false;
            email.setError("Please enter valid email!");
        } else {
            valid = true;
            email.setError(null);
        }

        //Handling validation for Password field
        if (Password.isEmpty()) {
            valid = false;
            password.setError("Please enter valid password!");
        }

        return valid;
    }

}