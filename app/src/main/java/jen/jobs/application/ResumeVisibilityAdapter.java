package jen.jobs.application;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ResumeVisibilityAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    public String[] visibility = {"Open","Limited","Hidden"};
    public String[] visibilityDesc = {
            "You can apply jobs<br />Employers can search your resume<br />Employers can contact you for job opportunities",
            "You can apply jobs<br />Employers can search your resume but cannot see your contact details<br />Employers cannot contact you without your approval",
            "You can apply jobs<br />Employers cannot search for your resume<br />Employers cannot contact you for job opportunities"
    };

    private ArrayList<String> vis = new ArrayList<>();

    public ResumeVisibilityAdapter(Context context) {
        this.context = context;
        Collections.addAll(vis, visibility);
    }

    @Override
    public int getCount() {
        return vis.size();
    }

    @Override
    public Object getItem(int position) {
        return vis.get(position);
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
            v = vi.inflate(R.layout.resume_visibility_item, parent, false);
        }
        String visibility = (String) getItem(position);
        TextView tvName = (TextView) v.findViewById(R.id.visibility_name);
        tvName.setText(visibility);

        TextView tvDesc = (TextView) v.findViewById(R.id.visibility_desc);
        tvDesc.setText( Html.fromHtml( visibilityDesc[position] ) );

        return v;
    }
}
