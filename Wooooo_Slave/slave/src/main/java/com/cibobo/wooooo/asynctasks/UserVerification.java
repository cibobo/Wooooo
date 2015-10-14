package com.cibobo.wooooo.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cibobo.wooooo.model.UserData;
import com.cibobo.wooooo.activities.BeginActivity;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.service.connection.ConnectionService;
import com.cibobo.wooooo.slave.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Cibobo on 9/28/14.
 * User verification running in asynchronous task.
 */
public class UserVerification extends AsyncTask<UserData, Integer, Boolean>{
    private final String tag = "User Verification";

    //Context of the current activity, which will be used to star the new activity.
    private Context context;

    private ConnectionService connectionService;

    private ExecutorService executor;

    /*
    Constructor, which can get the context from the current activity.
     */
    public UserVerification(Context context, ExecutorService executorService){
        this.context = context;
        this.executor = executorService;
    }

    @Override
    protected Boolean doInBackground(UserData... users) {
        UserData currentUser = users[0];

        connectionService = XMPPInstantMessageService.getInstance();
        boolean isConnectSuccessful = connectionService.connect(currentUser);

        if(isConnectSuccessful){
            this.updateUserAccount(currentUser);
//            connectionService.sendMessage();
            return true;
        } else {
            //@Message: Toast should not be used here to show the XMPP error.
            // Otherwise you will get:
            // java.lang.RuntimeException: An error occured while executing doInBackground()
            //Toast.makeText(context, context.getString(R.string.login_error_XMPP), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    protected void onPostExecute(Boolean isConnectSucceed){
        if(isConnectSucceed){
            /*
             * Start the MessageReceiveRunnable.
             * This runnable will always running in the background during the whole life of the APP
             */
            executor.execute(XMPPInstantMessageService.getInstance().getMessageReceiveRunnable());

            Intent mainIntent = new Intent(context, BeginActivity.class);
            context.startActivity(mainIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.login_error_user), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save login data into the Shared Preference
     * @param currentUser the current login user, the info of which needs to be saved in shared preferences
     */
    private void updateUserAccount(UserData currentUser){
        String username = currentUser.getUserName();
        String password = currentUser.getPassWord();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.login_data_preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.login_username), username);
        //Check whether the password should be saved or not in the shared preference
        //Default situation is not save
        if(sharedPref.getBoolean(context.getString(R.string.login_isPassSaved), false)){
            editor.putString(context.getString(R.string.login_password), password);
        }
        editor.commit();
    }
}
