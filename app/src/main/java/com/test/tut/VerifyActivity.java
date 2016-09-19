package com.test.tut;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editCode;
    private String code;
    private TextView error;
    private static final int RESULT_VERIFIED = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        editCode = (EditText) findViewById(R.id.verify_code);
        Button submit = (Button) findViewById(R.id.verify_btn);
        error = (TextView) findViewById(R.id.error_text);
        code = getIntent().getStringExtra("code");
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(editCode.getText().toString().equals(code)){
            error.setText("");
            setResult(RESULT_VERIFIED);
            finish();
        }else{
            error.setText("Code is incorrect. Please try again.");
        }
    }
}
