package ca.xiaowei.chen2267127.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ca.xiaowei.chen2267127.Activity.LoginActivity;
import ca.xiaowei.chen2267127.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button goLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goLoginBtn = findViewById(R.id.goLogin);
        goLoginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.goLogin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}