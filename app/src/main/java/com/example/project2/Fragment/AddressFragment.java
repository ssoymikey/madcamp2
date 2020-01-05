package com.example.project2.Fragment;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project2.CustomAdapter;
import com.example.project2.Dictionary;
import com.example.project2.GetContactsAsyncTask;
import com.example.project2.R;
import com.example.project2.SetContactsAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AddressFragment extends Fragment {
    private ArrayList<Dictionary> mArrayList;
    private CustomAdapter mAdapter;

    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_address, container, false);

        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        mAdapter = new CustomAdapter(v.getContext(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        //getDictionaryList();
        loadContectFromServer(v);
        mAdapter.notifyDataSetChanged();

        final SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mArrayList.clear();
                //getDictionaryList();
                loadContectFromServer(v);
                mAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Button buttonInsert = v.findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.edit_box, null, false);
                builder.setView(view);

                final Button ButtonSubmit = view.findViewById(R.id.button_dialog_submit);
                final EditText editTextNAME = view.findViewById(R.id.edittext_dialog_name);
                final EditText editTextPHONE = view.findViewById(R.id.edittext_dialog_phone);

                editTextPHONE.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

                final AlertDialog dialog = builder.create();

                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        // 4. 사용자가 입력한 내용을 가져와서
                        String strName = editTextNAME.getText().toString();
                        String strPhone = editTextPHONE.getText().toString();

                        insertContact(v.getContext().getContentResolver(), strName, strPhone);
                        //saveContact(getContentResolver(), strName, strPhone);
                        long id = findIDbyPhone(strPhone);

                        // 5. ArrayList에 추가하고
                        Dictionary dict = new Dictionary(id, strName, strPhone);
                        //mArrayList.add(0, dict); //첫번째 줄에 삽입됨
                        //mArrayList.add(dict); //마지막 줄에 삽입됨

                        // 서버에 삽입된 contact 전송하고
                        ArrayList<Dictionary> tempList = new ArrayList<>();
                        tempList.add(dict);
                        String json = makeJSONString(tempList);
                        SetContactsAsyncTask task = new SetContactsAsyncTask("PUT", "http://192.249.19.251:980/contact");
                        try {
                            boolean success = task.execute(json).get();
                            Toast.makeText(v.getContext(), "PUT to MongoDB!! : "+success, Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        // 6. 어댑터에서 RecyclerView에 반영하도록 합니다.
                        loadContectFromServer(v);

                        //mAdapter.notifyItemInserted(0);
                        mAdapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        // 동기화 버튼
        final Button serverBtn = v.findViewById(R.id.serverbutton);
        serverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json;

                mArrayList.clear();
                json = getDictionaryListJson();

                SetContactsAsyncTask task = new SetContactsAsyncTask("POST", "http://192.249.19.251:980/contact");
                try {
                    boolean success = task.execute(json).get();
                    Toast.makeText(v.getContext(), "POST to MongoDB!! : "+success, Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            AlertDialog.Builder del_builder = new AlertDialog.Builder(v.getContext());
            v = LayoutInflater.from(v.getContext()).inflate(R.layout.delete_box, null, false);
            del_builder.setView(v);
            final Button ButtonSubmit = v.findViewById(R.id.button_del);

            final AlertDialog del_dialog = del_builder.create();
            ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long mid = mArrayList.get(position).getPersonId();

                    ArrayList<Dictionary> tempList = new ArrayList<>();
                    tempList.add(mArrayList.get(position));

                    // 삭제 정보 서버에 보내기
                    String json = makeJSONString(tempList);
                    SetContactsAsyncTask task = new SetContactsAsyncTask("DELETE", "http://192.249.19.251:980/contact");
                    try {
                        boolean success = task.execute(json).get();
                        Toast.makeText(v.getContext(), "DELETE to MongoDB!! : "+success, Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    System.out.println("mid 값 : " + mid);
                    deleteContact(v.getContext().getContentResolver(), mid);

                    //mArrayList.remove(position);
                    //mAdapter.notifyItemRemoved(position);

                    del_dialog.dismiss();
                    loadContectFromServer(v);

                    mAdapter.notifyDataSetChanged();
                }
            });

            del_dialog.show();

            mAdapter.notifyDataSetChanged();
        }
    };

    public void loadContectFromServer(View v){
        GetContactsAsyncTask task = new GetContactsAsyncTask();
        try {
            mArrayList.clear();
            System.out.println("load start");
            ArrayList<Dictionary> returnValues = task.execute().get();
            System.out.println("load end");

            for(int i=0;i<returnValues.size();i++) {
                Dictionary FetchedData = returnValues.get(i);
                mArrayList.add(FetchedData);
            }

            Toast.makeText(v.getContext(), "Fetched from MongoDB!!", Toast.LENGTH_SHORT).show();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String getDictionaryListJson() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.RawContacts.CONTACT_ID,
        };
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " asc";
        String json = new String();

        Cursor cursor = v.getContext().getContentResolver().query(uri, projection, null, selectionArgs, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                long person = cursor.getLong(2);
                Dictionary dictionary = new Dictionary();
                dictionary.setUser_Name(cursor.getString(1));
                dictionary.setUser_phNumber(cursor.getString(0));
                dictionary.setPersonId(person);

                if (dictionary.getUser_phNumber().startsWith("01")) {
                    mArrayList.add(dictionary);
                    Log.d("<<CONTACTS", "name=" + dictionary.getUser_Name() + ", phone = " + dictionary.getUser_phNumber() + ", personId = " + dictionary.getPersonId());
                }
            }
            json = makeJSONString(mArrayList);
            cursor.close();
        }
        return json;
    }

    public static String makeJSONString(ArrayList<Dictionary> dicArr) {
        String json;
        JSONArray arr = new JSONArray();
        JSONObject univ = new JSONObject();
        JSONObject jsDict;
        Dictionary dictionary;

        for(int i=0;i<dicArr.size();i++) {
            dictionary = dicArr.get(i);
            jsDict = new JSONObject();

            try {
                jsDict.put("personID", dictionary.getPersonId());
                jsDict.put("name", dictionary.getUser_Name());
                jsDict.put("phone", dictionary.getUser_phNumber());

                arr.put(jsDict);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            univ.put("DB_Input", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        json = univ.toString();

        System.out.println(json);

        return json;
    }


    public static boolean insertContact(ContentResolver contactHelper, String name, String phoneNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ContentProviderOperation.Builder op;

        op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        ops.add(op.build());

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        ops.add(op.build());

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        op.withYieldAllowed(true);
        ops.add(op.build());


        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e("ContactsAdder", "Exception: " + e);
            return false;
        }
        return true;
    }

    private void deleteContact(ContentResolver contactHelper, long id) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.RawContacts.CONTACT_ID
        };

        Cursor cursor = v.getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                long getContactId = cursor.getLong(0);
                if (getContactId == id) {
                    System.out.println("Contact ID : " + getContactId);
                    String where = ContactsContract.RawContacts.CONTACT_ID + " = " + getContactId;
                    contactHelper.delete(ContactsContract.RawContacts.CONTENT_URI, where, null);
                    System.out.println("where : " + where);
                }
            } cursor.close();
        }
    }

    private long findIDbyPhone(String phone) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.RawContacts.CONTACT_ID,
        };
        long result = -1;

        Cursor cursor = v.getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                String getPhone = cursor.getString(0);
                long getContactId = cursor.getLong(1);
                if (getPhone.equals(phone)) {
                    System.out.println("Contact ID : " + getContactId);
                    result = getContactId;
                }
            } cursor.close();
        }

        return result;
    }
}
