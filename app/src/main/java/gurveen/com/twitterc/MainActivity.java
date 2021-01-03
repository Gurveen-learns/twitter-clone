package gurveen.com.twitterc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    EditText emailEditText, passwordEditText;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        if (mAuth.getCurrentUser() != null) {
            logIn();
        }

    }

    public void loginClicked(View view) {
        final String emailInput = emailEditText.getText().toString();
        final String passwordInput = passwordEditText.getText().toString();

        //login the user
        mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()) {
                    //Login successful move to next activity
                    logIn();
                } else {
                    //Login Failed signup instead
                    //signup a new user
                    mAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //create user successful moveON
                                uid = task.getResult().getUser().getUid();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(uid)
                                        .child("email").setValue(emailInput, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                       if (databaseError != null){
                                           Log.d("Database Error",databaseError.getMessage());
                                       }else {Log.d("Database","Successful");}

                                    }
                                });
                                logIn();
                            } else {
                                //login failed
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    public void logIn() {
        Intent intent = new Intent(MainActivity.this, FollowActivity.class);
        intent.putExtra("currentUid",uid);
        startActivity(intent);
    }
}


