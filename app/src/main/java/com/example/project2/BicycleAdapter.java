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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project2.Fragment.AddressFragment;
import com.google.android.gms.maps.CameraUpdateFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;


public class BicycleAdapter extends RecyclerView.Adapter<BicycleAdapter.CustomViewHolder> {

    private ArrayList<Bicycle> mList;
    private Context mContext;
    private String userPhone;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView end;
        protected TextView name;
        protected TextView avail;

        public CustomViewHolder(View view) {
            super(view);
            this.end = view.findViewById(R.id.dest);
            this.name = view.findViewById(R.id.name);
            this.avail = view.findViewById(R.id.avail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button ButtonSubmit;
                    final TextView startEnd;
                    final TextView name;
                    final TextView phone;
                    final TextView pwd;

                    if(mList.get(getAdapterPosition()).getAvail().equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        v = LayoutInflater.from(mContext).inflate(R.layout.borrow_box, null, false);
                        builder.setView(v);
                        ButtonSubmit = v.findViewById(R.id.confirm);
                        startEnd = v.findViewById(R.id.start_end);
                        name = v.findViewById(R.id.name);
                        phone = v.findViewById(R.id.phone);
                        pwd = v.findViewById(R.id.password);

                        startEnd.setText(mList.get(getAdapterPosition()).getStart() + " -> " + mList.get(getAdapterPosition()).getEnd());
                        name.setText(mList.get(getAdapterPosition()).getUser_Name());
                        phone.setText(mList.get(getAdapterPosition()).getUser_phNumber());
                        pwd.setText(mList.get(getAdapterPosition()).getPwd());

                        JSONObject Registerinfo = new JSONObject();
                        try {
                            Registerinfo.put("phone", mList.get(getAdapterPosition()).getUser_phNumber());
                            Registerinfo.put("rentphone", userPhone);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // send to server
                        final String json = Registerinfo.toString();

                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SetContactsAsyncTask task = new SetContactsAsyncTask("PUT", "http://192.249.19.251:980/setfalse");
                                try {
                                    boolean success = task.execute(json).get();
                                    //Toast.makeText(v.getContext(), "PUT to MongoDB!! : " + success, Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                ArrayList<Bicycle> bicycles = new ArrayList<Bicycle>();

                                GetBikeAsyncTask gettask = new GetBikeAsyncTask("http://192.249.19.251:980/bike");
                                try {
                                    bicycles = gettask.execute(mList.get(getAdapterPosition()).getStart()).get();
                                    //Toast.makeText(getApplicationContext(), "GET to MongoDB!!", Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                addItem(bicycles);
                                notifyDataSetChanged();

                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    } else {
                        Toast.makeText(v.getContext(), "자전거가 이미 사용중입니다!", Toast.LENGTH_SHORT).show();
                    }
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

    public BicycleAdapter(Context context, ArrayList<Bicycle> list, String userPhone) {
        mList = list;
        mContext = context;
        this.userPhone = userPhone;
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

        if(mList.get(position).getAvail().equals("1")) {
            viewholder.avail.setText("O");
        } else {
            viewholder.avail.setText("X");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}