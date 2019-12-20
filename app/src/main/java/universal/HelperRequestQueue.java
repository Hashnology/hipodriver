package universal;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Linez-001 on 12/21/2017.
 */

public class HelperRequestQueue {
    private static Context context;
    private static HelperRequestQueue request_instance;
    private RequestQueue requestQueue;

    private HelperRequestQueue(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        return requestQueue;
    }

    public static synchronized HelperRequestQueue getRequestInstance(Context context){
        if(request_instance == null){
            request_instance = new HelperRequestQueue(context);
        }

        return request_instance;
    }

    public<T> void addRequest(Request<T> request){
        requestQueue.add(request);
    }
}