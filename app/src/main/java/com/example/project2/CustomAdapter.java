package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Dictionary> mList;
    private Context mContext;
    static long userID;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView phoneNumber;
        protected TextView name;
        protected Button callButton;

        public CustomViewHolder(View view) {
            super(view);
            this.phoneNumber = view.findViewById(R.id.phone_listitem);
            this.name = view.findViewById(R.id.name_listitem);
            this.callButton = view.findViewById(R.id.call_button);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button ButtonSubmit;
                    final EditText editTextNAME;
                    final EditText editTextPHONE;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    v = LayoutInflater.from(mContext).inflate(R.layout.edit_box, null, false);
                    builder.setView(v);
                    ButtonSubmit = v.findViewById(R.id.button_dialog_submit);
                    editTextNAME = v.findViewById(R.id.edittext_dialog_name);
                    editTextPHONE = v.findViewById(R.id.edittext_dialog_phone);

                    editTextNAME.setText(mList.get(getAdapterPosition()).getUser_Name());
                    editTextPHONE.setText(mList.get(getAdapterPosition()).getUser_phNumber());

                    editTextPHONE.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

                    userID = mList.get(getAdapterPosition()).getPersonId();

                    final AlertDialog dialog = builder.create();
                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String strNAME = editTextNAME.getText().toString();
                            String strPHONE = editTextPHONE.getText().toString();

                            Dictionary dict = new Dictionary(mList.get(getAdapterPosition()).getPersonId(), strNAME, strPHONE);
                            System.out.println("dict의 personId : " + dict.getPersonId());
                            mList.set(getAdapterPosition(), dict);

                            ArrayList<Dictionary> tempList = new ArrayList<>();
                            tempList.add(dict);
                            String json = AddressFragment.makeJSONString(tempList);
                            SetContactsAsyncTask task = new SetContactsAsyncTask("PUT", "http://192.249.19.251:980/contactedit");
                            try {
                                boolean success = task.execute(json).get();
                                Toast.makeText(v.getContext(), "PUT to MongoDB!! : "+success, Toast.LENGTH_SHORT).show();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            System.out.println("mList personId : " + mList.get(getAdapterPosition()).getPersonId());
                            notifyItemChanged(getAdapterPosition());
                            dialog.dismiss();

                            updateContact2(mContext, mContext.getContentResolver(), mList.get(getAdapterPosition()), userID);
                            //updateContact3(mContext.getContentResolver(), mList.get(getAdapterPosition()), userName, userNumber);
                        }
                    });

                    dialog.show();
                }
            });
        }
    }

    private static void updateContact2(Context context, ContentResolver contactHelper, Dictionary dictionary, long userID) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.RawContacts.CONTACT_ID
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                String getName = cursor.getString(0);
                long getContactId = cursor.getLong(1);
                if (getContactId == userID) {
                    String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
                    ContentValues values = new ContentValues();
                    System.out.println("name: " + getName);
                    System.out.println("getContactId : " + getContactId);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, dictionary.getUser_Name().substring(1));
                    values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, dictionary.getUser_Name().substring(0, 1));
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, dictionary.getUser_phNumber());
                    contactHelper.update(ContactsContract.Data.CONTENT_URI, values, where, null);
                }
            } cursor.close();
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    public CustomAdapter(Context context, ArrayList<Dictionary> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        final int pos = position;

        viewholder.phoneNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        viewholder.phoneNumber.setGravity(Gravity.CENTER);
        viewholder.name.setGravity(Gravity.CENTER);

        viewholder.phoneNumber.setText(mList.get(position).getUser_phNumber());
        viewholder.name.setText(mList.get(position).getUser_Name());

        viewholder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.checkSelfPermission(Manifest.permission.CALL_PHONE + "");
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mList.get(pos).getUser_phNumber()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}