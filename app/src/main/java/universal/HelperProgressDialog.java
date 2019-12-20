package universal;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Linez-001 on 12/18/2017.
 */

public class HelperProgressDialog {
    private static ProgressDialog progress_dialog;

    public static void showDialog(Context context, String title, String message){
        progress_dialog = ProgressDialog.show(context, title, message, true);
    }

    public static void closeDialog(){
        if(progress_dialog != null){
            progress_dialog.dismiss();
        }
    }
}

