package com.cibobo.wooooo.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cibobo.wooooo.model.UserData;
import com.cibobo.wooooo.asynctasks.UserVerification;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.slave.R;

public class LoginActivity extends ActionBarActivity {
    private Context context;
    private EditText userNameEditText;
    private EditText passWordEditText;

    private CheckBox savePassCheckBox;

    private Button loginButton;

    private SharedPreferences loginDataSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        //Create a Shared Preference. The login data will be saved after verification of User
        loginDataSharedPref = this.getSharedPreferences(getString(R.string.login_data_preference), Context.MODE_PRIVATE);

        userNameEditText = (EditText)this.findViewById(R.id.editTextUserName);
        passWordEditText = (EditText)this.findViewById(R.id.editTextPassword);
        savePassCheckBox = (CheckBox)this.findViewById(R.id.checkBoxSavePass);
        loginButton = (Button)this.findViewById(R.id.buttonLogin);

        //Set the user name as in the last login
        String savedUserName = loginDataSharedPref.getString(this.getString(R.string.login_username),"");
        userNameEditText.setText(savedUserName);

        //If "save password" is selected by the user in last login, set the password with saved value.
        boolean isPassSaved = loginDataSharedPref.getBoolean(this.getString(R.string.login_isPassSaved), false);
        if(isPassSaved){
            String savedPassword = loginDataSharedPref.getString(this.getString(R.string.login_password), "");
            passWordEditText.setText(savedPassword);
            savePassCheckBox.setSelected(true);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userNameEditText.getText().toString();
                String passWord = passWordEditText.getText().toString();

                //Check whether the password should be saved
                boolean isPassSaved = savePassCheckBox.isChecked();
                //Save the selection of the user into shared preference
                SharedPreferences.Editor editor = loginDataSharedPref.edit();
                editor.putBoolean(context.getString(R.string.login_isPassSaved), isPassSaved);
                editor.commit();

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

    @Override
    protected void onDestroy() {
        XMPPInstantMessageService.getInstance().disconnection();
        super.onDestroy();
    }
}
