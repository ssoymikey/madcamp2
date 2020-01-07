package com.example.project2;

import android.content.Context;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.project2.Fragment.AddressFragment.makeJSONString;
import static java.security.AccessController.getContext;

public class kicycle_Activity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {

    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions myLocationMarker;

    private ArrayList<Bicycle> mArrayList;
    private BicycleAdapter mAdapter;
    RecyclerView mRecyclerView;
    LinearLayout bikeInfoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kicycle_main);

        Button button_register = findViewById(R.id.register);
        ImageView button_profile = findViewById(R.id.profile);
        ImageView button_alimi = findViewById(R.id.alimi);

        Intent intent = getIntent();
        final String UserID = intent.getExtras().getString("UserID");

        button_profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), UserID, Toast.LENGTH_SHORT).show();
            }
        });

        button_alimi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.alimi, null, false);
                builder.setView(view);

                final AlertDialog dialog = builder.create();
                final Button alimi_close = view.findViewById(R.id.register_close);
                alimi_close.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();



            }
        });

        //Register button Listner
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.register_box, null, false);
                builder.setView(view);

                final Button register_submit = view.findViewById(R.id.register_submit);
                final Button register_close = view.findViewById(R.id.register_close);
                final EditText register_name = view.findViewById(R.id.register_name);
                final EditText register_phone = view.findViewById(R.id.register_phone);
                final EditText register_password = view.findViewById(R.id.register_password);
                final Spinner startpoint = view.findViewById(R.id.register_startpoint);
                final Spinner endpoint = view.findViewById(R.id.register_endpoint);

                register_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

                final AlertDialog dialog = builder.create();

                final Object[] rentpoint_name = new Object[2];

                startpoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        rentpoint_name[0] = parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                endpoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        rentpoint_name[1] = parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                register_close.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //register submit button listner
                register_submit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        // 4. 사용자가 입력한 내용을 가져와서
                        String strName = register_name.getText().toString();
                        String strPhone = register_phone.getText().toString();
                        String strPassword = register_password.getText().toString();
                        String strStartpoint = rentpoint_name[0].toString();
                        String strEndpoint = rentpoint_name[1].toString();

                        JSONObject Registerinfo = new JSONObject();
                        try {
                            Registerinfo.put("name", strName);
                            Registerinfo.put("phone", strPhone);
                            Registerinfo.put("password", strPassword);
                            Registerinfo.put("startpoint", strStartpoint);
                            Registerinfo.put("endpoint", strEndpoint);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // send to server
                        String json = Registerinfo.toString();
                        SetContactsAsyncTask task = new SetContactsAsyncTask("PUT", "http://192.249.19.251:980/register");
                        try {
                            boolean success = task.execute(json).get();
                            Toast.makeText(v.getContext(), "PUT to MongoDB!! : "+success, Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMyLocationEnabled(true);
                showRentPlaceMarker();
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Button backbutton = findViewById(R.id.back_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bikeInfoLayout.setVisibility(View.INVISIBLE);
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_reverse);
                bikeInfoLayout.startAnimation(anim);
            }
        });

        bikeInfoLayout = findViewById(R.id.third_layout);
        mRecyclerView = findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mArrayList = new ArrayList<Bicycle>();
        mAdapter = new BicycleAdapter(getApplicationContext(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
        requestMyLocation();
    }

    private void requestMyLocation() {
        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            long minTime = 1000000;
            float minDistance = 0;
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) { }

                        @Override
                        public void onProviderEnabled(String provider) { }

                        @Override
                        public void onProviderDisabled(String provider) { }
                    });

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                showCurrentLocation(lastLocation);
            }

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) { }

                        @Override
                        public void onProviderEnabled(String provider) { }

                        @Override
                        public void onProviderDisabled(String provider) { }
                    });

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 16));
    }

    private void showRentPlaceMarker() {
        myLocationMarker = new MarkerOptions();
        myLocationMarker.position(new LatLng(36.373986, 127.356660));
        myLocationMarker.title("아름관");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.370347, 127.362593));
        myLocationMarker.title("창의관");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.373802, 127.359253));
        myLocationMarker.title("카이마루");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.374369, 127.365635));
        myLocationMarker.title("N1");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.373482, 127.362643));
        myLocationMarker.title("인사동");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.371174, 127.357922));
        myLocationMarker.title("노천극장");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.368467, 127.362627));
        myLocationMarker.title("오리연못");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.369241, 127.365199));
        myLocationMarker.title("E3");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.366141, 127.363672));
        myLocationMarker.title("정문");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.364399, 127.358784));
        myLocationMarker.title("쪽문");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.368625, 127.356944));
        myLocationMarker.title("희망관");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mArrayList = getBikes(marker.getTitle());
        for(int i=0;i<mArrayList.size();i++) {
            System.out.println(mArrayList.get(i).getUser_Name());
        }

        mAdapter.notifyDataSetChanged();

        bikeInfoLayout.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        bikeInfoLayout.startAnimation(anim);
        map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
        //Toast.makeText(this, marker.getTitle() +" "+marker.getPosition(), Toast.LENGTH_SHORT).show();

        mAdapter.addItem(mArrayList);
        mAdapter.notifyDataSetChanged();

        return true;
    }

    public ArrayList<Bicycle> getBikes(String start) {
        ArrayList<Bicycle> bicycles = new ArrayList<Bicycle>();

        GetBikeAsyncTask task = new GetBikeAsyncTask();
        try {
            bicycles = task.execute(start).get();
            Toast.makeText(getApplicationContext(), "GET to MongoDB!!", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return bicycles;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }
}