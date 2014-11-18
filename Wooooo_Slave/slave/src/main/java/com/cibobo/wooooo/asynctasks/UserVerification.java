package com.cibobo.wooooo.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cibobo.wooooo.model.UserData;
import com.cibobo.wooooo.activities.BeginActivity;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageReceiveThread;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.service.connection.ConnectionService;
import com.cibobo.wooooo.service.connection.ConnectionServiceFactory;
import com.cibobo.wooooo.slave.R;

/**
 * Created by Cibobo on 9/28/14.
 * User verification running in asynchronous task.
 */
public class UserVerification extends AsyncTask<UserData, Integer, UserData>{
    //Context of the current activity, which will be used to star the new activity.
    private Context context;

    private ConnectionService connectionService;
    /*
    Constructor, which can get the context from the current activity.
     */
    public UserVerification(Context context){
        this.context = context;
    }

    @Override
    protected UserData doInBackground(UserData... users) {
        UserData currentUser = users[0];
/*
        if(currentUser.getUserName().equals("cibobo")){
            return currentUser;
        } else {
            return null;
        }
*/

        connectionService = XMPPInstantMessageService.getInstance();
        boolean isConnectSuccessful = connectionService.connect(currentUser.getUserName(), currentUser.getPassWord());

        if(isConnectSuccessful){
            this.updateUserAccount(currentUser.getUserName(), currentUser.getPassWord());
//            connectionService.sendMessage();
            return currentUser;
        } else {
            return null;
        }
    }

    protected void onPostExecute(UserData user){
        //TODO: should add the judgement about the network situation
        if(user != null){
            Intent mainIntent = new Intent(context, BeginActivity.class);
            context.startActivity(mainIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.ErrorMessage_Login), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save login data into the Shared Preference
     * @param username
     * @param password
     */
    private void updateUserAccount(String username, String password){
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
