package com.loaneasy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.loaneasy.Beans.ContactListBeans;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class ContactList extends AppCompatActivity {

    private static final String TAG_ANDROID_CONTACTS = "ANDROID_CONTACTS";
    private ArrayList<ContactListBeans> contactLists = new ArrayList<ContactListBeans>();
    private ArrayList<String> myList = new ArrayList<String>();
    String json = "";
    JSONObject userData = new JSONObject();
    private ArrayList<String> name,phoneNo;

    JSONObject JSONcontacts = new JSONObject();
    JSONObject JSONname = new JSONObject();
    JSONObject EverythingJSON = new JSONObject();
    private String getPhoneNo, getName;
    //String[] name,phoneNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        name = new ArrayList<String>();
        phoneNo = new ArrayList<String>();

        //setTitle("dev2qa.com - Android Contacts Operate Example.");

        Button sendContacts = (Button) findViewById(R.id.btSendContacts);
        sendContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String json = new Gson().toJson(contactLists);
                Log.i("gson","->"+contactLists.size());
                Log.i("gson","->"+json.length());

                //makeRequest(json);
                new SendingContactDetails().execute();


            }
        });

        Button displayContact = (Button) findViewById(R.id.btListContact);
        displayContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /// Toast.makeText(ContactList.this, "Clicked", Toast.LENGTH_SHORT).show();
               /* Log.i("RR","-->"+phoneNo.size());
                Log.i("RR","-->"+name.size());*/

               //sendContactDetails();


                Log.i("RR","-->"+phoneNo.size());
                Log.i("RR","-->"+name.size());

                Log.i("RR","-->"+contactLists.size());

                for(int i=0; i<phoneNo.size(); i++)
                {
                    try {
                        JSONcontacts.put("Phone:" + String.valueOf(i + 1), phoneNo.get(i));
                        //Log.i("Phone","------->");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for(int j=0; j<name.size(); j++)
                {
                    try {
                        JSONname.put("Name:" + String.valueOf(j + 1), name.get(j));
                        //Log.i("Name","------->");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    EverythingJSON.put("phone_no", JSONcontacts);
                    EverythingJSON.put("contact_name", JSONname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("data","-->"+EverythingJSON);


            }
        });

        // Load all contacts, and print each contact as log debug info.
        Button loadButton = (Button)findViewById(R.id.contact_operate_load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPhoneContactsPermission(android.Manifest.permission.READ_CONTACTS))
                {
                    requestPermission(android.Manifest.permission.READ_CONTACTS);
                }else {
                    getAllContacts();
                    Toast.makeText(ContactList.this, "Contact data has been printed in the android monitor log..", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }






    /* Return all contacts and show each contact data in android monitor console as debug info. */
    private List<ContactDTO> getAllContacts()
    {
        List<ContactDTO> ret = new ArrayList<ContactDTO>();

        // Get all raw contacts id list.
        List<Integer> rawContactsIdList = getRawContactsIdList();

        int contactListSize = rawContactsIdList.size();

        ContentResolver contentResolver = getContentResolver();

        // Loop in the raw contacts list.
        for(int i=0;i<contactListSize;i++)
        {
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

            // Query data table and return related contact data.
            Cursor cursor = contentResolver.query(dataContentUri, queryColumnArr, whereClauseBuf.toString(), null, null);

            /* If this cursor return database table row data.
               If do not check cursor.getCount() then it will throw error
               android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0.
               */
            if(cursor!=null && cursor.getCount() > 0)
            {
                StringBuffer lineBuf = new StringBuffer();
                cursor.moveToFirst();
                lineBuf.append("Raw Contact Id:");
                lineBuf.append(rawContactId);

              /*  long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                lineBuf.append(", Contact Id:");
                lineBuf.append(contactId);*/

                do{
                    // First get mimetype column value.
                    String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                    //lineBuf.append(", MimeType:");
                    //lineBuf.append(mimeType);

                    List<String> dataValueList = getColumnValueByMimetype(cursor, mimeType);
                    int dataValueListSize = dataValueList.size();
                    for(int j=0;j < dataValueListSize;j++)
                    {
                        String dataValue = dataValueList.get(j);
                        lineBuf.append(", ");
                        lineBuf.append(dataValue);


                    }

                }while(cursor.moveToNext());

                //Log.d(TAG_ANDROID_CONTACTS, lineBuf.toString());

                //myList.add(lineBuf.toString());
                String data =  lineBuf.toString();
                String[] ary = data.split(", ");
               // Log.i("rr","--->"+i);

               // String srs = Arrays.toString(ary);
                //Log.i("rr","--->"+srs);


                for (int j=0; j<ary.length; j++) {
                    //Log.i("rr","--->"+ary[j]);

                    if (ary[j].contains("Display Name")) {
                         Log.i("rr","--->"+ary[j]);
                        //name.add(anAry);
                        //name[i] = ary[j];

                        getName = ary[j];

                        name.add(ary[j]);
                    }
                    if (ary[j].contains("Phone Number")) {
                        Log.i("rr","--->"+ary[j]);
                        //phoneNo.add(anAry);
                        //phoneNo[i] = ary[j];
                         phoneNo.add(ary[j]);
                        getPhoneNo = ary[j];
                    }

                }


                ContactListBeans contactListBeans = new ContactListBeans("1001",getPhoneNo,getName);
                contactLists.add(contactListBeans);





            }

            //Log.d(TAG_ANDROID_CONTACTS, "=========================================================================");
        }

        return ret;
    }

    /*
     *  Get email type related string format value.
     * */
    private String getEmailTypeString(int dataType)
    {
        String ret = "";

        if(ContactsContract.CommonDataKinds.Email.TYPE_HOME == dataType)
        {
            ret = "HomeActivity";
        }else if(ContactsContract.CommonDataKinds.Email.TYPE_WORK==dataType)
        {
            ret = "Work";
        }
        return ret;
    }

    /*
     *  Get phone type related string format value.
     * */
    private String getPhoneTypeString(int dataType)
    {
        String ret = "";

        if(ContactsContract.CommonDataKinds.Phone.TYPE_HOME == dataType)
        {
            ret = "HomeActivity";
        }else if(ContactsContract.CommonDataKinds.Phone.TYPE_WORK==dataType)
        {
            ret = "Work";
        }else if(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE==dataType)
        {
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
    private List<String> getColumnValueByMimetype(Cursor cursor, String mimeType)
    {
        List<String> ret = new ArrayList<String>();

        switch (mimeType)
        {
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
    private List<Integer> getRawContactsIdList()
    {
        List<Integer> ret = new ArrayList<Integer>();

        ContentResolver contentResolver = getContentResolver();

        // Row contacts content uri( access raw_contacts table. ).
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;
        // Return _id column in contacts raw_contacts table.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};
        // Query raw_contacts table and return raw_contacts table _id.
        Cursor cursor = contentResolver.query(rawContactUri,queryColumnArr, null, null, null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            do{
                int idColumnIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                int rawContactsId = cursor.getInt(idColumnIndex);
                ret.add(new Integer(rawContactsId));
            }while(cursor.moveToNext());
        }

        cursor.close();

        return ret;
    }


    // Check whether user has phone contacts manipulation permission or not.
    private boolean hasPhoneContactsPermission(String permission)
    {
        boolean ret = false;

        // If android sdk version is bigger than 23 the need to check run time permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // return phone read contacts permission grant status.
            int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            // If permission is granted then return true.
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        }else
        {
            ret = true;
        }
        return ret;
    }

    // Request a runtime permission to app user.
    private void requestPermission(String permission)
    {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, 1);
    }

    // After user select Allow or Deny button in request runtime permission dialog
    // , this method will be invoked.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int length = grantResults.length;
        if(length > 0)
        {
            int grantResult = grantResults[0];

            if(grantResult == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "You allowed permission, please click the button again.", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(getApplicationContext(), "You denied permission.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class SendingContactDetails extends AsyncTask<Object,Void, ArrayList<String>>
    {
        ProgressDialog dialog;
        String responseBody;

        @Override
        protected ArrayList<String> doInBackground(Object... objects) {
            ArrayList<NameValuePair> postVars = new ArrayList<NameValuePair>();

            //Add a 1st Post Value called JSON with String value of JSON inside
            //This is first and last post value sent because server side will decode the JSON and get other vars from it.
            //postVars.add(new BasicNameValuePair("JSON", EverythingJSON.toString()));
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(JSONname);
            jsonArray.put(JSONcontacts);

            String json = new Gson().toJson(contactLists);
            Log.i("GSON","->"+json);
            postVars.add(new BasicNameValuePair("contact_list", json));
            //postVars.add(new BasicNameValuePair("contact_name[]", name.toString()));

            //Declare and Initialize Http Clients and Http Posts
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://fuinex.com/LoanEasy/API/getContactDetails");

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
                Log.i("response","-->"+responseBody);
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
            dialog = new ProgressDialog(ContactList.this);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
         dialog.dismiss();
        }
    }



    public static String makeRequest( String json) {
        HttpURLConnection urlConnection;
        String url="http://fuinex.com/LoanEasy/API/getContactDetails";
        JSONObject jsonObject = new JSONObject();
        String data = json;
        String result = null;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /*private void sendContactDetails()
    {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(ContactList.this);
        progressBar.setCancelable( true );
        progressBar.setMessage( "Please Wait..." );
        progressBar.setProgressStyle( ProgressDialog.STYLE_SPINNER );
        progressBar.setProgress( 0 );
        progressBar.setMax( 1000 );
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://192.168.0.100/LoanEasy/API/")
                .setLogLevel(RestAdapter.LogLevel.FULL)

                .build();
        ApiRequest api = adapter.create(ApiRequest.class);
        String json = new Gson().toJson(contactLists);
        api.contactDetails(json, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                BufferedReader reader = null;
                String output = "";

                progressBar.dismiss();
                try {

                    //Initializing buffered reader
                    reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    //Reading the output in the string
                    output = reader.readLine();

                    //JSONObject jsonObject = new JSONObject(output);
                    Log.i("response","----------->"+output);



                } catch (Exception e)
                {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i("failure","---->>"+error);
                Log.i("failure","---->>"+error.getMessage());
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }*/


}
