package com.sawatruck.driver.controller;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;


/**
 * This service runs in the background and broadcast the result to any registered receivers
 */
public class LocationUpdateService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private static final String TAG = LocationUpdateService.class.getSimpleName();
	LocationRequest mLocationRequest;
	private static final String ACTION_LOCATION_UPDATED = "location_updated";
	private static final String ACTION_REQUEST_LOCATION = "request_location";

	private static GoogleApiClient _googleApiClient;

	private static double _destLat = 10000, _destLng = 10000;

	private static String travelID = "";
	public LocationUpdateService() {
		super(TAG);
	}

    public static boolean isActive = false;
	private static int serviceType = 0;
	public static String getTravelID() {
		return travelID;
	}

	public static void setTravelID(String travelID) {
		LocationUpdateService.travelID = travelID;
	}

	public static int getServiceType() {
		return serviceType;
	}

	public static void setServiceType(int serviceType) {
		LocationUpdateService.serviceType = serviceType;
	}

	public static double get_destLat() {
		return _destLat;
	}

	public static void set_destLat(double _destLat) {
		LocationUpdateService._destLat = _destLat;
	}

	public static double get_destLng() {
		return _destLng;
	}

	public static void set_destLng(double _destLng) {
		LocationUpdateService._destLng = _destLng;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent != null ? intent.getAction() : null ;

		if(ACTION_LOCATION_UPDATED.equals(action)){
			if(isActive)
				onLocationUpdated(intent);
		}
		else
			onRequestLocation();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(600);
		mLocationRequest.setFastestInterval(300);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	protected  void buildGoogleApiClient() {
		if(_googleApiClient ==null) {
			Logger.error("buildGoogleApiClient");

			_googleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
		}
	}

	/**
	 * Called when a location update is requested. We block until we get a result back.
	 * We are using Fused NearbyLocation Api.
	 */

	private void onRequestLocation() {
		buildGoogleApiClient();

		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = false;
		boolean network_enabled = false;

		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch(Exception ex) {}

		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch(Exception ex) {}

		if(!gps_enabled && !network_enabled) {
			Notice.showOnMainThread(getString(R.string.error_location_service));

            Logger.error(getString(R.string.error_location_service));
			return;
		}
		// we block here
		ConnectionResult connectionResult = _googleApiClient.blockingConnect(2, TimeUnit.SECONDS);

		if (connectionResult.isSuccess() && _googleApiClient.isConnected()) {
			Intent locationUpdatedIntent = new Intent(this, LocationUpdateService.class);
			locationUpdatedIntent.setAction(ACTION_LOCATION_UPDATED);

			// Send last known location out first if available
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}


			createLocationRequest();

			if(isActive)
				FusedLocationApi.requestLocationUpdates(
					_googleApiClient, mLocationRequest,
					PendingIntent.getService(this, Constant.TRACKING_REQUEST_CODE, locationUpdatedIntent, 0));
			else  {

				FusedLocationApi.removeLocationUpdates(_googleApiClient, PendingIntent.getService(this, Constant.TRACKING_REQUEST_CODE, locationUpdatedIntent, 0));
			}

			_googleApiClient.disconnect();
		} else if(!connectionResult.isSuccess()){
				Notice.showOnMainThread(getString(R.string.error_location_service));
		}
	}

	/**
	 * Called when the location has been updated & broadcast the new location
	 */

	private void onLocationUpdated(Intent intent) {
		// Extra new location
		Location location =
				intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

		if (location != null) {
			LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Intent intent1 = new Intent("onLocationUpdate");

//			Logger.error("_prevLat=" + _prevLat);
//			Logger.error("_prevLng=" + _prevLng);
			Logger.error("latitude=" + currentLatLng.latitude);
			Logger.error("longitude=" + currentLatLng.longitude);
//			Logger.error("bearing=" + location.getBearing());
//			Logger.error("provider=" + location.getProvider());
//			Logger.error("accuracy=" + location.getAccuracy());
//			Logger.error("altitude=" + location.getAltitude());
//			Logger.error("elapsed=" + location.getElapsedRealtimeNanos());
//			Logger.error("speed=" + location.getSpeed());
//			Logger.error("time=" + location.getTime());

			LatLng prevLatLng = new LatLng(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());

			float degree =  Misc.getBearing(prevLatLng, currentLatLng);

			if(degree>30.0f) {
				Logger.error("Truck is Turnning");
				sendLocationToServer(currentLatLng);
			}

			AppSettings.with(this).setCurrentLat(currentLatLng.latitude);
			AppSettings.with(this).setCurrentLng(currentLatLng.longitude);

            //TODO Broadcast current NearbyLocation;
            sendBroadcast(intent1);

//            Location destLocation  = new Location(LocationManager.GPS_PROVIDER);
//            destLocation.setLatitude(_destLat);
//            destLocation.setLongitude(_destLng);
//
//			float distance = location.distanceTo(destLocation);
//
//			if ((distance) < 200000) {
//				Misc.sendNotification(getResources().getString(R.string.app_name), "You are reached your destination!!");
//                Logger.error("distance. b/w < 1km , distance = " + distance);
//                stopLocationUpdateService();
//			}

		}
	}


	public void sendLocationToServer(LatLng currentLatLng){
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("TravelID",LocationUpdateService.getTravelID());

			if(LocationUpdateService.getServiceType() == 1){
				jsonObject.put("Status","OnThePickUpWay");
			}else {
				jsonObject.put("Status","OnTheWay");
			}
//			Logger.error("sendLocationToServer");
//			Logger.error(currentLatLng.latitude + "," + currentLatLng.longitude);




			jsonObject.put("Lat",currentLatLng.latitude);
			jsonObject.put("long",currentLatLng.longitude);

			AddNewPointAsyncTask locationAsyncTask = new AddNewPointAsyncTask();
			locationAsyncTask.execute( jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class AddNewPointAsyncTask extends AsyncTask<String, Void, Void> {
		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		protected Void doInBackground(String... params) {
			String json = params[0];

			HttpUtil.postBody(Constant.ADD_NEW_POINT_API, json, new HttpUtil.ResponseHandler() {
				@Override
				public void onSuccess(final String responseBody) {
				}

				@Override
				public void onFailure(final String errorString) {
					Logger.error(errorString);
				}

			});
			return null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Logger.error("onConnected");
	}



	@Override
	public void onConnectionSuspended(int i) {
		_googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Logger.error("onConnectionFailed");
	}
}