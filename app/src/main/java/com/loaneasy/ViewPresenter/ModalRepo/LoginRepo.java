package com.loaneasy.ViewPresenter.ModalRepo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRepo {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("response")
    @Expose
    private Response response;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }


    public class Response {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("phone_no")
        @Expose
        private String phoneNo;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("existing_customer")
        @Expose
        private String existingCustomer;
        @SerializedName("profile_completed")
        @Expose
        private String profileCompleted;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("fcm_token")
        @Expose
        private String fcmToken;
        @SerializedName("date_time")
        @Expose
        private String dateTime;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getExistingCustomer() {
            return existingCustomer;
        }

        public void setExistingCustomer(String existingCustomer) {
            this.existingCustomer = existingCustomer;
        }

        public String getProfileCompleted() {
            return profileCompleted;
        }

        public void setProfileCompleted(String profileCompleted) {
            this.profileCompleted = profileCompleted;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getFcmToken() {
            return fcmToken;
        }

        public void setFcmToken(String fcmToken) {
            this.fcmToken = fcmToken;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }


        @Override
        public String toString() {
            return "Response{" +
                    "userId='" + userId + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", phoneNo='" + phoneNo + '\'' +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", existingCustomer='" + existingCustomer + '\'' +
                    ", profileCompleted='" + profileCompleted + '\'' +
                    ", location='" + location + '\'' +
                    ", fcmToken='" + fcmToken + '\'' +
                    ", dateTime='" + dateTime + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginRepo{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", response=" + response +
                '}';
    }
}