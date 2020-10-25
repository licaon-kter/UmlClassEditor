package com.nathaniel.motus.umlclasseditor.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;

import java.util.ArrayList;

public class ClassEditorFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText mClassNameEdit;
    private RadioButton mJavaRadio;
    private RadioButton mAbstractRadio;
    private RadioButton mInterfaceRadio;
    private RadioButton mEnumRadio;
    private Button mNewAttributeButton;
    private ListView mAttributeList;
    private Button mNewMethodButton;
    private ListView mMethodList;
    private Button mOKButton;
    private Button mCancelButton;

    private static final int NEW_ATTRIBUTE_BUTTON_TAG=210;
    private static final int ATTRIBUTE_LIST_TAG=220;
    private static final int NEW_METHOD_BUTTON_TAG=230;
    private static final int METHOD_LIST_TAG=240;
    private static final int OK_BUTTON_TAG=250;
    private static final int CANCEL_BUTTON_TAG=260;

    private FragmentObserver mCallback;

    private ArrayList<UmlClassAttribute> mUmlClassAttributes;
    private ArrayList<UmlClassMethod> mUmlClassMethods;
    private ArrayList<String> mValues;
    private float mXPos;
    private float mYPos;
    private int mClassIndex;
    //class index in current project, -1 if new class

    private static final String XPOS_KEY="xPos";
    private static final String YPOS_KEY="yPos";
    private static final String CLASS_INDEX_KEY="classIndex";



//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public ClassEditorFragment() {
        // Required empty public constructor
    }

    public static ClassEditorFragment newInstance(float xPos, float yPos,int classIndex) {
        ClassEditorFragment fragment = new ClassEditorFragment();
        Bundle args = new Bundle();
        args.putFloat(XPOS_KEY,xPos);
        args.putFloat(YPOS_KEY,yPos);
        args.putInt(CLASS_INDEX_KEY,classIndex);
        fragment.setArguments(args);
        return fragment;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public ArrayList<UmlClassAttribute> getUmlClassAttributes() {
        return mUmlClassAttributes;
    }

    public void setUmlClassAttributes(ArrayList<UmlClassAttribute> umlClassAttributes) {
        mUmlClassAttributes = umlClassAttributes;
    }

    public ArrayList<UmlClassMethod> getUmlClassMethods() {
        return mUmlClassMethods;
    }

    public void setUmlClassMethods(ArrayList<UmlClassMethod> umlClassMethods) {
        mUmlClassMethods = umlClassMethods;
    }

    public ArrayList<String> getValues() {
        return mValues;
    }

    public void setValues(ArrayList<String> values) {
        mValues = values;
    }


//    **********************************************************************************************
//    Fragment events
//    **********************************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mXPos=getArguments().getFloat(XPOS_KEY);
            mYPos=getArguments().getFloat(YPOS_KEY);
            mClassIndex=getArguments().getInt(CLASS_INDEX_KEY,-1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureViews();
        createCallbackToParentActivity();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    private void configureViews() {
        mClassNameEdit=getActivity().findViewById(R.id.class_name_input);

        mJavaRadio=getActivity().findViewById(R.id.class_java_radio);
        mAbstractRadio=getActivity().findViewById(R.id.class_abstract_radio);
        mInterfaceRadio=getActivity().findViewById(R.id.class_interface_radio);
        mEnumRadio=getActivity().findViewById(R.id.class_enum_radio);

        mNewAttributeButton=getActivity().findViewById(R.id.class_add_attribute_button);
        mNewAttributeButton.setTag(NEW_ATTRIBUTE_BUTTON_TAG);
        mNewAttributeButton.setOnClickListener(this);

        mAttributeList=getActivity().findViewById(R.id.class_attributes_list);
        mAttributeList.setTag(ATTRIBUTE_LIST_TAG);
        mAttributeList.setOnItemClickListener(this);

        mNewMethodButton=getActivity().findViewById(R.id.class_add_method_button);
        mNewMethodButton.setTag(NEW_METHOD_BUTTON_TAG);
        mNewMethodButton.setOnClickListener(this);

        mMethodList=getActivity().findViewById(R.id.class_methods_list);
        mMethodList.setTag(METHOD_LIST_TAG);
        mMethodList.setOnItemClickListener(this);

        mOKButton=getActivity().findViewById(R.id.class_ok_button);
        mOKButton.setTag(OK_BUTTON_TAG);
        mOKButton.setOnClickListener(this);

        mCancelButton=getActivity().findViewById(R.id.class_cancel_button);
        mCancelButton.setTag(CANCEL_BUTTON_TAG);
        mCancelButton.setOnClickListener(this);

        mUmlClassAttributes=new ArrayList<>();
        mUmlClassMethods=new ArrayList<>();
    }

    private void createCallbackToParentActivity() {
        mCallback=(FragmentObserver)getActivity();
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public void onClick(View v) {

        int tag=(int)v.getTag();

        switch (tag) {

            case OK_BUTTON_TAG:
                createOrUpdateClass();
                break;

            case CANCEL_BUTTON_TAG:
                mCallback.closeFragment(this);
                break;

            case NEW_ATTRIBUTE_BUTTON_TAG:
                mCallback.setPurpose(FragmentObserver.Purpose.CREATE_ATTRIBUTE);
                mCallback.openAttributeEditorFragment(-1);
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    private void createOrUpdateClass() {

        if (getClassName().equals("")) Toast.makeText(getContext(),"Name cannot be blank",Toast.LENGTH_SHORT).show();

        else {
            if (mClassIndex == -1) {
                mCallback.getProject().addUmlClass(new UmlClass(getClassName(), getClassType(), mUmlClassAttributes, mUmlClassMethods,mValues, mXPos, mYPos));
            } else {
                mCallback.getProject().getUmlClasses().get(mClassIndex).setName(getClassName());
                mCallback.getProject().getUmlClasses().get(mClassIndex).setUmlClassType(getClassType());
                mCallback.getProject().getUmlClasses().get(mClassIndex).setAttributeList(mUmlClassAttributes);
                mCallback.getProject().getUmlClasses().get(mClassIndex).setMethodList(mUmlClassMethods);
                mCallback.getProject().getUmlClasses().get(mClassIndex).setValueList(mValues);
            }
            mCallback.closeFragment(this);
        }
    }

    private String getClassName() {
        return mClassNameEdit.getText().toString();
    }

    private UmlClass.UmlClassType getClassType() {
        UmlClass.UmlClassType type= UmlClass.UmlClassType.JAVA_CLASS;

        if (mJavaRadio.isChecked()) type= UmlClass.UmlClassType.JAVA_CLASS;
        if (mAbstractRadio.isChecked()) type= UmlClass.UmlClassType.ABSTRACT_CLASS;
        if (mInterfaceRadio.isChecked()) type= UmlClass.UmlClassType.INTERFACE;
        if (mEnumRadio.isChecked()) type= UmlClass.UmlClassType.ENUM;

        return type;
    }

}