package ca.xiaowei.chen2267127.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ca.xiaowei.chen2267127.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
EditText usernameText,emailText,passwordText,confirmPswText;
Button signUpBtn;
String emailValue,usernameValue,passwordValue,confirmPswValue;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();
    }
public void initialize(){
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.email);
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        confirmPswText = findViewById(R.id.confirm_password);
        signUpBtn = findViewById(R.id.signUpButton);
        signUpBtn.setOnClickListener(this);
}
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signUpButton){
            usernameValue = usernameText.getText().toString();
            emailValue = emailText.getText().toString();
            passwordValue = passwordText.getText().toString();
            confirmPswValue = confirmPswText.getText().toString();
            if(TextUtils.isEmpty(usernameValue)){
                Toast.makeText(this,"Enter username",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(emailValue)){
                Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(passwordValue)){
                Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(confirmPswValue)){
                Toast.makeText(this,"Enter confirm password",Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(SignUpActivity.this, "Account created.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }
}