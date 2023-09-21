package com.example.healthfile;

import android.net.Uri;

public class Upload {
    private String rType;
    private String hName;
    private String dName;
    private String vDate;
    private String reMarks;
    private String mImgUrl;

    private String id;

    public Upload(){

    }
    public Upload(String Rtype, String Hname, String Dname, String VDate, String ReMarks,String ImgUrl){
        rType=Rtype;
        hName=Hname;
        dName=Dname;
        vDate=VDate;
        reMarks=ReMarks;
        mImgUrl=ImgUrl;
    }
    public String getrType() {
        return rType;
    }
    public void setrType(String Rtype) {
        rType = Rtype;
    }

    public String gethName() {
        return hName;
    }
    public void sethName(String Hname) {
        hName = Hname;
    }

    public String getdName() {
        return dName;
    }
    public void setdName(String Dname) {
        dName = Dname;
    }

    public void setvDate(String VDate) {
        vDate = VDate;
    }
    public String getvDate() {
        return vDate;
    }

    public String getrMarks() {
        return reMarks;
    }
    public void setrMarks(String ReMarks) {
        reMarks = ReMarks;
    }

    public String getmImgUrl() {
        return mImgUrl;
    }
    public void setmImgUrl(String ImgUrl) {
        mImgUrl = ImgUrl;
    }


    public String getId(){return id;}
    public void setId(String id){this.id=id;}
}
