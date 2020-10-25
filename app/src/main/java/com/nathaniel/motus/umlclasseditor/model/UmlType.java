package com.nathaniel.motus.umlclasseditor.model;

public class UmlType {
    //type of attributes and parameters, such as int, String, etc.
    //for custom types, i.e project classes, it will be extended by UmlClass

    protected String mName;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlType(String name) {
        this.mName = name;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}