//package com.karimapps.hipodriver.activities;
//
//import android.app.AlertDialog.Builder;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
//import android.content.Intent;
//import android.content.SharedPreferences.Editor;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
//import android.graphics.Bitmap.Config;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.net.ConnectivityManager;
//import android.net.Uri;
//import android.preference.PreferenceManager;
//import android.provider.Settings.Secure;
//import android.support.annotation.DrawableRes;
//import android.support.design.widget.Snackbar;
//import android.support.v4.content.ContextCompat;
//import android.view.View;
//import android.widget.Toast;
//import com.google.android.gms.maps.model.BitmapDescriptor;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.karimapps.hipodriver.C0496R;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.reflect.Method;
//
//public class Utils {
//    public static int DISTANCE_TO_ROTATE = 20;
//    public static String GET_PLAN_STATIONS;
//    public static String GET_STATION_PASSENGERS;
//    public static String LOGIN_API;
//    public static String server = " http://23.239.215.199:3456/";
//
//    /* renamed from: universal.Utils$1 */
//    static class C05561 implements OnClickListener {
//        C05561() {
//        }
//
//        public void onClick(DialogInterface dialog, int which) {
//        }
//    }
//
//    static {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(server);
//        stringBuilder.append("api/auth");
//        LOGIN_API = stringBuilder.toString();
//        stringBuilder = new StringBuilder();
//        stringBuilder.append(server);
//        stringBuilder.append("api/driver/getPlanStations");
//        GET_PLAN_STATIONS = stringBuilder.toString();
//        stringBuilder = new StringBuilder();
//        stringBuilder.append(server);
//        stringBuilder.append("api/driver/getStationPassengersDetails");
//        GET_STATION_PASSENGERS = stringBuilder.toString();
//    }
//
//    public static String getPreferences(String key, Context context) {
//        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
//    }
//
//    public static int getPreferencesInt(String key, Context context) {
//        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
//    }
//
//    public static boolean savePreferences(String key, String value, Context context) {
//        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putString(key, value);
//        editor.commit();
//        return true;
//    }
//
//    public static boolean savePreferencesInt(String key, int value, Context context) {
//        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putInt(key, value);
//        editor.commit();
//        return true;
//    }
//
//    public static byte[] getImageBytes(Bitmap bitmap) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(CompressFormat.PNG, 100, stream);
//        return stream.toByteArray();
//    }
//
//    public static Bitmap getImage(byte[] image) {
//        return BitmapFactory.decodeByteArray(image, 0, image.length);
//    }
//
//    public static byte[] getBytes(InputStream inputStream) throws IOException {
//        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        while (true) {
//            int read = inputStream.read(buffer);
//            int len = read;
//            if (read == -1) {
//                return byteBuffer.toByteArray();
//            }
//            byteBuffer.write(buffer, 0, len);
//        }
//    }
//
//    public static void CopyStream(InputStream is, OutputStream os) {
//        int buffer_size = 1024;
//        try {
//            byte[] bytes = new byte[1024];
//            while (true) {
//                int count = is.read(bytes, 0, 1024);
//                if (count != -1) {
//                    os.write(bytes, 0, count);
//                } else {
//                    return;
//                }
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    public static boolean WifiEnable(Context context) {
//        if (((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean isMobileDataEnable(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
//        try {
//            Method method = Class.forName(cm.getClass().getName()).getDeclaredMethod("getMobileDataEnabled", new Class[0]);
//            method.setAccessible(true);
//            return ((Boolean) method.invoke(cm, new Object[0])).booleanValue();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public static void showToast(Context context, String txt) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("");
//        stringBuilder.append(txt);
//        Toast.makeText(context, stringBuilder.toString(), 0).show();
//    }
//
//    public static void showToastTest(Context context, String txt) {
//    }
//
//    public static void showSnackbar(View view, String text) {
//        Snackbar.make(view, (CharSequence) text, -1).show();
//    }
//
//    public static String ucFirst(String str) {
//        String cap = new StringBuilder();
//        cap.append(str.substring(0, 1).toUpperCase());
//        cap.append(str.substring(1));
//        return cap.toString();
//    }
//
//    public static void showCustomDialog(Context context, String message) {
//        Builder builder = new Builder(context);
//        builder.setMessage(message);
//        builder.setPositiveButton("ok", new C05561());
//        builder.create().show();
//    }
//
//    public static String randomAlphaNumeric(int count) {
//        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//        StringBuilder builder = new StringBuilder();
//        while (true) {
//            int count2 = count - 1;
//            if (count == 0) {
//                return builder.toString();
//            }
//            double random = Math.random();
//            double length = (double) "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length();
//            Double.isNaN(length);
//            builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt((int) (random * length)));
//            count = count2;
//        }
//    }
//
//    public static boolean savePreferencesBool(String key, boolean value, Context context) {
//        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putBoolean(key, value);
//        editor.commit();
//        return true;
//    }
//
//    public static boolean getPreferencesBool(String key, Context context) {
//        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
//    }
//
//    public static void turnGPSOn(Context context) {
//        if (!Secure.getString(context.getContentResolver(), "location_providers_allowed").contains("gps")) {
//            Intent poke = new Intent();
//            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            poke.addCategory("android.intent.category.ALTERNATIVE");
//            poke.setData(Uri.parse("3"));
//            context.sendBroadcast(poke);
//        }
//    }
//
//    public static void turnGPSOff(Context context) {
//        if (Secure.getString(context.getContentResolver(), "location_providers_allowed").contains("gps")) {
//            Intent poke = new Intent();
//            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            poke.addCategory("android.intent.category.ALTERNATIVE");
//            poke.setData(Uri.parse("3"));
//            context.sendBroadcast(poke);
//        }
//    }
//
//    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
//        Drawable background = ContextCompat.getDrawable(context, C0496R.drawable.ic_map_pin);
//        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
//        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
//        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        background.draw(canvas);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }
//}
