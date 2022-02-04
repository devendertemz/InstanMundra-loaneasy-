package com.loaneasy.login_signup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.loaneasy.Beans.ContactListBeans;
import com.loaneasy.ContactDTO;
import com.loaneasy.R;
import com.loaneasy.new_user_details.UserProfile;
import com.loaneasy.user_details_new.PersonalDetailsActivity;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
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

public class SocialMediaLogin extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private ArrayList<ContactListBeans> contactLists = new ArrayList<ContactListBeans>();
    private String getPhoneNo, getName;
    CallbackManager callbackManager;
    //ImageView fbLogin;
    LoginButton loginButtonFacebook;
    private SignInButton btnSignIn;
    ProgressDialog progressBar;
    ArrayList<String> alTaggedPics;
    ArrayList<String> alUploadedPics;

    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_SIGN_IN_CODE = 9001;
    private TextView socialTextSignIn, socialText, tv_ok;
    private static final String TAG = SocialMediaLogin.class.getSimpleName();
    private UserSharedPreference sharedPreference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        /*************To generate facebook Hashcode for login***********/
        //printHashKey();


        sharedPreference = new UserSharedPreference(this);
        alTaggedPics = new ArrayList<>();
        alUploadedPics = new ArrayList<>();

        setContentView(R.layout.activity_social_media_login);


        new loadAllContacts().execute();

        if (sharedPreference.getExistCustomerFlag() == 1)
            showWelcomePopUp();

        btnSignIn = (SignInButton) findViewById(R.id.btGLogin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

    /*  mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
              .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
              .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
              .addOnConnectionFailedListener(this)
              .build();*/

        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(SocialMediaLogin.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        loginButtonFacebook = (LoginButton) findViewById(R.id.btFbLogin);
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

    private void showWelcomePopUp() {
        final Dialog dialog = new Dialog(SocialMediaLogin.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);
        tv_ok = dialog.findViewById(R.id.start);

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE);
    }

    private void setUpFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
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
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
            e.getMessage();
        }
    }



    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = "not available";
            if (acct.getPhotoUrl()!=null)
                personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String id = acct.getId();

            Log.e(TAG, "id: " + id);

            String socialMediaType = "Google";

            sharedPreference.setSocialMediaId(id);
            sharedPreference.setUserEmail(email);
            sharedPreference.setSocialMediaType(socialMediaType);
            sharedPreference.setUserSocialName(personName);
            sharedPreference.setProfilePic(personPhotoUrl);

            sharedPreference.setSignFlag(2);

            /*Intent intent = new Intent(getApplicationContext(), PersonalDetailsActivity.class);
            startActivity(intent);
            finish();*/

            new SendingContactDetails().execute();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    private void callFbGet() {

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + sharedPreference.getSocialMediaId() + "/photos/tagged",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("TAG111", "albumresponse---->" + response.toString());
                        String sRawResponse = response.getRawResponse();
                        String sAlbumId = "";
                        try {

                            JSONObject object = new JSONObject(sRawResponse);
                            JSONArray array = object.getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                alTaggedPics.add(data.getString("id"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + sharedPreference.getSocialMediaId() + "/photos/uploaded",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("TAG111", "albumresponse---->" + response.toString());
                        String sRawResponse = response.getRawResponse();
                        String sAlbumId = "";
                        try {

                            JSONObject object = new JSONObject(sRawResponse);
                            JSONArray array = object.getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                alUploadedPics.add(data.getString("id"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();


        String sarValues[] = {"address", "hometown", "languages", "sports", "inspirational_people", "favorite_athletes"};
        for (int i = 0; i < sarValues.length; i++) {
            getFBData(sarValues[i]);
        }
    }

    public void getFBData(String sValue) {

        Bundle paramsData = new Bundle();
        paramsData.putString("fields", sValue);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + sharedPreference.getSocialMediaId() + "/",
                paramsData,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        String sRawResponse = response.getRawResponse();
                        Log.d("TAG111", "-1->>>" + sRawResponse);
                    }
                }
        ).executeAsync();

    }

    public void getPhotos(int photoId) {
        Bundle params = new Bundle();
        params.putString("fields", "images");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + photoId,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        String sRawResponse = response.getRawResponse();
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


    /* Return all contacts and show each contact data in android monitor console as debug info. */
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

        Toast.makeText(this, "--"+contactLists.size(), Toast.LENGTH_SHORT).show();
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
            // Get email data.
           /* case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE :
                // Email.ADDRESS == data1
                String emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                // Email.TYPE == data2
                int emailType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                String emailTypeStr = getEmailTypeString(emailType);

                ret.add("Email Address : " + emailAddress);
                ret.add("Email Int Type : " + emailType);
                ret.add("Email String Type : " + emailTypeStr);
                break;*/

            // Get im data.
            case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                // Im.PROTOCOL == data5
                String imProtocol = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                // Im.DATA == data1
                String imId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));

                ret.add("IM Protocol : " + imProtocol);
                ret.add("IM ID : " + imId);
                break;

            // Get nickname
          /*  case ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE:
                // Nickname.NAME == data1
                String nickName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                ret.add("Nick name:" + nickName);
                break;*/

            // Get organization data.
            /*case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                // Organization.COMPANY == data1
                String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                // Organization.DEPARTMENT == data5
                String department = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
                // Organization.TITLE == data4
                String title = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                // Organization.JOB_DESCRIPTION == data6
                String jobDescription = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION));
                // Organization.OFFICE_LOCATION == data9
                String officeLocation = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION));

                ret.add("Company : " + company);
                ret.add("department : " + department);
                ret.add("Title : " + title);
                ret.add("Job Description : " + jobDescription);
                ret.add("Office Location : " + officeLocation);
                break;*/

            // Get phone number.
            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                // Phone.NUMBER == data1
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                // Phone.TYPE == data2
                int phoneTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String phoneTypeStr = getPhoneTypeString(phoneTypeInt);

                ret.add("Phone Number:" + phoneNumber);
                //ret.add("Phone Type Integer : " + phoneTypeInt);
                //ret.add("Phone Type String : " + phoneTypeStr);
                break;

            // Get sip address.
            case ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE:
            /*    // SipAddress.SIP_ADDRESS == data1
                String address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS));
                // SipAddress.TYPE == data2
                int addressTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.TYPE));
                String addressTypeStr = getEmailTypeString(addressTypeInt);

                ret.add("Address : " + address);
                ret.add("Address Type Integer : " + addressTypeInt);
                ret.add("Address Type String : " + addressTypeStr);
                break;*/

                // Get display name.
            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                // StructuredName.DISPLAY_NAME == data1
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                // StructuredName.GIVEN_NAME == data2
                String givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                // StructuredName.FAMILY_NAME == data3
                String familyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));

                ret.add("Display Name:" + displayName);

                //ret.add("Given Name : " + givenName);
                //ret.add("Family Name : " + familyName);
                break;

            // Get postal address.
            case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
            /*    // StructuredPostal.COUNTRY == data10
                String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                // StructuredPostal.CITY == data7
                String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                // StructuredPostal.REGION == data8
                String region = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                // StructuredPostal.STREET == data4
                String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                // StructuredPostal.POSTCODE == data9
                String postcode = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                // StructuredPostal.TYPE == data2
                int postType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                String postTypeStr = getEmailTypeString(postType);

                ret.add("Country : " + country);
                ret.add("City : " + city);
                ret.add("Region : " + region);
                ret.add("Street : " + street);
                ret.add("Postcode : " + postcode);
                ret.add("Post Type Integer : " + postType);
                ret.add("Post Type String : " + postTypeStr);
                break;*/

                // Get identity.
            /*case ContactsContract.CommonDataKinds.Identity.CONTENT_ITEM_TYPE:
                // Identity.IDENTITY == data1
                String identity = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.IDENTITY));
                // Identity.NAMESPACE == data2
                String namespace = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.NAMESPACE));

                ret.add("Identity : " + identity);
                ret.add("Identity Namespace : " + namespace);
                break;*/

                // Get photo.
            /*case ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE:
                // Photo.PHOTO == data15
                //String photo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
                // Photo.PHOTO_FILE_ID == data14
                String photoFileId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID));

                //ret.add("Photo : " + photo);
                ret.add("Photo File Id: " + photoFileId);
                break;*/

                // Get group membership.
            /*case ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE:
                // GroupMembership.GROUP_ROW_ID == data1
                int groupId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID));
                ret.add("Group ID : " + groupId);
                break;

            // Get website.
            case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                // Website.URL == data1
                String websiteUrl = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                // Website.TYPE == data2
                int websiteTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
                String websiteTypeStr = getEmailTypeString(websiteTypeInt);

                ret.add("Website Url : " + websiteUrl);
                ret.add("Website Type Integer : " + websiteTypeInt);
                ret.add("Website Type String : " + websiteTypeStr);
                break;

            // Get note.
            case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE:
                // Note.NOTE == data1
                String note = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                ret.add("Note : " + note);
                break;*/

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

    /*@Override
    protected void onDestroy() {
        dialog.dismiss();
        //Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }*/

    @SuppressLint("StaticFieldLeak")
    public class loadAllContacts extends AsyncTask<Void,Void,Void>{
        ProgressDialog dialog;
        @Override
        protected Void doInBackground(Void... voids) {

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
            String lastnumber = "0", userId = sharedPreference.getUserId();

            if (!contactLists.isEmpty()){
                contactLists.clear();
            }
            if (cur.getCount() > 0)
            {
                while (cur.moveToNext())
                {
                    String number = null;
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]
                                { id }, null);
                        while (cursor.moveToNext())
                        {
                            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","");
                            Log.e("lastnumber ", lastnumber);
                            Log.e("number", number);

                            if (number.equals(lastnumber))
                            {

                            }
                            else
                            {
                                lastnumber = number;

                                Log.e("lastnumber ", lastnumber);
                                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                switch (type)
                                {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        Log.e("Not Inserted", "Not inserted");
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:

                                        ContactListBeans contactListBeans = new ContactListBeans(userId,
                                                lastnumber, name);
                                        contactLists.add(contactListBeans);

                                    /*for(int i=0;i<contactLists.size();i++) {
                                        if (lastnumber.equalsIgnoreCase(contactLists.get(i).getPhone_no())){
                                            isMatch = true;
                                        }
                                    }

                                    if (!isMatch) {

                                    }
                                    isMatch = false;*/
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
            dialog = new ProgressDialog(SocialMediaLogin.this);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(dialog.isShowing())
            {
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
            HttpPost httppost = new HttpPost(Utility.BASE_URL+"/getContactDetails");

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
            dialog = new ProgressDialog(SocialMediaLogin.this);
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
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }

            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(intent);
            finish();
            /*Intent intent = new Intent(getApplicationContext(), PersonalDetailsActivity.class);
            startActivity(intent);*/
        }
    }
}
