package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*
* adapter to bind to list of selected job spec in JobSearch
* when clicked, open SelectJobRole + intent jobSpecId for specific job spec
* */

public class SelectedJobSpecAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<JobSpec> listOfJobSpec;
    private ArrayList<ArrayList<JobRole>> listOfJobRole;

    public SelectedJobSpecAdapter(Context context) {
        this.context = context;
        listOfJobSpec = new ArrayList<>();
        listOfJobRole = new ArrayList<>();
    }

    public void setJobSpec( ArrayList<JobSpec> _jobSpec ){
        listOfJobSpec.clear();
        listOfJobSpec.addAll(_jobSpec);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listOfJobSpec.size();
    }

    @Override
    public JobSpec getItem(int position) {
        return listOfJobSpec.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.list_of_selected_job_spec, parent, false);
        }

        JobSpec js = getItem(position);

        ((TextView)v.findViewById(R.id.currentJobSpec)).setText(js.name);
        ((TextView)v.findViewById(R.id.currentJobRole)).setText("Click to select job role");
        //Log.e("listOfJobRole.size", ""+listOfJobRole.size());
        if( listOfJobRole.size() > 0 ){
            //Log.e("position", ""+position);
            //for(int i=0;i<listOfJobRole.size();i++){
                //Log.e("index", ""+i);
                //ArrayList<JobRole> _savedRole = listOfJobRole.get(i);
                //Log.e("index", ""+_savedRole.toString());
                //for(JobRole r : _savedRole){
                    //Log.e("r.name", ""+r.name);
                //}
            //}
            ArrayList<JobRole> savedRole = listOfJobRole.get(position);
            //Log.e("savedRole.size", "" + savedRole.size());
            if( savedRole.size() > 0 ){
                ArrayList<String> jobRoleNames = new ArrayList<>();
                for(JobRole r : savedRole){
                    jobRoleNames.add(r.name);
                }
                //Log.e("jobRoleNames", TextUtils.join(",", jobRoleNames));
                ((TextView) v.findViewById(R.id.currentJobRole)).setText(TextUtils.join(",", jobRoleNames));
            }
        }
        v.setTag(R.id.TAG_JOB_ROLE_ID, js);

        return v;
    }

    public void resetJobSpec(){
        listOfJobSpec.clear();
    }

    public void addJobSpec(JobSpec c) {
        listOfJobSpec.add(c);
    }

    public void setJobRole(int viewIndex, ArrayList<JobRole> jobRoles){
        try{
            listOfJobRole.remove(viewIndex);
        }catch(IndexOutOfBoundsException e){
            //
        }
        listOfJobRole.add(viewIndex, jobRoles);
    }
}
