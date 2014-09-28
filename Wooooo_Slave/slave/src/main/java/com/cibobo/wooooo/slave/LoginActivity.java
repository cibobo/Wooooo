package com.cibobo.wooooo.slave;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cibobo.wooooo.Model.UserData;
import com.cibobo.wooooo.asynctasks.UserVerification;

public class LoginActivity extends ActionBarActivity {
    private Context context;
    private EditText userNameEditText;
    private EditText passWordEditText;

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave_login);

        context = this;

        userNameEditText = (EditText)this.findViewById(R.id.editTextUserName);
        passWordEditText = (EditText)this.findViewById(R.id.editTextPassword);

        loginButton = (Button)this.findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userNameEditText.getText().toString();
                String passWord = passWordEditText.getText().toString();

                UserData userData = new UserData(userName,passWord);
                UserVerification verification = new UserVerification(context);
                verification.execute(userData);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.slave_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
