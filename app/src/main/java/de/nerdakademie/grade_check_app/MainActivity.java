package de.nerdakademie.grade_check_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPreferences.getBoolean("darkmode",false)){
            this.setTheme(R.style.AppThemeDark_NoActionBar);
        }else{
            this.setTheme(R.style.AppTheme_NoActionBar);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();
    }

    private void initUI(){
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.dark_mode) {
        //    return true;
        //}
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            return true;
        }
        else if (id == R.id.close_app) {
            MainActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        final String user = username.getText().toString();
        final String pass = password.getText().toString();


        Intent GradeList = new Intent(MainActivity.this, GradeList.class);
        GradeList.putExtra("user",user);
        GradeList.putExtra("pass",pass);

        startActivity(GradeList);
    }
}
