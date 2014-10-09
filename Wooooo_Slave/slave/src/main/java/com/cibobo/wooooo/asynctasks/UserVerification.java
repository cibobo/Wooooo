package com.cibobo.wooooo.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cibobo.wooooo.model.UserData;
import com.cibobo.wooooo.slave.BeginActivity;
import com.cibobo.wooooo.slave.R;

/**
 * Created by Cibobo on 9/28/14.
 * User verification running in asynchronous task.
 */
public class UserVerification extends AsyncTask<UserData, Integer, UserData>{
    //Context of the current activity, which will be used to star the new activity.
    private Context context;

    /*
    Constructor, which can get the context from the current activity.
     */
    public UserVerification(Context context){
        this.context = context;
    }

    @Override
    protected UserData doInBackground(UserData... users) {
        //TODO: Create verification functions to XMPP connection.
        UserData currentUser = users[0];
        if(currentUser.getUserName().equals("cibobo")){
            return currentUser;
        } else {
            return null;
        }
    }

    protected void onPostExecute(UserData user){
        //TODO: If verification is successful, start MasterActivity
        if(user != null){
            Intent mainIntent = new Intent(context, BeginActivity.class);
            context.startActivity(mainIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.ErrorMessage_Login), Toast.LENGTH_SHORT).show();
        }
    }
}
