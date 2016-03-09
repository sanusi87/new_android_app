package jen.jobs.application;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;

public class ImageAdapter extends BaseAdapter implements ListAdapter{

    Activity activity;
    String[] fileList;
    private File currentPath;

    public ImageAdapter(){}

    public void setCurrentPath(File currentPath){
        this.currentPath = currentPath;
    }

    public void setFileList(String[] fileList){
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileList.length;
    }

    @Override
    public Object getItem(int position) {
        return fileList[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String fileName = fileList[position];

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(activity);
            v = vi.inflate(R.layout.spinner_item_with_image, parent, false);
        }

        TextView imageName = (TextView)v.findViewById(R.id.imageName);
        ImageView imagePreview = (ImageView)v.findViewById(R.id.imagePreview);
        imageName.setText(fileName);

        File newFile = new File(currentPath, fileName);
        if( newFile.isFile() ){
            Bitmap bitmap = decodeSampledBitmapFromResource(newFile.getPath(), 100, 100);
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageBitmap(bitmap);
        }else{
            imagePreview.setVisibility(View.GONE);
        }

        return v;
    }

    // START: reduce memory consumption
    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String filePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    // END: reduce memory consumption
}
