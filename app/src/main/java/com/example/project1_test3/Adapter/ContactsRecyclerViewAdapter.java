package com.example.project1_test3.Adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project1_test3.ContactItem;
import com.example.project1_test3.R;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ContactItem> list;
    private final Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1, textView2;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.text1);
            textView2 = itemView.findViewById(R.id.text2);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public ContactsRecyclerViewAdapter(Context context, ArrayList<ContactItem> list) {
        this.mContext = context;
        this.list = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ContactsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_item, viewGroup, false) ;
        return new ContactsRecyclerViewAdapter.ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ContactsRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        ContactItem contacts = list.get(position);
        viewHolder.textView1.setText(contacts.getUser_Name());
        viewHolder.textView2.setText(contacts.getUser_phNumber());
        viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_foreground));
        Bitmap profile = loadContactPhoto(mContext.getContentResolver(),contacts.getPerson_id(), contacts
                .getPhoto_id());
        if (profile != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.imageView.setBackground(new ShapeDrawable(new OvalShape()));
                viewHolder.imageView.setClipToOutline(true);
            }
            viewHolder.imageView.setImageBitmap(profile);
        } else {
//            viewHolder.profile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_profile_thumnail));
            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.imageView.setClipToOutline(false);
            }
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public Bitmap loadContactPhoto(ContentResolver cr, long id, long photo_id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input != null)
            return resizingBitmap(BitmapFactory.decodeStream(input));
        else
            Log.d("PHOTO", "first try failed to load photo");

        byte[] photoBytes = null;
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
        Cursor c = cr.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        if (photoBytes != null)
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        else
            Log.d("PHOTO", "second try also failed");
        return null;
    }

    public Bitmap resizingBitmap(Bitmap oBitmap) {
        if (oBitmap == null)
            return null;
        float width = oBitmap.getWidth();
        float height = oBitmap.getHeight();
        float resizing_size = 120;
        Bitmap rBitmap = null;
        if (width > resizing_size) {
            float mWidth = (float) (width / 100);
            float fScale = (float) (resizing_size / mWidth);
            width *= (fScale / 100);
            height *= (fScale / 100);
        }

        // Log.d("rBitmap : " + width + ", " + height);
        rBitmap = Bitmap.createScaledBitmap(oBitmap, (int) width, (int) height, true);
        return rBitmap;
    }
}