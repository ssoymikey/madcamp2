package com.example.project2;

import android.content.Context;
import android.icu.text.Transliterator;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
    //String [] rent_place = {"아름관", "창의관", "카이마루"};


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

        myLocationMarker.position(new LatLng(36.374369, 127.365635));
        myLocationMarker.title("N1\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.373482, 127.362643));
        myLocationMarker.title("인사동\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.371174, 127.357922));
        myLocationMarker.title("노천극장\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.368467, 127.362627));
        myLocationMarker.title("오리연못\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.369241, 127.365199));
        myLocationMarker.title("E3\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.366141, 127.363672));
        myLocationMarker.title("정문\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.364399, 127.358784));
        myLocationMarker.title("쪽문\n");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
        map.addMarker(myLocationMarker);

        myLocationMarker.position(new LatLng(36.368625, 127.356944));
        myLocationMarker.title("희망관\n");
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