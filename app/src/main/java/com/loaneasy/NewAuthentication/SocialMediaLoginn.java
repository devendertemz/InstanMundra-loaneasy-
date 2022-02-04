package com.loaneasy.NewAuthentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.irozon.sneaker.Sneaker;
import com.loaneasy.Beans.ContactListBeans;
import com.loaneasy.ContactDTO;
import com.loaneasy.HomeActivity;
import com.loaneasy.R;
import com.loaneasy.ViewPresenter.ModalRepo.LoginRepo;
import com.loaneasy.ViewPresenter.ModelReq.LoginRequest;
import com.loaneasy.ViewPresenter.ModelReq.SignUpBody;
import com.loaneasy.ViewPresenter.NewUserLoginPresenter;
import com.loaneasy.ViewPresenter.NewUserSignUpPresenter;
import com.loaneasy.login_signup.SocialMediaLogin;
import com.loaneasy.new_user_details.UserProfile;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import com.rjesture.startupkit.AppTools;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;

public class SocialMediaLoginn extends AppCompatActivity  implements  GoogleApiClient.OnConnectionFailedListener  , NewUserSignUpPresenter.NewUserSignUpView {




    UserSharedPreference sharedPreference;
    private String getPhoneNo, getName;
    CallbackManager callbackManager;
    //ImageView fbLogin;
    LoginButton loginButtonFacebook;
    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_SIGN_IN_CODE = 9001;
    private static final String TAG = SocialMediaLogin.class.getSimpleName();

    private ArrayList<ContactListBeans> contactLists = new ArrayList<ContactListBeans>();
    NewUserSignUpPresenter presenter;

    Intent intent;
    String Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media_login2);

        presenter=new NewUserSignUpPresenter(this);
        sharedPreference = new UserSharedPreference(this);


        intent = getIntent();
        if (intent != null) {
            Number = intent.getStringExtra("number");
            Log.e("keyyyy", Number );


        }



        printHashKey();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(SocialMediaLoginn.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button);
        loginButtonFacebook.setReadPermissions(Arrays.asList(
                "public_profile", "user_photos", "email", "user_birthday", "user_friends", "public_profile", "user_hometown",
                "user_location"));

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String socialMediaId = object.getString("id");
                                    String socialEmail = object.getString("email");
                                    String socialMediaType = "Facebook";
                                    String socialName = object.getString("name");
                                    URL imageURL = new URL("https://graph.facebook.com/" + socialMediaId + "/picture?type=large");

                                    Log.i("imageURL", "-->" + imageURL.toString());
                                    Log.i("facebook id", "-->" + socialMediaId);
                                    Log.i("email", "-->" + socialEmail);
                                    Log.i("Name", "-->" + socialName);


                                    sharedPreference.setSignFlag(2);
                                    sharedPreference.setSocialMediaId(socialMediaId);
                                    sharedPreference.setUserEmail(socialEmail);
                                    sharedPreference.setSocialMediaType(socialMediaType);
                                    sharedPreference.setSocialMediaType(socialMediaType);
                                    sharedPreference.setUserSocialName(socialName);
                                    sharedPreference.setProfilePic(imageURL.toString());

                                    new SendingContactDetails().execute();

                                    // callFbGet();
                                   /* Intent intent = new Intent(getApplicationContext(), PersonalDetailsActivity.class);
                                    startActivity(intent);
                                    finish();*/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                // 01/31/1980 format
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }






    public void onSignUpClick(View view) {


        Intent in = new Intent(SocialMediaLoginn.this, ForgetPasswordActivity.class);
        in.putExtra("key", "Signup");
        startActivity(in);

    }

    public void onLoginWithOTPClick(View view) {


        Intent in = new Intent(SocialMediaLoginn.this, ForgetPasswordActivity.class);
        in.putExtra("key", "LoginWithOTP");
        startActivity(in);


    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());


        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = "not available";
            if (acct.getPhotoUrl() != null)
                personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String id = acct.getId();

            Log.e(TAG, "id: " + id);

            //Toast.makeText(getApplicationContext(), "" + id, Toast.LENGTH_SHORT).show();

            String socialMediaType = "Google";
            sharedPreference.setSocialMediaId(id);
            sharedPreference.setUserEmail(email);
            sharedPreference.setSocialMediaType(socialMediaType);
            sharedPreference.setUserSocialName(personName);
            sharedPreference.setProfilePic(personPhotoUrl);
            sharedPreference.setSignFlag(2);

            SignUpBody signUpBody = new SignUpBody(personName,email,Number,"Password");

            presenter.NewUserSignUp(SocialMediaLoginn.this ,signUpBody);

           /* Intent intent = new Intent(getApplicationContext(), PersonalDetailsActivity.class);
            startActivity(intent);
            finish();*/



        //    new SendingContactDetails().execute();


          /*  Toast.makeText(getApplicationContext(), "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl, Toast.LENGTH_SHORT).show();

*/
            Log.e("Loginnnnn", "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

        } else {

            Log.d("signInResult:=", result.toString());
            Log.d("signInResult:=", String.valueOf(result.getStatus()));

            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }


    public void OnGoogleLogin(View view) {

        AppTools.showRequestDialog(this);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE);

    }

    public void OnFbLogin(View view) {
        loginButtonFacebook.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppTools.hideDialog();

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Not signed with google", Toast.LENGTH_SHORT).show();
    }

    private List<ContactDTO> getAllContacts() {

        if (!contactLists.isEmpty())
            contactLists.clear();

        String userId = sharedPreference.getUserId();

        List<ContactDTO> ret = new ArrayList<ContactDTO>();

        // Get all raw contacts id list.
        List<Integer> rawContactsIdList = getRawContactsIdList();

        int contactListSize = rawContactsIdList.size();

        ContentResolver contentResolver = getContentResolver();

        // Loop in the raw contacts list.
        for (int i = 0; i < contactListSize; i++) {
            // Get the raw contact id.

            Integer rawContactId = rawContactsIdList.get(i);

            //Log.d(TAG_ANDROID_CONTACTS, "raw contact id : " + rawContactId.intValue());

            // Data content uri (access data table. )
            Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

            // Build query columns name array.
            List<String> queryColumnList = new ArrayList<String>();

            // ContactsContract.Data.CONTACT_ID = "contact_id";
            queryColumnList.add(ContactsContract.Data.CONTACT_ID);

            // ContactsContract.Data.MIMETYPE = "mimetype";
            queryColumnList.add(ContactsContract.Data.MIMETYPE);

            queryColumnList.add(ContactsContract.Data.DATA1);
            queryColumnList.add(ContactsContract.Data.DATA2);
            queryColumnList.add(ContactsContract.Data.DATA3);
            queryColumnList.add(ContactsContract.Data.DATA4);
            queryColumnList.add(ContactsContract.Data.DATA5);
            queryColumnList.add(ContactsContract.Data.DATA6);
            queryColumnList.add(ContactsContract.Data.DATA7);
            queryColumnList.add(ContactsContract.Data.DATA8);
            queryColumnList.add(ContactsContract.Data.DATA9);
            queryColumnList.add(ContactsContract.Data.DATA10);
            queryColumnList.add(ContactsContract.Data.DATA11);
            queryColumnList.add(ContactsContract.Data.DATA12);
            queryColumnList.add(ContactsContract.Data.DATA13);
            queryColumnList.add(ContactsContract.Data.DATA14);
            queryColumnList.add(ContactsContract.Data.DATA15);

            // Translate column name list to array.
            String queryColumnArr[] = queryColumnList.toArray(new String[queryColumnList.size()]);

            // Build query condition string. Query rows by contact id.
            StringBuffer whereClauseBuf = new StringBuffer();
            whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
            whereClauseBuf.append("=");
            whereClauseBuf.append(rawContactId);


            try {


                // Query data table and return related contact data.
                Cursor cursor = contentResolver.query(dataContentUri, queryColumnArr, whereClauseBuf.toString(), null, null);

            /* If this cursor return database table row data.
               If do not check cursor.getCount() then it will throw error
               android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0.
               */
                if (cursor != null && cursor.getCount() > 0) {
                    StringBuffer lineBuf = new StringBuffer();
                    cursor.moveToFirst();
                    lineBuf.append("Raw Contact Id:");
                    lineBuf.append(rawContactId);

              /*  long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                lineBuf.append(", Contact Id:");
                lineBuf.append(contactId);*/

                    do {
                        // First get mimetype column value.
                        String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                        //lineBuf.append(", MimeType:");
                        //lineBuf.append(mimeType);

                        List<String> dataValueList = getColumnValueByMimetype(cursor, mimeType);
                        int dataValueListSize = dataValueList.size();
                        for (int j = 0; j < dataValueListSize; j++) {
                            String dataValue = dataValueList.get(j);
                            lineBuf.append(", ");
                            lineBuf.append(dataValue);


                        }

                    } while (cursor.moveToNext());

                    //Log.d(TAG_ANDROID_CONTACTS, lineBuf.toString());

                    //myList.add(lineBuf.toString());
                    String data = lineBuf.toString();
                    String[] ary = data.split(", ");
                    // Log.i("rr","--->"+i);

                    // String srs = Arrays.toString(ary);
                    //Log.i("rr","--->"+srs);


                    for (int j = 0; j < ary.length; j++) {
                        //Log.i("rr","--->"+ary[j]);

                        if (ary[j].contains("Display Name")) {
                            Log.i("rr", "--->" + ary[j]);
                            getName = ary[j];

                        }
                        if (ary[j].contains("Phone Number")) {
                            //Log.i("rr","--->"+ary[j]);
                            //phoneNo.add(anAry);
                            //phoneNo[i] = ary[j];

                            getPhoneNo = ary[j];
                        }

                    }


                    ContactListBeans contactListBeans = new ContactListBeans(userId, getPhoneNo, getName);
                    contactLists.add(contactListBeans);


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

      //  Toast.makeText(this, "--" + contactLists.size(), Toast.LENGTH_SHORT).show();
        //  progressBar.dismiss();
        return ret;
    }


    /*
     *  Get phone type related string format value.
     * */
    private String getPhoneTypeString(int dataType) {
        String ret = "";

        if (ContactsContract.CommonDataKinds.Phone.TYPE_HOME == dataType) {
            ret = "HomeActivity";
        } else if (ContactsContract.CommonDataKinds.Phone.TYPE_WORK == dataType) {
            ret = "Work";
        } else if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == dataType) {
            ret = "Mobile";
        }
        return ret;
    }

    /*
     *  Return data column value by mimetype column value.
     *  Because for each mimetype there has not only one related value,
     *  such as Organization.CONTENT_ITEM_TYPE need return company, department, title, job description etc.
     *  So the return is a list string, each string for one column value.
     * */
    private List<String> getColumnValueByMimetype(Cursor cursor, String mimeType) {
        List<String> ret = new ArrayList<String>();

        switch (mimeType) {

            case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                // Im.PROTOCOL == data5
                String imProtocol = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                // Im.DATA == data1
                String imId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));

                ret.add("IM Protocol : " + imProtocol);
                ret.add("IM ID : " + imId);
                break;
            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int phoneTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String phoneTypeStr = getPhoneTypeString(phoneTypeInt);

                ret.add("Phone Number:" + phoneNumber);
                break;

            // Get sip address.
            case ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE:

            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                String givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                String familyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));

                ret.add("Display Name:" + displayName);
                break;

            case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:

        }

        return ret;
    }

    // Return all raw_contacts _id in a list.
    private List<Integer> getRawContactsIdList() {
        List<Integer> ret = new ArrayList<Integer>();

        ContentResolver contentResolver = getContentResolver();

        // Row contacts content uri( access raw_contacts table. ).
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;
        // Return _id column in contacts raw_contacts table.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};
        // Query raw_contacts table and return raw_contacts table _id.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                int idColumnIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                int rawContactsId = cursor.getInt(idColumnIndex);
                ret.add(new Integer(rawContactsId));
            } while (cursor.moveToNext());
        }

        assert cursor != null;
        cursor.close();

        return ret;
    }

    @Override
    public void onNewUserSignUpError(String message) {
        Sneaker.with(this)
                .setTitle(message)
                .setMessage("")
                .sneakError();
    }

    @Override
    public void onNewUserSignUpSuccess(ResponseBody responseBody, String message) {

        String response=null,status = null,msg = null;
        JSONObject jsonObject ;

        if (message.equalsIgnoreCase("ok")) {

            try {
                response = responseBody.string();
                 jsonObject = new JSONObject(response);
                status = jsonObject.getString("status");
                msg = jsonObject.getString("msg");

                if (status.equalsIgnoreCase("true")) {

                    JSONObject jsonObject1=jsonObject.getJSONObject("response");
                    sharedPreference.setUserId(jsonObject1.getString("user_id"));
                  //  Toast.makeText(SocialMediaLoginn.this, jsonObject1.getString("user_id") + "", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                    startActivity(intent);
                    finish();





                } else {
                    Sneaker.with(this)
                            .setTitle(msg)
                            .setMessage("")
                            .sneakError();

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void showHideProgress(boolean isShow) {
        if (isShow) {
            AppTools.showRequestDialog(this);


        } else {
            AppTools.hideDialog();

        }
    }

    @Override
    public void onNewUserSignUpFailure(Throwable t) {
        Sneaker.with(this)
                .setTitle(t.getLocalizedMessage())
                .setMessage("")
                .sneakError();
    }


    @SuppressLint("StaticFieldLeak")
    public class loadAllContacts extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected Void doInBackground(Void... voids) {

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
            String lastnumber = "0", userId = sharedPreference.getUserId();

            if (!contactLists.isEmpty()) {
                contactLists.clear();
            }
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String number = null;
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]
                                {id}, null);
                        while (cursor.moveToNext()) {
                            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                            Log.e("lastnumber ", lastnumber);
                            Log.e("number", number);

                            if (number.equals(lastnumber)) {

                            } else {
                                lastnumber = number;

                                Log.e("lastnumber ", lastnumber);
                                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                switch (type) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        Log.e("Not Inserted", "Not inserted");
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:

                                        ContactListBeans contactListBeans = new ContactListBeans(userId,
                                                lastnumber, name);
                                        contactLists.add(contactListBeans);

                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        Log.e("Not Inserted", "Not inserted");
                                        break;
                                }

                            }

                        }
                        cursor.close();
                    }

                }
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SocialMediaLoginn.this);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class SendingContactDetails extends AsyncTask<Object, Void, ArrayList<String>> {
        ProgressDialog dialog;
        String responseBody;

        @Override
        protected ArrayList<String> doInBackground(Object... objects) {
            ArrayList<NameValuePair> postVars = new ArrayList<NameValuePair>();

            //Add a 1st Post Value called JSON with String value of JSON inside
            //This is first and last post value sent because server side will decode the JSON and get other vars from it.
            //postVars.add(new BasicNameValuePair("JSON", EverythingJSON.toString()));


            String json = new Gson().toJson(contactLists);
            Log.i("GSON", "->" + json);
            postVars.add(new BasicNameValuePair("contact_list", json));
            //postVars.add(new BasicNameValuePair("contact_name[]", name.toString()));

            //Declare and Initialize Http Clients and Http Posts
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Utility.BASE_URL + "/getContactDetails");

            //Format it to be sent
            try {
                httppost.setEntity(new UrlEncodedFormEntity(postVars));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            /* Send request and Get the Response Back */
            try {
                HttpResponse response = httpclient.execute(httppost);
                responseBody = EntityUtils.toString(response.getEntity());
                Log.i("response", "-->" + responseBody);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.v("MAD", "Error sending... ");
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("MAD", "Error sending... ");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SocialMediaLoginn.this);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            /*if (SocialMediaLogin.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }*/
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(intent);
            finish();
            /*Intent intent = new Intent(getApplicationContext(), PersonalDetailsActivity.class);
            startActivity(intent);*/
        }
    }


    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.loaneasy",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

                Log.d("KeyHash:", String.valueOf(md));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
            e.getMessage();
        }
    }


}

