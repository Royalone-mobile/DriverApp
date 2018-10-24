package com.sawatruck.driver.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.android.gms.maps.model.LatLng;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.AddressDetail;
import com.sawatruck.driver.entities.NotificationModel;
import com.sawatruck.driver.entities.NotificationModels;
import com.sawatruck.driver.entities.SawaTruckLocation;
import com.sawatruck.driver.view.activity.ActivityNotification;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by royal on 8/19/2017.
 */

public class Misc {

    public static int getColorFromResource(int resId) {
        return ContextCompat.getColor(BaseApplication.getContext(), resId);
    }

    public static void applyLocale(Context context ){
//        Resources res = context.getResources();
//        // Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        String strLangCode = AppSettings.with(context).getLangCode();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            conf.setLocale(new Locale(strLangCode.toLowerCase())); // API 17+ only.
//            conf.setLayoutDirection(new Locale(strLangCode.toLowerCase()));
//        }
//        res.updateConfiguration(conf, dm);
        String strLangCode = AppSettings.with(context).getLangCode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, strLangCode.toLowerCase());
        }

        updateResourcesLegacy(context, strLangCode.toLowerCase());
    }

    public static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    public static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }


    public static void showResponseMessage(byte[] responseBody){
        try {
            String paramString = new String(responseBody);
            paramString = StringUtil.escapeString(paramString);
            JSONObject jsonObject = new JSONObject(paramString);
            Logger.error(jsonObject.toString());
            CLog.log(paramString);

            if(jsonObject.has("ModelState")) {
                if (jsonObject.getJSONObject("ModelState").has("Errors")) {
                    String str = jsonObject.getJSONObject("ModelState").getString("Errors");
                    if(str.length()>0)
                        str = str.substring(2,str.length() - 2);
                    Notice.show(str);
                }
            }
            else
                Notice.show(jsonObject.getString("Message"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showResponseMessage(String responseBody){
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            Logger.error(jsonObject.toString());
            if(jsonObject.has("ModelState")) {
                if (jsonObject.getJSONObject("ModelState").has("Errors")) {
                    String str = jsonObject.getJSONObject("ModelState").getString("Errors");
                    if(str.length()>0)
                        str = str.substring(2,str.length() -2);
                    Notice.show(str);
                }
            }
            else
                Notice.show(jsonObject.getString("Message"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<NotificationModels> getSortedNotifications(ArrayList<NotificationModel> notificationModels){
        ArrayList<NotificationModels> retList = new ArrayList<>();
        HashMap<Date,Integer> hashMap = new HashMap<Date,Integer>();
        int counter = 0;
        for(NotificationModel notificationModel:notificationModels){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date =  format.parse(notificationModel.getNotification().getDate());
                if(hashMap.get(date) == null){
                    hashMap.put(date,counter);
                    NotificationModels notifications = new NotificationModels();

                    notifications.add(notificationModel);
                    retList.add(notifications);
                    counter++;
                }
                else {
                    int index =hashMap.get(date);
                    NotificationModels notifications = retList.get(index);
                    notifications.add(notificationModel);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return retList;
    }

    public static String getSpanFromDate(Context context,Date date){
        try {
            long span = (getUTCdatetimeAsDate().getTime() - date.getTime() + 5) / 1000;
            String times[] = new String[]{"60", "3600", "86400", "604800", "2419200", "29030400"};
            if ((span > Long.valueOf(times[5])))
                return context.getResources().getQuantityString(R.plurals.years, (int) (span / Long.valueOf(times[4])), (int) (span / Long.valueOf(times[5]))) + " ago";
            else if ((span > Long.valueOf(times[4])))
                return context.getResources().getQuantityString(R.plurals.months, (int) (span / Long.valueOf(times[3])), (int) (span / Long.valueOf(times[4]))) + " ago";
            else if ((span > Long.valueOf(times[3])))
                return context.getResources().getQuantityString(R.plurals.weeks, (int) (span / Long.valueOf(times[3])), (int) (span / Long.valueOf(times[3]))) + " ago";
            else if ((span > Long.valueOf(times[2])))
                return context.getResources().getQuantityString(R.plurals.days, (int) (span / Long.valueOf(times[2])), (int) (span / Long.valueOf(times[2]))) + " ago";
            else if ((span > Long.valueOf(times[1])))
                return context.getResources().getQuantityString(R.plurals.hours, (int) (span / Long.valueOf(times[1])), (int) (span / Long.valueOf(times[1]))) + " ago";
            else if ((span > Long.valueOf(times[0])))
                return context.getResources().getQuantityString(R.plurals.minutes, (int) (span / Long.valueOf(times[0])), (int) (span / Long.valueOf(times[0]))) + " ago";

            else if (span <= 0)
                return "From Now";
            else
                return context.getResources().getQuantityString(R.plurals.seconds, (int) span, (int) span) + " ago";
        }catch(Exception e) {
            return "";
        }
    }

    public static String getNextSpanFromDate(Context context,Date date){
        try {
            long span = (getUTCdatetimeAsDate().getTime() - date.getTime() + 5) / 1000;
            String times[] = new String[]{"60", "3600", "86400", "604800", "2419200", "29030400"};
            if ((span > Long.valueOf(times[5])))
                return context.getResources().getQuantityString(R.plurals.years, (int) (span / Long.valueOf(times[4])), (int) (span / Long.valueOf(times[5])));
            else if ((span > Long.valueOf(times[4])))
                return context.getResources().getQuantityString(R.plurals.months, (int) (span / Long.valueOf(times[3])), (int) (span / Long.valueOf(times[4])));
            else if ((span > Long.valueOf(times[3])))
                return context.getResources().getQuantityString(R.plurals.weeks, (int) (span / Long.valueOf(times[3])), (int) (span / Long.valueOf(times[3])));
            else if ((span > Long.valueOf(times[2])))
                return context.getResources().getQuantityString(R.plurals.days, (int) (span / Long.valueOf(times[2])), (int) (span / Long.valueOf(times[2])));
            else if ((span > Long.valueOf(times[1])))
                return context.getResources().getQuantityString(R.plurals.hours, (int) (span / Long.valueOf(times[1])), (int) (span / Long.valueOf(times[1])));
            else if ((span > Long.valueOf(times[0])))
                return context.getResources().getQuantityString(R.plurals.minutes, (int) (span / Long.valueOf(times[0])), (int) (span / Long.valueOf(times[0])));

            else if (span <= 0)
                return "Now";
            else
                return context.getResources().getQuantityString(R.plurals.seconds, (int) span, (int) span);
        }catch(Exception e) {
            return "";
        }
    }

    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String DATEFORMAT_1 = "yyyy-MM-dd HH:mm:ss aa";
    public static Date getUTCdatetimeAsDate()
    {
        //note: doesn't check for null
        return StringDateToDate(getUTCdatetimeAsString());
    }

    public static String getUTCdatetimeAsString()
    {
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static String getUTCDatetimeAsStringWithPM()
    {
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT_1, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT, locale);

        try
        {
            dateToReturn = dateFormat.parse(StrDate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    public static String getNotificationSpanFromDate(Context context,Date date){
		try
		{
		long span = (getUTCdatetimeAsDate().getTime() - date.getTime()+5)/1000;
        String times[] = new String[] { "60","3600","86400","604800","2419200","29030400"};
        if((span>Long.valueOf(times[5])))
            return context.getResources().getQuantityString(R.plurals.years, (int)(span/Long.valueOf(times[5])),(int)(span/Long.valueOf(times[5]))) + " ago";
        else if ((span>Long.valueOf(times[4])))
            return context.getResources().getQuantityString(R.plurals.months, (int)(span/Long.valueOf(times[4])),(int)(span/Long.valueOf(times[4]))) + " ago";
        else if ((span>Long.valueOf(times[3])))
            return context.getResources().getQuantityString(R.plurals.weeks, (int)(span/Long.valueOf(times[3])),(int)(span/Long.valueOf(times[3]))) + " ago";
        else if ((span>Long.valueOf(times[2]))) {
            if(span / Long.valueOf(times[2]) == 1)
                return context.getString(R.string.yesturday);
            else
                return context.getResources().getQuantityString(R.plurals.yesturday_days, (int) (span / Long.valueOf(times[2])), (int) (span / Long.valueOf(times[2])));
        }
        else return context.getString(R.string.today);
		}
		catch (Exception e)
		{
			return "";
		}
     
    }

    public static Date getDateFromString(String strDate) {
        Date d = null;
        try {
            Locale locale = new Locale(Locale.ENGLISH.getLanguage());
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", locale);
            d = f.parse(strDate);
            DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return d;
    }

    public static String getDateStringFromDate(String strDate) {
        Date d = null;
        String string = "";
        try {
            Locale locale = new Locale(Locale.ENGLISH.getLanguage());
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", locale);

            DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
            
            d = f.parse(strDate);

            string = date.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return string;
    }

    public static String getTimeStringFromDate(String strDate) {
        String datetime = "";
        Date d = null;
        try {
            Locale locale = new Locale(Locale.ENGLISH.getLanguage());
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", locale);
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            d = f.parse(strDate);
            DateFormat time = new SimpleDateFormat("hh:mm a");
            Calendar calendar = Calendar.getInstance();
            Logger.error(calendar.getTimeZone().toString());
            time.setTimeZone(calendar.getTimeZone());
            datetime = time.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datetime;
    }

    public static String getTimeZoneDate(String strDate) {
        String datetime = "";
        Date d = null;
        try {
            Locale locale = new Locale(Locale.ENGLISH.getLanguage());
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd",locale);

            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            d = f.parse(strDate);
            DateFormat time = new SimpleDateFormat("yyyy-MM-dd",locale);

            Calendar calendar = Calendar.getInstance();
            Logger.error(calendar.getTimeZone().toString());
            time.setTimeZone(calendar.getTimeZone());
            datetime = time.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datetime;
    }

    public static String convert2UTC(Date date) {
        String datetime = "";
        try {
            Locale locale = new Locale(Locale.ENGLISH.getLanguage());
            DateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mmaa", locale);

            time.setTimeZone(TimeZone.getTimeZone("UTC"));

            datetime = time.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datetime;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI(Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(BaseApplication.getInstance().getApplicationContext(), uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(BaseApplication.getInstance().getApplicationContext(), contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(BaseApplication.getInstance().getApplicationContext(), contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(BaseApplication.getInstance().getApplicationContext(), uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

  public static String getCountryDialCode(){
        String countryCode = "";
        String regionCode = AppSettings.with(BaseApplication.getContext()).getRegionCode();
        String[] arrContryCode= BaseApplication.getContext().getResources().getStringArray(R.array.DialingCountryCode);
        for(int i=0; i<arrContryCode.length; i++){
            String[] arrDial = arrContryCode[i].split(",");
            if(arrDial[1].trim().equals(regionCode.trim())){
                countryCode = arrDial[0];
                break;
            }
        }
        return countryCode;
    }


    public static int getZoomLevel(double radius){
        double scale = radius / 500;
        return ((int) (16 - Math.log(scale) / Math.log(2)));
    }

    public static void sendNotification(String title, String body) {
        PendingIntent contentIntent = PendingIntent.getActivity(BaseApplication.getContext(), 0,
                new Intent(BaseApplication.getContext(), ActivityNotification.class), 0);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(BaseApplication.getContext()).setSmallIcon(R.drawable.ico_setting)
                        .setContentTitle(title).setContentText(body).setContentIntent(contentIntent);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        NotificationManager mNotificationManager =
                (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2000, mBuilder.build());

    }


    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public static AddressDetail parseAddressXML(InputStream stream){
        AddressDetail addressDetail = new AddressDetail();
        try {
//            InputStream is = getAssets().open("file1.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);

            Element domElement=doc.getDocumentElement();
            domElement.normalize();

            NodeList nList = doc.getElementsByTagName("result");

            if(nList.getLength()<=0) return addressDetail;

            Node node = nList.item(0);


            Element resultElement = (Element) node;
            addressDetail.setFormatted_address(getValue("formatted_address", resultElement));

            NodeList addressNode = resultElement.getElementsByTagName("address_component");
            NodeList geometryNode = resultElement.getElementsByTagName("geometry");
            Element locationElement = (Element)((Element)geometryNode.item(0)).getElementsByTagName("location").item(0);

            addressDetail.setLatitude(Double.valueOf(getValue("lat", locationElement)));
            addressDetail.setLongitude(Double.valueOf(getValue("lng", locationElement)));

            for (int i=0; i<addressNode.getLength(); i++) {
                Element addressElement = (Element)addressNode.item(i);
                NodeList typeList = addressElement.getElementsByTagName("type");
                for(int j=0; j<typeList.getLength(); j++){
                    Node typeNode = typeList.item(j);
                    switch(typeNode.getChildNodes().item(0).getNodeValue()){
                        case "route":
                            addressDetail.setRoute(getValue("long_name",addressElement));
                            break;
                        case "country":
                            addressDetail.setCountry(getValue("long_name",addressElement));
                            break;
                        case "postal_town":

                            addressDetail.setCity(getValue("long_name",addressElement));
                            break;
                        case "administrative_area_level_2":
                            addressDetail.setAdministrativeAreaLevel2(getValue("long_name",addressElement));
                            break;
                        case "administrative_area_level_1":
                            addressDetail.setCity(getValue("long_name",addressElement));
                            addressDetail.setAdministrativeAreaLevel1(getValue("long_name",addressElement));
                            break;
                        case "postal_code":
                            addressDetail.setPostalCodePrefix(getValue("long_name",addressElement));
                            break;

                        case "street_number":
                            addressDetail.setStreetNumber(getValue("long_name",addressElement));
                            break;
                        case "locality":
                            addressDetail.setCity(getValue("long_name",addressElement));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressDetail;
    }

    public static AddressDetail getAddressDetail(Double latitude, Double longitude) {
        AddressDetail addressDetail = new AddressDetail();
        SawaTruckLocation location = new SawaTruckLocation();
        location.setLatitude(latitude.toString());
        location.setLongitude(longitude.toString());
        try {
            addressDetail = new ReverseGeoCodeTask1(Constant.GEOCODE_API_KEY).execute(location).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressDetail;
    }

    public static AddressDetail getAddressDetail(SawaTruckLocation location) {
        String GEOCODE_API_ENDPOINT_BASE = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=";
        AddressDetail addressDetail = new AddressDetail();
        String googleGeocodeEndpoint = GEOCODE_API_ENDPOINT_BASE + location.getLatitude() + "," + location.getLongitude() + "&key=" + Constant.GEOCODE_API_KEY + "&language=en";
        try {
            URL url = new URL(googleGeocodeEndpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            addressDetail = Misc.parseAddressXML(in);
            String response = BaseApplication.getGson().toJson(addressDetail, AddressDetail.class);
            Logger.error(response);
            return addressDetail;

        }catch ( Exception e){

            e.printStackTrace();
        }
        return addressDetail;
    }

    private static class ReverseGeoCodeTask1 extends AsyncTask<SawaTruckLocation, Void, AddressDetail> {

        private final static String GEOCODE_API_ENDPOINT_BASE = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=";
        private String apiKey;

        public ReverseGeoCodeTask1(final String apiKey){
            this.apiKey = apiKey;
        }

        @Override
        protected AddressDetail doInBackground(SawaTruckLocation... params) {
            AddressDetail addressDetail = new AddressDetail();
            if(apiKey == null){
                throw new IllegalStateException("Pass in a geocode api key in the ReverseGeoCoder constructor");
            }

            SawaTruckLocation location = params[0];
            String googleGeocodeEndpoint = GEOCODE_API_ENDPOINT_BASE + location.getLatitude() + "," + location.getLongitude() + "&key=" + apiKey + "&language=en";
            try {
                URL url = new URL(googleGeocodeEndpoint);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                addressDetail = Misc.parseAddressXML(in);
                String response = BaseApplication.getGson().toJson(addressDetail, AddressDetail.class);
                return addressDetail;

            }catch ( Exception e){

                e.printStackTrace();

            }
            return addressDetail;
        }

        @Override
        protected void onPostExecute(AddressDetail address) {
            super.onPostExecute(address);
        }
    }

    public static float dpToPx(float dp) {
        Resources r = BaseApplication.getInstance().getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());

        return px;
    }

    public static Bitmap getMarkerBitmap(int resId) {
        int height = 90;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)BaseApplication.getContext().getResources().getDrawable(resId);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }


    //Method for finding bearing between two points
    public static float getBearing(LatLng begin, LatLng end) {
        double PI = 3.14159;
        double lat1 = begin.latitude * PI / 180;
        double long1 = begin.longitude * PI / 180;
        double lat2 = end.latitude * PI / 180;
        double long2 = end.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return (float) brng;
    }


    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException ex) {}
        return "";
    }
}
