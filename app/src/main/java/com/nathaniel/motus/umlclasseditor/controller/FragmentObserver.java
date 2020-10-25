package com.nathaniel.motus.umlclasseditor.controller;

import androidx.fragment.app.Fragment;

import com.nathaniel.motus.umlclasseditor.model.UmlProject;

//    **********************************************************************************************
//    Callback interface
//    **********************************************************************************************
    public interface FragmentObserver {


        void setExpectingTouchLocation(boolean b);
        void setPurpose(Purpose purpose);
        UmlProject getProject();
        void closeFragment(Fragment fragment);
        void openClassEditorFragment();
        void openAttributeEditorFragment(int attributeIndex);
        void openMethodEditorFragment(int methodIndex);
        void openParameterEditorFragment(int parameterIndex);

        enum Purpose{NONE,CREATE_CLASS,EDIT_CLASS,CREATE_ATTRIBUTE,EDIT_ATTRIBUTE,CREATE_METHOD,EDIT_METHOD,CREATE_PARAMETER,EDIT_PARAMETER}
}