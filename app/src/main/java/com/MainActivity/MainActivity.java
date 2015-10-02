package com.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.Data.MyServerData;
import com.QuizActivity.QuizActivity;
import com.example.andrei.multiplechoicequestions.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_menu);
        Button btnGrile = (Button)findViewById(R.id.btnGrile);
        btnGrile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Quiz = new Intent(getApplicationContext(), QuizActivity.class);
                MyServerData.getInstance().setTestState("inProgress");
                startActivity(Quiz);
            }
        });
    }
}
