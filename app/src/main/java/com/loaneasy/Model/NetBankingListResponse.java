package com.loaneasy.Model;

import com.google.gson.annotations.SerializedName;

public class NetBankingListResponse {

    @SerializedName("bank_id")
    private String bank_id;
    @SerializedName("bank_name")
    private String bank_name;
    @SerializedName("type")
    private String type;
    @SerializedName("bank_code")
    private String bank_code;
    @SerializedName("created_at")
    private String created_at;


    public NetBankingListResponse(String bank_id, String bank_name, String type,
                                  String bank_code, String created_at) {
        this.bank_id = bank_id;
        this.bank_name = bank_name;
        this.type = type;
        this.bank_code = bank_code;
        this.created_at = created_at;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
