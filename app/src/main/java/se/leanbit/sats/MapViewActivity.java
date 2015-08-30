package se.leanbit.sats;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import se.leanbit.sats.models.SatsSimpleCenter;
import se.leanbit.sats.repositories.services.SatsActivitiesService;

public class MapViewActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener, GoogleMap.OnCameraChangeListener
{
    private GoogleMap mMap = null;
    private Location mLastLocation;
    private Map<String, Marker> mSatsMarkers;
    private Map<String, SatsSimpleCenter> mAllSatsCenters;
    private LatLng mCameraPosition;
    private final static String CAMERA_LAT = "lat_coords";
    private final static String CAMERA_LANG = "lang_coords";
    private SatsActivitiesService mSatsActivitiesService;
    private Map<String, Marker> mMarkers;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        ImageView image = (ImageView) findViewById(R.id.action_bar_logo_settings);
        setupClickListner(image);
        image.setImageResource(R.drawable.back_icon);
        TextView textView = (TextView) findViewById(R.id.action_bar_text_training);
        textView.setText("HITTA CENTER");
        setSupportActionBar(toolbar);
        setupLocation();
        drawMap();
        inflater = getLayoutInflater();
        mSatsActivitiesService = new SatsActivitiesService();
        mAllSatsCenters = mSatsActivitiesService.getFullCenterMap();
    }

    private void setupClickListner(ImageView image)
    {
        image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Intent intent = new Intent(view.getContext(), MainActivity.class);
                //view.getContext().startActivity(intent);
                finish();
                //getActivity().onBackPressed();
            }
        });
    }

    private void drawMap()
    {
        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_view, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map)
    {

        mMap = map;
        LatLng mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCameraPosition, 15));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
        }
        addMarkers();
        setInfoClickListener();
        //Log.d("     ", "onMapReady fired ..............");
    }
    private void setupLocation() {
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        mLastLocation = locationManager.getLastKnownLocation(bestProvider);
        if (mLastLocation != null) {
            onLocationChanged(mLastLocation);
        }else
        {
            mLastLocation = new Location(bestProvider);
            mLastLocation.setLatitude(59.293573D);
            mLastLocation.setLongitude(18.083550D);
            mCameraPosition = new LatLng(59.293573D,18.083550D);
        }
        locationManager.requestLocationUpdates(bestProvider, 60000, 0, (android.location.LocationListener) this);
        //Log.d(mLastLocation + " location", "setupLocation fired ..............");
    }
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mCameraPosition = new LatLng(latitude, longitude);
        //Log.d(mCameraPosition.latitude +" lat " + mCameraPosition.longitude, "onLocationChanged fired ..............");

    }
    @Override
    public void onMapClick(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        //Log.d("onMapClick", "onMapClick fired ..............");
    }

    private void addMarkers() {
      mMarkers = new HashMap<>();
        for (Map.Entry<String, SatsSimpleCenter> entry : mAllSatsCenters.entrySet()) {
            if(entry.getValue().lat < mCameraPosition.latitude +0.1 & entry.getValue().Long < mCameraPosition.longitude +0.1 )

            mMarkers.put(entry.getValue().centerName, mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(entry.getValue().lat, entry.getValue().Long))
                    .title("SATS " + entry.getValue().centerName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sats_pin_normal))));

        }

        //Log.d("onAddMarkers", " allMarkerssize" + mAllSatsCenters.size() + " lat" + mMarkers.size() + " long " + mCameraPosition.longitude);

    }

    private void setInfoClickListener()
    {
        setCustomWindow();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {

            @Override
            public void onInfoWindowClick(Marker marker)
            {
                String centerName = marker.getTitle();
                String url = "";
                centerName = centerName.substring(5);
                for (Map.Entry<String, SatsSimpleCenter> entry : mAllSatsCenters.entrySet())
                {
                    if (centerName.equals(entry.getValue().centerName))
                    {
                        url = entry.getValue().webUrl;
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putString("urlString", url);
                Intent intent = new Intent(MapViewActivity.this, SatsWebActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //Log.d("setInfoClickListener " + url + " " + centerName, " onInfoClick fired ..............");
            }
        });
    }

    private void setCustomWindow()
    {

        GoogleMap.InfoWindowAdapter customWindowAdapter = new GoogleMap.InfoWindowAdapter()
        {

            @Override
            public View getInfoWindow(Marker marker)
            {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                View popup=inflater.inflate(R.layout.custom_info_window, null);

                TextView tv=(TextView) popup.findViewById(R.id.custom_info_window_text);
                tv.setText(marker.getTitle());
                return(popup);
            }
        };
        mMap.setInfoWindowAdapter(customWindowAdapter);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mCameraPosition = mMap.getCameraPosition().target;
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat(CAMERA_LAT, (float) mCameraPosition.latitude);
        outState.putFloat(CAMERA_LANG, (float) mCameraPosition.longitude);
        Log.d(Float.toString((float) mCameraPosition.latitude), "onSavedInstanceState fired ..............................");
        super.onSaveInstanceState(outState);

    }

}
