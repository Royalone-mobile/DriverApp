package com.sawatruck.driver.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.controller.RegionCodeAPI;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomEditText;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/20/2017.
 */


public class ActivitySignIn extends BaseActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    @Bind(R.id.edit_email) CustomEditText editEmail;
    @Bind(R.id.edit_password) CustomEditText editPassword;
    @Bind(R.id.btn_signin) View btnSignIn;
    @Bind(R.id.btn_signup) View btnSignUp;
    @Bind(R.id.btn_signin_google) View btnSignInGoogle;
    @Bind(R.id.btn_signin_facebook) View btnSignInFacebook;
    @Bind(R.id.btn_forgot_password) View btnForgotPassword;
    private static final int RC_SIGN_IN = 100;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private boolean signedInUser;

    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;

    private ActivitySignIn _instance;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_signin,null);
        ButterKnife.bind(this, view);
        _instance = this;

        editEmail.setOnClickListener(this);
        editPassword.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnSignInGoogle.setOnClickListener(this);
        btnSignInFacebook.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        setRegionCode();
        Scope scope = new Scope(Constant.GOOGLE_API_SCOPE_URL);




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("738995436229-shkkc8p17cdk7a7qqb6fg2c256rn162b.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        setAppTitle(getString(R.string.title_signin));
        showNavHome(false);

        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResult.getAccessToken().getUserId();
                AccessToken accessToken = loginResult.getAccessToken();
                final Bundle[] bFacebookData = new Bundle[1];
                Log.d(TAG, "Facebook success");
                Toast.makeText(context, Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_LONG).show();
                Log.i(TAG, Profile.getCurrentProfile().getName());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id){
            case R.id.btn_signin:
                signIn();
                break;
            case R.id.btn_signup:
                intent = new Intent(this, ActivitySignUp.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_signin_google:
                googlePlusLogin();
                break;
            case R.id.btn_signin_facebook:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.btn_forgot_password:
                alertForgotPassword();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(signedInUser ){
        }
        signedInUser = false;
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = connectionResult;

            if (signedInUser) {
                resolveSignInError();
                signedInUser = false;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Status ss = result.getStatus();
        GoogleSignInAccount acct = result.getSignInAccount();
        if (result.isSuccess()) {
            final String id = acct.getId();
            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();

        } else {

        }
    }

    private void googlePlusLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void alertForgotPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View viewForgotPassword = getLayoutInflater().inflate(R.layout.dialog_forgot_password,null);


        final CustomEditText editEmail = (CustomEditText)viewForgotPassword.findViewById(R.id.edit_email);

        final Button btnOk = (Button)viewForgotPassword.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewForgotPassword.findViewById(R.id.btn_cancel);

        builder.setView(viewForgotPassword);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUserName = editEmail.getText().toString();

                if(strUserName.length() == 0){
                    editEmail.setError(getString(R.string.error_enter_email));
                    editEmail.requestFocus();
                    return;
                }
                if(!Misc.isEmailValid(strUserName)){
                    editEmail.setError(getString(R.string.error_invalid_email));
                    editEmail.requestFocus();
                    return;
                }

                forgotPassword(strUserName);
                alertDialog.dismiss();
            }
        });
    }
    private void forgotPassword(String strUserName) {
        RequestParams params = new RequestParams();

        params.put("Email",strUserName);
        HttpUtil.getInstance().post(Constant.USER_FORGOTPASSWORD_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Misc.showResponseMessage(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }

    private void signIn(){
        RequestParams params = new RequestParams();
        String strUserName = editEmail.getText().toString();
        String strPassword = editPassword.getText().toString();

        if(strUserName.length() == 0){
            editEmail.setError(getString(R.string.error_enter_email));
            editEmail.requestFocus();
            return;
        }
        if(strPassword.length() == 0){
            editPassword.setError(getString(R.string.error_enter_password));
            editPassword.requestFocus();
            return;
        }

        if(!Misc.isEmailValid(strUserName)){
            editEmail.setError(getString(R.string.error_invalid_email));
            editEmail.requestFocus();
            return;
        }

        hideKeyboard();
        params.put("username", strUserName);
        params.put("password", strPassword);
        params.put("grant_type", "password");
        params.put("client_id", Constant.SAWATRUCK_CLIENT_ID);
        params.put("client_secret", Constant.SAWATRUCK_CLIENT_SECRET);

        String firebaseToken = AppSettings.with(context).getFirebaseToken();
        params.put("RegisterationToken", firebaseToken);

        HttpUtil.getInstance().post(Constant.USER_SIGNIN_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
//                paramString = StringUtil.escapeString(paramString);
                    User user = BaseApplication.getGson().fromJson(paramString, User.class);
                    if (user != null) {
                        UserManager.with(_instance).setUserType(1);
                        user.setToken("bearer " + user.getToken());
                        UserManager.with(_instance).setCurrentUser(user);
                        BaseApplication.getInstance().reconfigHub();
                        Intent intent = new Intent(_instance, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONObject jsonObject = new JSONObject(paramString);
                    Notice.show(jsonObject.getString("error_description"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setRegionCode(){
        RegionCodeAPI regionCodeAPI = RegionCodeAPI.getInstance();
        regionCodeAPI.setContext(this);
        regionCodeAPI.execute();
    }
}
