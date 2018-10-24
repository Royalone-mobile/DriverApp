package com.sawatruck.driver.utils;

/**
 * Created by royal on 9/22/2017.
 */

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


/**
 * Create this Class from tutorial :
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial
 *
 * For Geocoder read this : http://stackoverflow.com/questions/472313/android-reverse-geocoding-getfromlocation
 *
 */

public class GPSTracker extends Service implements LocationListener {

    private static GPSTracker instance = null;
    // Get Class Name
    private static String TAG = GPSTracker.class.getName();

    private final Context mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    // boolean isGPSTrackingEnabled = false;


    Location location;
    double latitude;
    double longitude;

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;

    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a NearbyLocation Manager
    protected LocationManager locationManager;

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;

    private GPSTracker(Context context) {
        this.mContext = context;
        //  getLocation();
    }
    private List<Address> addresses;
    public static GPSTracker getInstance(Context context) {
        if (instance == null) {
            instance = new GPSTracker(context);
        }
        return instance;
    }
    public void setLatitude(double lat) {this.latitude = lat;}
    public void setLongitude(double lng){this.longitude = lng;}

    /**
     * Try to get my current location by GPS or Network Provider
     */
    public Location getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Try to get location if you GPS Service is enabled

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);


            provider_info = LocationManager.NETWORK_PROVIDER;

            // Context mContext = getApplicationContext();
            // if(mContext == null)
            //      DebuggingHelper.logInfo("Context is null");
            // Application can use GPS or Network Provider
            //if (!provider_info.isEmpty()) {
            if(isNetworkEnabled){
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    int res = -1;
                    // ActivityCompat.requestPermissions((Activity)mContext, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  } ,100);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                        int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
//                    return TODO;
                }
                locationManager.requestLocationUpdates(
                        provider_info,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider_info);
                } else if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            Location loc = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (loc != null) {
                                return loc;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return location;
    }

    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    public boolean getIsGPSTrackingEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions((Activity)mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        11); // Last parameter is requestCode
                return;
            }
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress() {
        if (location != null) {

            Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                addresses = geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);
                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            } catch (IllegalArgumentException illegalArgumentException) {
                String errorMessage = "Invalid Latitude or Longitude Used";
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latitude + ", Longitude = " +
                        longitude, illegalArgumentException);
            }
        }

        return null;
    }
    public void getGeocoderAddresses() {
        if (location != null) {

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                addresses = geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            } catch (IllegalArgumentException illegalArgumentException) {
                String errorMessage = "Invalid Latitude or Longitude Used";
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latitude + ", Longitude = " +
                        longitude, illegalArgumentException);
            }
        }
    }
    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine() {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);
            return addressLine;
        } else {
            return null;
        }
    }
    public String getAddressLine(List<Address> addresses) {

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            Logger.error(address.toString());
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }
    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality() {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();
            return locality;
        } else {
            return null;
        }
    }
    public String getLocality( List<Address> addresses) {

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();
            return locality;
        } else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode() {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }
    public String getPostalCode(List<Address> addresses) {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }
    public String getCityName()
    {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            String cityName = address.getLocality();
            return cityName;
        } else {
            return null;
        }
    }
    public String getCityName(List<Address> addresses)
    {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            String cityName = address.getLocality();

            return cityName;
        } else {
            return null;
        }
    }
    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName() {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }
    public String getCountryName( List<Address> addresses) {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }
    public String getStateName() {
        List<Address> addresses = getGeocoderAddress();
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            String stateName = address.getAdminArea();

            return stateName;
        } else {
            return null;
        }
    }
    @Override
    public void onLocationChanged(Location location) {
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Location getLastKnownLocation() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
    public String getAddressOne() {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressOne = address.getAddressLine(0);
            Logger.error(address.toString());
            return addressOne;
        }
        return null;
    }
    public String getAddressTwo() {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressTwo = address.getAddressLine(0);
            Logger.error( address.toString());
            return addressTwo;
        }
        return null;
    }
}


