package com.example.project2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project2.Fragment.AddressFragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;


public class BicycleAdapter extends RecyclerView.Adapter<BicycleAdapter.CustomViewHolder> {

    private ArrayList<Bicycle> mList;
    private Context mContext;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView end;
        protected TextView name;

        public CustomViewHolder(View view) {
            super(view);
            this.end = view.findViewById(R.id.dest);
            this.name = view.findViewById(R.id.name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button ButtonSubmit;
                    final TextView startEnd;
                    final TextView name;
                    final TextView phone;
                    final TextView pwd;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    v = LayoutInflater.from(mContext).inflate(R.layout.borrow_box, null, false);
                    builder.setView(v);
                    ButtonSubmit = v.findViewById(R.id.confirm);
                    startEnd = v.findViewById(R.id.start_end);
                    name = v.findViewById(R.id.name);
                    phone = v.findViewById(R.id.phone);
                    pwd = v.findViewById(R.id.password);

                    startEnd.setText( mList.get(getAdapterPosition()).getStart() + " -> " + mList.get(getAdapterPosition()).getEnd());
                    name.setText(mList.get(getAdapterPosition()).getUser_Name());
                    phone.setText(mList.get(getAdapterPosition()).getUser_phNumber());
                    pwd.setText(mList.get(getAdapterPosition()).getPwd());

                    final AlertDialog dialog = builder.create();
                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_cycle_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    public BicycleAdapter(Context context, ArrayList<Bicycle> list) {
        mList = list;
        mContext = context;
    }

    public void addItem(ArrayList<Bicycle> bicycles) {
        mList = bicycles;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        final int pos = position;

        viewholder.end.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        viewholder.end.setGravity(Gravity.CENTER);
        viewholder.name.setGravity(Gravity.CENTER);

        viewholder.end.setText(mList.get(position).getEnd());
        viewholder.name.setText(mList.get(position).getUser_Name());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}