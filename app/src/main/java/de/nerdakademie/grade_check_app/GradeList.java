package de.nerdakademie.grade_check_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import java.util.Map;

public class GradeList extends AppCompatActivity {

    String user;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            user = extras.getString("user","");
            pass = extras.getString("pass","");
        }

        setContentView(R.layout.grade_main);
        initUI();
    }

    private void initUI(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                GradesCheckContainer checker = new GradesCheckContainer(user, pass);
                Map<String,String> grades = checker.getGrades();
                Log.d("LUL", grades.toString());
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grademenu_main, menu);
        return true;
    }
}
