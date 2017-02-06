package com.pengona.sqtest;

/**
 * Created by mehmetatmaca on 2016-11-19.
 */

public class CSVData {

    private int id;
    private String _date;
    private String _col1;

    public CSVData(){

    }

    public CSVData(int id, String dateCol, String col1){
        this.id = id;
        this._date=dateCol;
        this._col1=col1;
    }
    public CSVData(String dateCol, String col1){
        this._date=dateCol;
        this._col1=col1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_col1() {
        return _col1;
    }

    public void set_col1(String _col1) {
        this._col1 = _col1;
    }
}
