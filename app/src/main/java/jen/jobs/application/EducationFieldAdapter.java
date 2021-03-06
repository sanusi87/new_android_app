package jen.jobs.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class EducationFieldAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<EducationField> eduField;
    private Context context;
    private boolean single = false;

    public EducationFieldAdapter( Context context ){
        this.context = context;

        HashMap<Integer, String> fields = Jenjobs.getEducationField();
        ArrayList<EducationField> tempArr = new ArrayList<>();
        eduField = new ArrayList<>();

        for (Object o : fields.entrySet()) {
            HashMap.Entry e = (HashMap.Entry) o;
            tempArr.add(new EducationField((int) e.getKey(), String.valueOf(e.getValue())));
        }

        Collections.sort(tempArr, new Comparator<EducationField>() {
            @Override
            public int compare(EducationField lhs, EducationField rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (t[0].equals(lhs.name)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        eduField = tempArr;
    }

    @Override
    public int getCount() {
        return eduField.size();
    }

    @Override
    public Object getItem(int position) {
        return eduField.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView tvName;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            if( this.single ){
                v = vi.inflate(R.layout.spinner_item, parent, false);
            }else{
                v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }
        }

        if( this.single ){
            tvName = (TextView) v.findViewById(R.id.spinner_item);
        }else{
            tvName = (TextView) v.findViewById(android.R.id.text1);
            tvName.setTextAppearance(context, R.style.AppThemeBaseLabel);
            tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        }

        EducationField c = (EducationField) getItem(position);
        tvName.setText(c.name);
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public void setSingle(boolean single){
        this.single = single;
    }
}
