package com.gpasalis.threegoodthings;

import java.io.Serializable;

public class Note implements Serializable {

    private int noteId;
    private String date;
    private String exp_1;
    private String exp_2;
    private String exp_3;

    public Note()  {

    }

    public Note(  String date, String exp_1, String exp_2, String exp_3) {
        this.date = date;
        this.exp_1 = exp_1;
        this.exp_2 = exp_2;
        this.exp_3 = exp_3;
    }

    public Note(int noteId, String date, String exp_1, String exp_2, String exp_3) {
        this.noteId= noteId;
        this.date= date;
        this.exp_1 = exp_1;
        this.exp_2 = exp_2;
        this.exp_3 = exp_3;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExp1() {
        return exp_1;
    }

    public void setExp1(String exp_1) {
        this.exp_1 = exp_1;
    }

    public String getExp2() {
        return exp_2;
    }

    public void setExp2(String exp_2) {
        this.exp_2 = exp_2;
    }

    public String getExp3() {
        return exp_3;
    }

    public void setExp3(String exp_3) {
        this.exp_3 = exp_3;
    }


    @Override
    public String toString()  {
        return date;
    }
}