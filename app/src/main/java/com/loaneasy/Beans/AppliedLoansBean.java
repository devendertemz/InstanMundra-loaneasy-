package com.loaneasy.Beans;

public class AppliedLoansBean {

    private String loanId, loanAmnt, disbursedAmnt, loanAvailedDate, repayDate, tenure, interest, processingFee, repayAmount,
            dueDate, applicationStatus, repayment_status, upcoming_payment, fineAMount, chequeBounceAount;


    public AppliedLoansBean(String loanId, String loanAmnt, String disbursedAmnt, String loanAvailedDate, String repayDate, String tenure, String interest,
                            String processingFee, String repayAmount, String dueDate, String applicationStatus,
                            String repayment_status, String upcoming_payment, String fineAMount, String chequeBounceAount ) {
        this.loanId = loanId;
        this.loanAmnt = loanAmnt;
        this.disbursedAmnt = disbursedAmnt;
        this.loanAvailedDate = loanAvailedDate;
        this.repayDate = repayDate;
        this.tenure = tenure;
        this.interest = interest;
        this.processingFee = processingFee;
        this.repayAmount = repayAmount;
        this.dueDate = dueDate;
        this.applicationStatus = applicationStatus;
        this.repayment_status = repayment_status;
        this.upcoming_payment = upcoming_payment;
        this.fineAMount = fineAMount;
        this.chequeBounceAount = chequeBounceAount;
    }

    public String getFineAMount() {
        return fineAMount;
    }

    public void setFineAMount(String fineAMount) {
        this.fineAMount = fineAMount;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getLoanAmnt() {
        return loanAmnt;
    }

    public void setLoanAmnt(String loanAmnt) {
        this.loanAmnt = loanAmnt;
    }

    public String getDisbursedAmnt() {
        return disbursedAmnt;
    }

    public void setDisbursedAmnt(String disbursedAmnt) {
        this.disbursedAmnt = disbursedAmnt;
    }

    public String getLoanAvailedDate() {
        return loanAvailedDate;
    }

    public void setLoanAvailedDate(String loanAvailedDate) {
        this.loanAvailedDate = loanAvailedDate;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(String processingFee) {
        this.processingFee = processingFee;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(String repayDate) {
        this.repayDate = repayDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getRepayment_status() {
        return repayment_status;
    }

    public void setRepayment_status(String repayment_status) {
        this.repayment_status = repayment_status;
    }

    public String getUpcoming_payment() {
        return upcoming_payment;
    }

    public void setUpcoming_payment(String upcoming_payment) {
        this.upcoming_payment = upcoming_payment;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getChequeBounceAount() {
        return chequeBounceAount;
    }

    public void setChequeBounceAount(String chequeBounceAount) {
        this.chequeBounceAount = chequeBounceAount;
    }
}
