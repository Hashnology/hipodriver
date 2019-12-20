package universal;
import android.app.Application;
import java.util.Locale;

public class LangApplication extends Application {
    @Override
    public void onCreate()
    {
        setLocale();
        super.onCreate();
    }
    private void setLocale(){
        Locale jaLocale = new Locale("xml");
        AppUtils.setLocale(jaLocale);
        AppUtils.setConfigChange(this);
    }
}
