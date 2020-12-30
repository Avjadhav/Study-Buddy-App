package com.example.studybuddy;

public class Document {
    String UploadedBy,Document_year,Teacher_id,Document_Url,filename;
    long Date_Time;

    public Document() {
    }

    public Document(String uploadedBy, String document_year, String teacher_id, String document_Url, String filename) {
        UploadedBy = uploadedBy;
        Document_year = document_year;
        Teacher_id = teacher_id;
        Document_Url = document_Url;
        this.filename = filename;
        Date_Time = System.currentTimeMillis()/1000;
    }



    public String getUploadedBy() {
        return UploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        UploadedBy = uploadedBy;
    }

    public String getDocument_year() {
        return Document_year;
    }

    public void setDocument_year(String document_year) {
        Document_year = document_year;
    }

    public String getTeacher_id() {
        return Teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        Teacher_id = teacher_id;
    }

    public String getDocument_Url() {
        return Document_Url;
    }

    public void setDocument_Url(String document_Url) {
        Document_Url = document_Url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getDate_Time() {
        return Date_Time;
    }

    public void setDate_Time(long date_Time) {
        Date_Time = date_Time;
    }

    @Override
    public String toString() {
        return "Document{" +
                "UploadedBy='" + UploadedBy + '\'' +
                ", Document_year='" + Document_year + '\'' +
                ", Teacher_id='" + Teacher_id + '\'' +
                ", Document_Url='" + Document_Url + '\'' +
                ", filename='" + filename + '\'' +
                ", Date_Time=" + Date_Time +
                '}';
    }
}
