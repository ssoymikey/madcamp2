package com.example.project2;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.project2.Fragment.AddressFragment.makeJSONString;

public class kicycle_Activity extends AppCompatActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions myLocationMarker;


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

//    JSONObject rent_place =
//            {"place" :[
//                    {"name":"아름관", "location": [36.373986, 127.356660]},
//                    {"name":"카이마루", "location": [36.373802, 127.359253]},
//                    {"name":"창의관", "location": [36.370347, 127.362593]}
//                    ]
//            }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kicycle_main);

        Button button_register = findViewById(R.id.register);

        //Register button Listner
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.register_box, null, false);
                builder.setView(view);

                final Button register_submit = view.findViewById(R.id.register_submit);
                final EditText register_name = view.findViewById(R.id.register_name);
                final EditText register_phone = view.findViewById(R.id.register_phone);

                //register_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

                final AlertDialog dialog = builder.create();

                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
                register_submit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

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
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }
            );

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
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }
            );


        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
    }

    private void showRentPlaceMarker() {
        myLocationMarker = new MarkerOptions();
        myLocationMarker.position(new LatLng(36.373986, 127.356660));
        myLocationMarker.title("아름관\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.370347, 127.362593));
        myLocationMarker.title("창의관\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.373802, 127.359253));
        myLocationMarker.title("카이마루\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);


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
