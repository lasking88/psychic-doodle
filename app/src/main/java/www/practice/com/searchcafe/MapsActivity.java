package www.practice.com.searchcafe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final String EXTRA_LOCATION_PARAMS = "com.practice.www.searchcafe.extra_location_params";
    private static final String PRESERVED_LATITUDE = "preserved_latitude";
    private static final String PRESERVED_LONGITUDE = "preserved_longitude";
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private static final int CONSTANT_SI = 0;
    private static final int CONSTANT_GU = 1;
    private static final int CONSTANT_DONG = 2;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private List<Cafe> mCafes;

    private FirebaseFirestore mFirestore;

    private RecyclerView mRecyclerView;

    public static Intent newIntent(Context packageContext, CharSequence[] input) {
        Intent intent = new Intent(packageContext, MapsActivity.class);
        intent.putExtra(EXTRA_LOCATION_PARAMS, input);
        return intent;
    }

    private void setupLocationService() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mCurrentLocation = location;
                fetchCafes();
                mLocationManager.removeUpdates(mLocationListener);
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
        };
        if (ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, mLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllPermissioned = true;
        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED)
                isAllPermissioned = false;
        }
        if (isAllPermissioned)
            setupLocationService();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(PRESERVED_LATITUDE, mCurrentLocation.getLatitude());
        outState.putDouble(PRESERVED_LONGITUDE, mCurrentLocation.getLongitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        CharSequence[] input = getIntent().getCharSequenceArrayExtra(EXTRA_LOCATION_PARAMS);
        if (savedInstanceState == null) {
            showProgressDialog();
            if (input == null) {
                setupLocationService();
            } else {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                String locationName = input[CONSTANT_SI] + " " + input[CONSTANT_GU] + " " + input[CONSTANT_DONG];
                try {
                    List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                    if (addresses.size() != 0) {
                        mCurrentLocation = new Location("service provider");
                        mCurrentLocation.setLatitude(addresses.get(0).getLatitude());
                        mCurrentLocation.setLongitude(addresses.get(0).getLongitude());
                        fetchCafes();
                    } else {
                        Toast.makeText(this, "Failed to search the area", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            mCurrentLocation = new Location("service provider");
            mCurrentLocation.setLatitude(savedInstanceState.getDouble(PRESERVED_LATITUDE));
            mCurrentLocation.setLongitude(savedInstanceState.getDouble(PRESERVED_LONGITUDE));
            fetchCafes();
        }
        mRecyclerView = findViewById(R.id.recycler_view_cafes);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        hideProgressDialog();
        mMap = googleMap;
        LatLng location = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        if (mCafes != null) {
            for (Cafe c : mCafes) {
                LatLng cafeLocation = new LatLng(c.getLatitude(), c.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(cafeLocation)
                        .title(c.getCafeName())
                        .icon(BitmapDescriptorFactory.defaultMarker(c.getColor())));
            }
        }
    }

    private void updateUI() {
        mRecyclerView.setAdapter(new CafeAdapter(mCafes));
        mMapFragment.getMapAsync(MapsActivity.this);
    }

    private void fetchCafes() {
        mFirestore = FirebaseFirestore.getInstance();
        GeoPoint minimum = new GeoPoint(mCurrentLocation.getLatitude() - 0.02, mCurrentLocation.getLongitude() - 0.02);
        GeoPoint maximum = new GeoPoint(mCurrentLocation.getLatitude() + 0.02, mCurrentLocation.getLongitude() + 0.02);
        mFirestore.collection("cafes")
                .whereGreaterThan("latlng", minimum)
                .whereLessThan("latlng", maximum)
                .get()
                .addOnCompleteListener(l -> {
            if (l.isSuccessful()) {
                mCafes = new ArrayList<>();
                for (QueryDocumentSnapshot document : l.getResult()) {
                    String imgUrl = (String) document.get("imgurl");
                    String cafeName = (String) document.get("name");
                    String address = (String) document.get("address");
                    GeoPoint geoPoint = (GeoPoint) document.get("latlng");
                    int total = Integer.valueOf((String) document.get("totalSeats"));
                    int current = Integer.valueOf((String) document.get("currentSeats"));
                    assert geoPoint != null;
                    Cafe cafe = new Cafe(document.getId(), imgUrl, cafeName, address, geoPoint.getLatitude(), geoPoint.getLongitude(), total, current);
                    mCafes.add(cafe);
                }
                updateUI();
            } else {
                Log.w(TAG, "Firestore get collection is not successful");
            }
        });
    }

    private List<Cafe> createFakeData() {
        List<Cafe> mockData = new ArrayList<>();
//        mockData.add(new Cafe("", "스타벅스", "서울시 강남구 삼성2동 112-1", 37.498836,127.029638, 20f, 12f));
//        mockData.add(new Cafe("", "이디야커피", "서울시 강남구 삼성2동 112-2", 37.497385, 127.030131, 200f, 112f));
//        mockData.add(new Cafe("", "카페베네", "서울시 강남구 삼성2동 32", 37.499777,127.027504, 12314f, 12f));
//        mockData.add(new Cafe("", "커피 맛있게 타는 집 2호점 - 겨울연가 촬영지 (강남점)", "서울시 강남구 삼성2동 112", 37.496654, 127.034920, 22f, 22f));
        return mockData;
    }

    private class CafeHolder extends RecyclerView.ViewHolder {

        private Cafe mCafe;
        private ImageView mCafeImage;
        private TextView mCafeName;
        private TextView mCafeAddress;
        private TextView mSeats;

        public CafeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_cafe, parent, false));
            mCafeImage = itemView.findViewById(R.id.image_view_cafe);
            mCafeName = itemView.findViewById(R.id.text_view_cafe_name);
            mCafeAddress = itemView.findViewById(R.id.text_view_cafe_address);
            mSeats = itemView.findViewById(R.id.text_view_seats);
            itemView.setOnClickListener(l -> {
                Intent intent = CafeMenuActivity.newIntent(MapsActivity.this, mCafe.getId());
                startActivity(intent);
            });
        }

        public void bind(Cafe cafe) {
            mCafe = cafe;
            Glide.with(MapsActivity.this).load(mCafe.getImgUrl()).into(mCafeImage);
            mCafeName.setText(mCafe.getCafeName());
            mCafeAddress.setText(mCafe.getAddress());
            mSeats.setText(mCafe.getCurrent() + " / " + mCafe.getTotal());
            mSeats.setBackgroundColor(Color.HSVToColor(new float[]{mCafe.getColor(), 1f, .8f}));
        }
    }

    private class CafeAdapter extends RecyclerView.Adapter<CafeHolder> {

        private List<Cafe> mCafeList;

        public CafeAdapter(List<Cafe> cafeList) {
            mCafeList = cafeList;
        }

        @NonNull
        @Override
        public CafeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
            return new CafeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CafeHolder holder, int position) {
            holder.bind(mCafeList.get(position));
        }

        @Override
        public int getItemCount() {
            return mCafeList.size();
        }
    }
}
