package jen.jobs.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CurrencyAdapter extends BaseAdapter implements ListAdapter{
    public ArrayList<MyCurrency> currencies = new ArrayList<>();
    private Context context;

    public CurrencyAdapter(Context context){
        this.context = context;

        HashMap fields = Jenjobs.getCurrency();
        //ArrayList<JobSeekingStatus> tempArr = new ArrayList<>();

        for (Object o : fields.entrySet()) {
            HashMap.Entry e = (HashMap.Entry) o;
            //tempArr.add(new JobSeekingStatus( (int)e.getKey(), String.valueOf(e.getValue()) ));
            currencies.add(new MyCurrency((int) e.getKey(), String.valueOf(e.getValue())));
        }
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @Override
    public Object getItem(int position) {
        return currencies.get(position);
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
            v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        MyCurrency c = (MyCurrency) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< this.currencies.size();i++){
            if( this.currencies.get(i).id == theCode ){
                index = i;
                break;
            }
        }
        return index;
    }
}
