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
import com.cibobo.wooooo.provider.HandyStateProvider;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.slave.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends ActionBarActivity {
    private final String tag = "Login Activity";

    private Context context;
    private EditText userNameEditText;
    private EditText passWordEditText;

    //TODO: temporary solution for the partner input
    private EditText partnerNameEditText;

    private CheckBox savePassCheckBox;

    private Button loginButton;

    private SharedPreferences loginDataSharedPref;

    //Thread pool containing all necessary runnable, which is keep on active during the whole life of the APP
    /*
     * @message: the initialization of the runnable should Login from Begin. Otherwise for each time the user go back to the BeginActivity,
     * there will be a new runnable created. That duplicate the send and receive messages,
     */
    private ExecutorService executor;
    private final int MAX_THREAD_COUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        //Create thread pool to running the necessary task in other thread
        executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

        //Create a Shared Preference. The login data will be saved after verification of User
        loginDataSharedPref = this.getSharedPreferences(getString(R.string.login_data_preference), Context.MODE_PRIVATE);

        userNameEditText = (EditText)this.findViewById(R.id.editTextUserName);
        passWordEditText = (EditText)this.findViewById(R.id.editTextPassword);
        savePassCheckBox = (CheckBox)this.findViewById(R.id.checkBoxSavePass);
        //TODO: temporary solution for the partner input
        partnerNameEditText = (EditText) this.findViewById(R.id.editTextPartnerName);
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

        //TODO: temporary solution for the partner input
        String savedPartnerName = loginDataSharedPref.getString(this.getString(R.string.login_partnerName), "");
        partnerNameEditText.setText(savedPartnerName);

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

                //TODO: temporary solution for the partner input
                String partnerName = partnerNameEditText.getText().toString();
                editor.putString(context.getString(R.string.login_partnerName), partnerName);

                editor.putBoolean(context.getString(R.string.login_isPassSaved), isPassSaved);

                editor.commit();

                //Check the network connection.
                if(HandyStateProvider.getInstant(context).isNetworkConnected()) {
                    UserData userData = new UserData(userName, passWord);
                    UserVerification verification = new UserVerification(context, executor);
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
        //Shut down all thread in thread pool
        executor.shutdown();
        try {
            executor.awaitTermination((long)100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(tag, e.toString());
        }
        super.onDestroy();
    }
}
