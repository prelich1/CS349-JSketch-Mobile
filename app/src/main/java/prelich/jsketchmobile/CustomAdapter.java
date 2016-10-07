package prelich.jsketchmobile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

// Adapter used for the thickness list
// Base code taken from Android SDK and modified as needed

public class CustomAdapter extends BaseAdapter{
    String [] result;
    Context context;
    int [] imageId;
    int selectedIndex = 0;
    private static LayoutInflater inflater = null;

    private Model model;

    public CustomAdapter(Context mainActivity, Model m) {
        int [] imgid={
                R.drawable.thickness_1,
                R.drawable.thickness_2,
                R.drawable.thickness_3,
                R.drawable.thickness_4,
        };
        String [] prgmNameList={"1", "2", "3", "4"};

        model = m;
        context = mainActivity;
        imageId = imgid;
        inflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.thickness_list, null);

        holder.img=(ImageView) rowView.findViewById(R.id.icon);
        holder.img.setImageResource(imageId[position]);

        if (position == selectedIndex) {
            rowView.setBackgroundColor(Color.parseColor("#23b989"));
        } else {
            rowView.setBackgroundColor(Color.alpha(0));
        }

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(position);
            }
        });

        return rowView;
    }

    public int setSelected(int position) {
        selectedIndex = position;
        // Only update model if new thickness selected
        // to avoid endless update loop
        if (model.getCurrentThickness() != position) {
            model.setCurrentThickness(position);
            if(model.getCurrentTool() == tool.SELECT) {
                model.setSelectedShapeThickness(position);
            }
        }

        notifyDataSetChanged();
        return selectedIndex;
    }
}