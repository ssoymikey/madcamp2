package com.example.project1_test3;

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
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Dictionary> mList;
    private Context mContext;
    static int count = -1;
    static String userName;
    static String userNumber;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView photo;
        protected TextView phoneNumber;
        protected TextView name;
        protected ImageButton callButton;


        public CustomViewHolder(View view) {
            super(view);
            this.photo = view.findViewById(R.id.id_listitem);
            this.phoneNumber = view.findViewById(R.id.phone_listitem);
            this.name = view.findViewById(R.id.name_listitem);
            this.callButton = view.findViewById(R.id.call_button);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ImageButton ButtonSubmit;
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

                    userName = getUserInfo(mList.get(getAdapterPosition()));
                    userNumber = mList.get(getAdapterPosition()).getUser_phNumber();

                    final AlertDialog dialog = builder.create();
                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String strID = mList.get(getAdapterPosition()).getId();
                            String strNAME = editTextNAME.getText().toString();
                            String strPHONE = editTextPHONE.getText().toString();

                            Dictionary dict = new Dictionary(mList.get(getAdapterPosition()).getPersonId(), strID, strNAME, strPHONE);
                            System.out.println("dict의 personId : " + dict.getPersonId());
                            mList.set(getAdapterPosition(), dict);
                            System.out.println("mList personId : " + mList.get(getAdapterPosition()).getPersonId());
                            notifyItemChanged(getAdapterPosition());
                            dialog.dismiss();

                            updateContact2(mContext, mContext.getContentResolver(), mList.get(getAdapterPosition()), userName);
                            //updateContact3(mContext.getContentResolver(), mList.get(getAdapterPosition()), userName, userNumber);
                        }
                    });

                    dialog.show();
                }
            });
        }
    }

    private static void deleteContact(ContentResolver contactHelper, long getContactId) {
        System.out.println("Contact ID : " + getContactId);
        String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
        contactHelper.delete(ContactsContract.RawContacts.CONTENT_URI, where, null);
    }

    private static String getUserInfo(Dictionary dictionary) {
        return dictionary.getUser_Name();

    }

    private static void updateContact2(Context context, ContentResolver contactHelper, Dictionary dictionary, String userName) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.RawContacts.CONTACT_ID
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                count++;
                String getName = cursor.getString(0);
                long getContactId = cursor.getLong(1);
                if (getName.equals(userName)) {
                    String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
                    ContentValues values = new ContentValues();
                    System.out.println("name: " + userName);
                    System.out.println("getContactId : " + getContactId);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, dictionary.getUser_Name().substring(1));
                    values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, dictionary.getUser_Name().substring(0, 1));
                    //values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, dictionary.getUser_Name());
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, dictionary.getUser_phNumber());
                    contactHelper.update(ContactsContract.Data.CONTENT_URI, values, where, null);
                }
            } cursor.close();
        }
    }

    private static void updateContact3(ContentResolver contactHelper, Dictionary dictionary, String userName, String userNumber) {
//        String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
        String selectPhone = ContactsContract.CommonDataKinds.Phone.NUMBER + " = " + userNumber;
        String selectName = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME + " = " + userName;
//        String nameWhere = ContactsContract.Data.CONTACT_ID + " = " + getContactId + " AND " + ContactsContract.Data.MIMETYPE + " = " + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
//        String phoneWhere = ContactsContract.Data.CONTACT_ID + " = " + getContactId + " AND " + ContactsContract.Data.MIMETYPE + " = " + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
//        String[] nameArgs = new String[] {String.valueOf(getContactId), String.valueOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)};
//        String[] phoneArgs = new String[] {String.valueOf(getContactId), String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)};

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ContentProviderOperation.Builder op;

        //ContentProviderOperation.Builder op = ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI)
          //      .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            //    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        //ops.add(op.build());

        op = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectName, null)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, dictionary.getUser_Name());
        ops.add(op.build());

        op = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, null)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, dictionary.getUser_phNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        op.withYieldAllowed(true);
        ops.add(op.build());

        System.out.println("name: " + dictionary.getUser_Name());
        //System.out.println("getContactId : " + getContactId);
        System.out.println("display name : " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        System.out.println("phoneNumber : " + dictionary.getUser_phNumber());
        Log.d(TAG, "Creating contact : " + dictionary.getUser_Name());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, "Exception encountered while updating contact: " + e);
        }

    }

    private static void updateContact4(ContentResolver contactHelper, Dictionary dictionary, long getContactId) {
        String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
        String nameWhere = ContactsContract.Data.CONTACT_ID + " = " + getContactId + " AND " + ContactsContract.Data.MIMETYPE + " = " + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
        String phoneWhere = ContactsContract.Data.CONTACT_ID + " = " + getContactId + " AND " + ContactsContract.Data.MIMETYPE + " = " + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        String[] nameArgs = new String[] {String.valueOf(getContactId), String.valueOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)};
        String[] phoneArgs = new String[] {String.valueOf(getContactId), String.valueOf(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)};
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, null)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, dictionary.getUser_Name()).build());
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, null)
                .withValue(ContactsContract.CommonDataKinds.Phone.DATA, dictionary.getUser_phNumber()).build());

        Log.d(TAG, "Creating contact : " + dictionary.getUser_Name());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, "Exception encountered while updating contact: " + e);
        }

    }

    private static void updateContactName(ContentResolver contactHelper, String name, long getContactId) {
        String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, null)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

        Log.d(TAG, "Creating contact : " + name);
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, "Exception encountered while updating contact: " + e);
        }
    }

    private static void updateContactPhone(ContentResolver contactHelper, String phone, long getContactId) {
        String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, null)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone).build());

        Log.d(TAG, "Creating contact number : " + phone);
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, "Exception encountered while updating contact: " + e);
        }
    }



//    public CustomAdapter(ArrayList<Dictionary> list) {
//        this.mList = list;
//    }



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

        long photo_id = mList.get(position).getPhotoId();

        if (photo_id != 0) {
            //Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photo_id);
            //System.out.println("Uri : " + uri);
            //Glide.with(mContext).load(uri).placeholder(R.drawable.loading_image).override(120, 120).dontAnimate().into(viewholder.photo);
            byte[] photoBytes = null;
            Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
            Cursor c = mContext.getContentResolver().query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO},
                    null,null, null);

            try {
                if(c.moveToFirst())
                    photoBytes = c.getBlob(0);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                c.close();
            }

            viewholder.photo.setPadding(0,0,0,0);
            viewholder.photo.setBackground(new ShapeDrawable(new OvalShape()));
            if(Build.VERSION.SDK_INT >= 21) {
                viewholder.photo.setClipToOutline(true);
            }
            Glide.with(mContext).load(photoBytes).placeholder(R.drawable.loading_image).override(120, 120).dontAnimate().into(viewholder.photo);
        } else {
            viewholder.photo.setPadding(15,15,15,15);
            viewholder.photo.setBackground(null);
            if(Build.VERSION.SDK_INT >= 21) {
                viewholder.photo.setClipToOutline(true);
            }
            Glide.with(mContext).load(R.drawable.user_b).placeholder(R.drawable.loading_image).override(120, 120).dontAnimate().into(viewholder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}