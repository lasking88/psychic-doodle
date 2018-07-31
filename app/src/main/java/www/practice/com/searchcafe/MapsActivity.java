package www.practice.com.searchcafe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String EXTRA_LOCATION_PARAMS = "com.practice.www.searchcafe.extra_location_params";
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
    private List<Cafe> mCafes = createFakeData(); // must be substituted with real data.

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
                mMapFragment.getMapAsync(MapsActivity.this);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        showProgressDialog();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        CharSequence[] input = getIntent().getCharSequenceArrayExtra(EXTRA_LOCATION_PARAMS);
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
                    mMapFragment.getMapAsync(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_view_cafes);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CafeAdapter(mCafes));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        for (Cafe c : mCafes) {
            LatLng cafeLocation = new LatLng(c.getLatitude(), c.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(cafeLocation)
                    .title(c.getCafeName())
                    .icon(BitmapDescriptorFactory.defaultMarker(c.getColor())));
        }
    }

    private List<Cafe> createFakeData() {
        List<Cafe> mockData = new ArrayList<>();
        mockData.add(new Cafe("Starbucks", "Korea", 12, 102, 20f, 12f));
        mockData.add(new Cafe("Edia", "Korea", 45, 122, 200f, 112f));
        mockData.add(new Cafe("Cafebene", "Korea", 15, 22, 12314f, 12f));
        mockData.add(new Cafe("Groningen", "Korea", 22, 1, 22f, 22f));
        return mockData;
    }

    private class CafeHolder extends RecyclerView.ViewHolder {

        private Cafe mCafe;
        private TextView mCafeName;
        private TextView mCafeAddress;

        public CafeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_cafe, parent, false));
            mCafeName = itemView.findViewById(R.id.text_view_cafe_name);
            mCafeAddress = itemView.findViewById(R.id.text_view_cafe_address);
            itemView.setOnClickListener(l -> {
                Intent intent = new Intent(MapsActivity.this, CafeMenuActivity.class);
                startActivity(intent);
            });
        }

        public void bind(Cafe cafe) {
            mCafe = cafe;
            mCafeName.setText(mCafe.getCafeName());
            mCafeAddress.setText(mCafe.getAddress());
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
