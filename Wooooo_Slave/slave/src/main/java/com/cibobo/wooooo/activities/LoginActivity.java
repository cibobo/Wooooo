package com.cibobo.wooooo.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.Preference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cibobo.wooooo.asynctasks.MessageServiceDisconnection;
import com.cibobo.wooooo.model.UserData;
import com.cibobo.wooooo.asynctasks.UserVerification;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.slave.R;

public class LoginActivity extends ActionBarActivity {
    private final String tag = "Login Activity";

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

        //Set login button as disabled, if the username is empty.
        loginButton.setEnabled(false);
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.toString().length() > 0) {
                    loginButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    loginButton.setEnabled(false);
                    Toast.makeText(context, getString(R.string.login_error_user_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Set the user name as in the last login
        String savedUserName = loginDataSharedPref.getString(this.getString(R.string.login_username),"");
        userNameEditText.setText(savedUserName);

        //If "save password" is selected by the user in last login, set the password with saved value.
        boolean isPassSaved = loginDataSharedPref.getBoolean(this.getString(R.string.login_isPassSaved), false);

        if(isPassSaved){
            String savedPassword = loginDataSharedPref.getString(this.getString(R.string.login_password), "");
            passWordEditText.setText(savedPassword);
            savePassCheckBox.setChecked(true);
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

                //Check the network connection.
                ConnectivityManager connectivityManager = (ConnectivityManager) context.
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()) {
                    UserData userData = new UserData(userName, passWord);
                    UserVerification verification = new UserVerification(context);
                    verification.execute(userData);
                } else {
                    Toast.makeText(context, getString(R.string.login_error_network), Toast.LENGTH_SHORT).show();
                }
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
        //XMPPInstantMessageService.getInstance().disconnection();
        super.onDestroy();
    }
}
