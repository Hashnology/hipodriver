package universal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.karimapps.hipodriver.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class Utils {

    public static String server = " http://23.239.215.199:3456/";

    public static String LOGIN_API = server + "api/auth";
    public static String GET_PLAN_STATIONS = server + "api/driver/getPlanStations";
    public static String GET_STATION_PASSENGERS = server + "api/driver/getStationPassengersDetails";
    public static String DRIVER_TIME_LOCATION = server + "api/driver/insertDriverTracking";


    public static int DISTANCE_TO_ROTATE = 20;

//    public static ArrayList<MinTime> GetMinTime = new ArrayList<MinTime>();
//    public static ArrayList<GetVehicles> GetVehicles = new ArrayList<GetVehicles>();
//    public static ArrayList<LoadCountries> LoadCountriesList = new ArrayList<LoadCountries>();
//    public static ArrayList<GetDrivers> DriversArroundList = new ArrayList<GetDrivers>();
//    public static ArrayList<UpcomingBookings> UpcomingBookingsList = new ArrayList<UpcomingBookings>();
//    public static ArrayList<InventoryImages> getInventoryImagesList = new ArrayList<InventoryImages>();
//    public static ArrayList<AllOffers> All_Offers_List = new ArrayList<AllOffers>();

    public static String getPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString(key, "");
        return userName;
    }

    public static int getPreferencesInt(String key, Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int int_value = sharedPreferences.getInt(key, 0);
        return int_value;
    }

    public static boolean savePreferences(String key, String value,
                                          Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        return true;
    }

    public static boolean savePreferencesInt(String key, int value,
                                             Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
        return true;
    }


    public static byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

//	private void postJSonbyCreatingJsonObject() {
//		try {
//			RequestQueue requestQueue = Volley.newRequestQueue();
//
//			String URL = CONSTANTS.baseUrl + "register_driver2.php";
//			JSONObject postParam = new JSONObject();
//
//			postParam.put("device", "android");
//			postParam.put("vehicle_license_number", "111111");
//			postParam.put("vehicle_registration_number", "433");
//			postParam.put("vehicle_maker_model", "4343");
//			postParam.put("vehicle_year", "443");
//			postParam.put("vehicle_type", "dfdsfsd");
//			postParam.put("vehicle_air_conditioner", "sdfsdf");
//			postParam.put("voice_booking", "dfdsfs");
//			postParam.put("payment_card", "dfdsf");
//
//			final String requestBody = postParam.toString();
//
//
//
//
//
//			StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//				@Override
//				public void onResponse(String response) {
//					Log.i("VOLLEY", response);
//					Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
//				}
//			}, new Response.ErrorListener() {
//				@Override
//				public void onErrorResponse(VolleyError error) {
//					Log.e("VOLLEY", error.toString());
//					Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
//				}
//			}) {
//
//
//				@Override
//				public Map<String, String> getHeaders ()throws AuthFailureError {
//					HashMap<String, String> headers = new HashMap<String, String>();
//					headers.put("Content-Type", "application/json");
//					headers.put("registration_id", "cBTXx4-Q3n0:APA91bHs81tH5Lz6IdmI8_vE7c43nRObz20lTtWaWL0mAtOf9qnIUvafjd2qMQvpvCPxSQifhv1AK-EXD_a9S2ypaz5XkQbka2YjkQRcDY21kXvPGFpz6M1scGnD4d2_gYiXw3vXATmq");
//					headers.put("driver_id", "1");
//					return headers;
//				}
//
//				@Override
//				public byte[] getBody() throws AuthFailureError {
//					try {
//						return requestBody == null ? null : requestBody.getBytes("utf-8");
//					} catch (UnsupportedEncodingException uee) {
//						VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//						return null;
//					}
//				}};
//
//
//
//
//
//			requestQueue.add(stringRequest);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

    public static boolean WifiEnable(Context context) {
        boolean mWifiEnable = false; // Assume disabled
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            mWifiEnable = true;
            return true;
        }
        return false;
    }

    public static boolean isMobileDataEnable(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API and do whatever error
            // handling you want here
        }
        return mobileDataEnabled;
    }

    public static void showToast(Context context, String txt) {
        Toast.makeText(context, "" + txt, Toast.LENGTH_SHORT).show();

    }

    public static void showToastTest(Context context, String txt) {

        //Toast.makeText(context, "" + txt, Toast.LENGTH_SHORT).show();

    }

    /*show a snackbar*/
    public static void showSnackbar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public static String ucFirst(String str) {
        String cap = str.substring(0, 1).toUpperCase()
                + str.substring(1);
        return cap;
    }

    public static void showCustomDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static String randomAlphaNumeric(int count) {
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static boolean savePreferencesBool(String key, boolean value,
                                              Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
        return true;
    }

    public static boolean getPreferencesBool(String key, Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean value = sharedPreferences.getBoolean(key, false);
        return value;
    }

    /*turn gps on*/
    public static void turnGPSOn(Context context) {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    /*turn gps off*/
    public static void turnGPSOff(Context context) {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_map_pin);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
