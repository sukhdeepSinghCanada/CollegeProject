package com.example.simpleauthentication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    TextView email;
    Button closeButton;
    String emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        closeButton = findViewById(R.id.closeButton);
        emailText = getIntent().getStringExtra("email");
        getView();
        setListeners();
        email.setText(emailText);
    }

    private void setListeners() {
        closeButton.setOnClickListener(this);
    }

    private void getView() {
        email = findViewById(R.id.email);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                finish();
                break;
        }
    }
}
