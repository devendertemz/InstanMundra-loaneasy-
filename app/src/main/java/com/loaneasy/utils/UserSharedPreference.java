package com.loaneasy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserSharedPreference {

    private SharedPreferences prefs;

    public UserSharedPreference(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setFCMId(String FCMId) {
        prefs.edit().putString("FCMId", FCMId).apply();
    }

    public String getFCMId() {
        return prefs.getString("FCMId","");
    }

    public void setUserId(String userId) {
        prefs.edit().putString("userId", userId).apply();
    }

    public String getUserId() {
        return prefs.getString("userId","");
    }

    public void setUserPhoneNo(String phoneNo) {
        prefs.edit().putString("phoneNo", phoneNo).apply();
    }

    public String getUserPhoneNo() {
        return prefs.getString("phoneNo","");
    }


    public void setUserArea(String area) {
        prefs.edit().putString("area", area).apply();
    }

    public String getUserArea() {
        return prefs.getString("area","");
    }



    public void setUserSocialName(String name) {
        prefs.edit().putString("Name", name).apply();
    }

    public String getUserSocialName() {
        return prefs.getString("Name","");
    }

    public void setUserEmail(String UserEmail) {
        prefs.edit().putString("UserEmail", UserEmail).apply();
    }

    public String getUserEmail() {
        return prefs.getString("UserEmail","");
    }

    public void setLocation(String Location) {
        prefs.edit().putString("Location", Location).apply();
    }

    public String getLocation() {
        return prefs.getString("Location","");
    }

    public void setSocialMediaId(String socialMediaId) {
        prefs.edit().putString("socialMediaId", socialMediaId).apply();
    }

    public String getSocialMediaId() {
        return prefs.getString("socialMediaId","");
    }

    public void setSocialMediaType(String socialMediaType) {
        prefs.edit().putString("socialMediaType", socialMediaType).apply();
    }

    public String getSocialMediaType() {
        return prefs.getString("socialMediaType","");
    }

    public void setProfilePic(String socialProfilePic) {
        prefs.edit().putString("socialProfilePic", socialProfilePic).apply();
    }

    public String getProfilePic() {
        return prefs.getString("socialProfilePic","");
    }

    public void setExistCustomerFlag(int existCustomerFlag) {
        prefs.edit().putInt("existCustomerFlag", existCustomerFlag).apply();
    }

    public int getExistCustomerFlag() {
        return prefs.getInt("existCustomerFlag",0);
    }

    public void setSignFlag(int signFlag) {
        prefs.edit().putInt("SignFlag", signFlag).apply();
    }

    public int getSignFlag() {
        return prefs.getInt("SignFlag",0);
    }

    public void setUserCity(String userPoints) {
        prefs.edit().putString("userCity", userPoints).apply();
    }

    public String getUserCity() {
        return prefs.getString("userCity","");
    }

    public void clearPrefs(){
        prefs.edit().clear().apply();
    }

    //////////
    public void setUserName(String name) {
        prefs.edit().putString("userName", name).apply();
    }

    public String getUserName() {
        return prefs.getString("userName","");
    }

    public void setPincode(String pin) {
        prefs.edit().putString("pincode", pin).apply();
    }

    public String getPincode() {
        return prefs.getString("pincode","");
    }

    public void setCity(String City) {
        prefs.edit().putString("City", City).apply();
    }

    public String getCity() {
        return prefs.getString("City","");
    }

    public void setState(String state) {
        prefs.edit().putString("state", state).apply();
    }

    public String getState() {
        return prefs.getString("state","");
    }

    public void setAddress(String add) {
        prefs.edit().putString("address", add).apply();
    }

    public String getAddress() {
        return prefs.getString("address","");
    }

    public void setEmail(String email) {
        prefs.edit().putString("Email", email).apply();
    }

    public String getEmail() {
        return prefs.getString("Email","");
    }

    public void setDob(String dob) {
        prefs.edit().putString("dob", dob).apply();
    }

    public String getDob() {
        return prefs.getString("dob","");
    }

    public void setCompanyName(String cname) {
        prefs.edit().putString("cname", cname).apply();
    }

    public String getCompanyName() {
        return prefs.getString("cname","");
    }

    public void setCompanyAddr(String caddr) {
        prefs.edit().putString("caddr", caddr).apply();
    }

    public String getCompanyAddr() {
        return prefs.getString("caddr","");
    }

    public void setSalary(String salary) {
        prefs.edit().putString("salary", salary).apply();
    }

    public String getSalary() {
        return prefs.getString("salary","");
    }

    public void setAadhar(String aadhar) {
        prefs.edit().putString("aadhar", aadhar).apply();
    }

    public String getAadhar() {
        return prefs.getString("aadhar","");
    }

    public void setPan(String pan) {
        prefs.edit().putString("pan", pan).apply();
    }

    public String getPan() {
        return prefs.getString("pan","");
    }

    public void setAccnNo(String accnNo) {
        prefs.edit().putString("accnNo", accnNo).apply();
    }

    public String getAccnNo() {
        return prefs.getString("accnNo","");
    }

    public void setIfsc(String ifsc) {
        prefs.edit().putString("ifsc", ifsc).apply();
    }

    public String getIfsc() {
        return prefs.getString("ifsc","");
    }

    public void setEmpIdURI(String empIdURI) {
        prefs.edit().putString("empIdURI", empIdURI).apply();
    }

    public String getEmpIdURI() {
        return prefs.getString("empIdURI","");
    }

    public void setAdharFrntURI(String adharFrntURI) {
        prefs.edit().putString("adharFrntURI", adharFrntURI).apply();
    }

    public String getAdharFrntURI() {
        return prefs.getString("adharFrntURI","");
    }

    public void setAdharBackURI(String adharBackURI) {
        prefs.edit().putString("adharBackURI", adharBackURI).apply();
    }

    public String getAdharBackURI() {
        return prefs.getString("adharBackURI","");
    }

    public void setPanURI(String panURI) {
        prefs.edit().putString("panURI", panURI).apply();
    }

    public String getPanURI() {
        return prefs.getString("panURI","");
    }

    public void setBankStatPath(String bankStatPath) {
        prefs.edit().putString("bankStatPath", bankStatPath).apply();
    }

    public String getBankStatPath() {
        return prefs.getString("bankStatPath","");
    }

    public void setSalSlip1Path(String salSlip1Path) {
        prefs.edit().putString("salSlip1Path", salSlip1Path).apply();
    }

    public String getSalSlip1Path() {
        return prefs.getString("salSlip1Path","");
    }

    public void setSalSlip2Path(String salSlip2Path) {
        prefs.edit().putString("salSlip2Path", salSlip2Path).apply();
    }

    public String getSalSlip2Path() {
        return prefs.getString("salSlip2Path","");
    }

    public void setSalSlip3Path(String salSlip3Path) {
        prefs.edit().putString("salSlip3Path", salSlip3Path).apply();
    }

    public String getSalSlip3Path() {
        return prefs.getString("salSlip3Path","");
    }

    /////for spinners
    public void setAccType(int accType) {
        prefs.edit().putInt("accType", accType).apply();
    }

    public int getAccType() {
        return prefs.getInt("accType",0);
    }

    public void setBankName(int bankName) {
        prefs.edit().putInt("bankName", bankName).apply();
    }

    public int getBankName() {
        return prefs.getInt("bankName",0);
    }

    public void setGender(int gender) {
        prefs.edit().putInt("gender", gender).apply();
    }

    public int getGender() {
        return prefs.getInt("gender",0);
    }

    public void setCurrLoan(int currLoan) {
        prefs.edit().putInt("currLoan", currLoan).apply();
    }

    public int getCurrLoan() {
        return prefs.getInt("currLoan",0);
    }

    public void setHouseType(int houseType) {
        prefs.edit().putInt("houseType", houseType).apply();
    }

    public int getHouseType() {
        return prefs.getInt("houseType",0);
    }

    public void setWorkYear(int workYear) {
        prefs.edit().putInt("workYear", workYear).apply();
    }

    public int getWorkYear() {
        return prefs.getInt("workYear",0);
    }

    public void setEmpType(int empType) {
        prefs.edit().putInt("empType", empType).apply();
    }

    public int getEmpType() {
        return prefs.getInt("empType",0);
    }

    public void setModeSalary(int modeSalary) {
        prefs.edit().putInt("modeSalary", modeSalary).apply();
    }

    public int getModeSalary() {
        return prefs.getInt("modeSalary",0);
    }




}