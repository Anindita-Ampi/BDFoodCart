package com.example.lenovo.bdfoodcart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class location extends AppCompatActivity implements View.OnClickListener{

    private Button btnc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        btnc = (Button) findViewById(R.id.btnc);
        btnc.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view==btnc) {
            final Intent in1 = new Intent(this, CustActivity.class);
            startActivity(in1);
        }
    }
}
