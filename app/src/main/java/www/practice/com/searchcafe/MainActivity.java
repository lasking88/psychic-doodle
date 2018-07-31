package www.practice.com.searchcafe;

import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_automatic_search).setOnClickListener(v -> {
            LocationManager locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
            Intent intent;
            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
            } else {
                intent = new Intent(this, MapsActivity.class);
            }
            startActivity(intent);
        });

        findViewById(R.id.button_customized_search).setOnClickListener(l -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
    }
}
