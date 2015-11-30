package com.example.william.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Array;

public class JobSearchFilterDialog extends DialogFragment{
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface JobSearchFilterListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    JobSearchFilterListener mListener;

    /*
    // search variable
    jobSearch.setKeywordFilter(); // string
    jobSearch.setKeyword(); // string
    jobSearch.setSalaryMin(); // int
    jobSearch.setSalaryMax(); // int
    jobSearch.setJobSpec(); // string[]
    jobSearch.setJobRole(); // string[]
    jobSearch.setCountry(); // string[]
    jobSearch.setState(); // string[]
    jobSearch.setJobLevel(); // string[]
    jobSearch.setJobType(); // string[]
    jobSearch.setAdvertiser(); // boolean
    jobSearch.setDirectEmployer(); //boolean
    jobSearch.setOrderPreference(); // string
    jobSearch.setPage(); // int
    */

    public int keywordFilter;
    public String keyword;
    public int salaryMin;
    public int salaryMax;
    public String[] jobSpec;
    public String[] jobRole;
    public String[] country;
    public String[] state;
    public String[] jobLevel;
    public String[] jobType;
    public boolean advertiser = false;
    public boolean directEmployer = false;
    public String orderPreference = "date_posted";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_filter, null);

        // form inputs
        final Spinner keywordFilterInput = (Spinner)view.findViewById(R.id.keyword_filter_spinner);
        ArrayAdapter<KeywordFilter> keywordFilterArrayAdapter = new ArrayAdapter<>(
                this.getActivity(),
                android.R.layout.simple_list_item_1,
                KeywordFilterArray.populate()
        );
        keywordFilterInput.setAdapter(keywordFilterArrayAdapter);

        final EditText keywordInput = (EditText)view.findViewById(R.id.keyword);
        final EditText salaryMinInput = (EditText)view.findViewById(R.id.minimum_salary);
        final EditText salaryMaxInput = (EditText)view.findViewById(R.id.maximum_salary);

        /*
        final Spinner countryInput = (Spinner)view.findViewById(R.id.country_spinner);
        ArrayAdapter<Country> countryAdapter = new ArrayAdapter<Country>(
                this.getActivity(),
                R.layout.country_spinner,
                CountryArray.populateCountry());
        countryInput.setAdapter(countryAdapter);
        */

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
            // Add action buttons
            .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    keyword = keywordInput.getText().toString();
                    keywordFilter = ((KeywordFilter)keywordFilterInput.getSelectedItem()).id;
                    salaryMin = Integer.valueOf(salaryMinInput.getText().toString());
                    salaryMax = Integer.valueOf(salaryMaxInput.getText().toString());
                    
                    // Send the positive button event back to the host activity
                    mListener.onDialogPositiveClick(JobSearchFilterDialog.this);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    JobSearchFilterDialog.this.getDialog().cancel();
                }
            });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (JobSearchFilterListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }
}
