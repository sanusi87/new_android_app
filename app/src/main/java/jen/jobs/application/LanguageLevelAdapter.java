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
import java.util.Objects;

public class LanguageLevelAdapter extends BaseAdapter implements ListAdapter{
    public ArrayList<LanguageLevel> language = new ArrayList<>();
    private Context context;

    public LanguageLevelAdapter(Context context){
        this.context = context;

        ArrayList<LanguageLevel> tempArr = new ArrayList<>();
        HashMap fields = Jenjobs.getLanguageLevel();
        for (Object o : fields.entrySet()) {
            HashMap.Entry e = (HashMap.Entry) o;
            tempArr.add(new LanguageLevel((int) e.getKey(), String.valueOf(e.getValue())));
        }

        Collections.sort(tempArr, new Comparator<LanguageLevel>() {
            @Override
            public int compare(LanguageLevel lhs, LanguageLevel rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (Objects.equals(t[0], lhs.name)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        language = tempArr;
    }

    @Override
    public int getCount() {
        return language.size();
    }

    @Override
    public Object getItem(int position) {
        return language.get(position);
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
            v = vi.inflate(R.layout.spinner_item, parent, false);
        }
        LanguageLevel c = (LanguageLevel) getItem(position);
        TextView tvName = (TextView) v.findViewById(R.id.spinner_item);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< language.size();i++){
            if( language.get(i).id == theCode ){
                index = i;
                break;
            }
        }
        return index;
    }
}
