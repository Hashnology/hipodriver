package universal;

/**
 * Created by Linez-001 on 12/13/2017.
 */

public class UniversalConstants
{
    public static String BASE_URL = "http://alex.karimapps.com/";
    public static String IMAGES_URL = "http://alex.karimapps.com/uploads/";

    /*public static String BASE_URL = "http://192.168.100.4/alex.karimapps.com/";
    public static String IMAGES_URL = "http://192.168.100.4/alex.karimapps.com/uploads/";*/

    public static final String STATION_NUMBER =  "STATION_NUMBER";

    public static final String USERNAME = "USERNAME";
    public static final String DRIVER_FIRSTNAME = "DRIVER_FIRSTNAME";
    public static final String DRIVER_LASTNAME = "DRIVER_LASTNAME";
    public static final String DRIVER_ID = "DRIVER_ID";
    public static final String DRIVER_PHONE = "DRIVER_PHOENE";
    public static final String DRIVER_CAR_REG = "DRIVER_CAR_REG";
    public static final String DRIVER_UID = "DRIVER_UID";
    public static final String DRIVER_USERNAME = "DRIVER_USERNAME";
    public static final String DRIVER_TOKEN = "DRIVER_TOKEN";
    public static final String DRIVER_DEPARTURE_DATE = "DRIVER_DEPARTURE_DATE";
    public static final String LOGIN = "LOGIN";

    public static String LOGGED_IN = "null";
    public static String SET_LAN = "false";

    public static final String CONFIRMATION_CODE = "CONFIRMATION_CODE";

    public static final String LANGUAGE = "LANGUAGE";


    public static final String API_NAME_REGISTER_DRIVER = BASE_URL+"register_driver.php";
    public static final String API_NAME_LOGIN_DRIVER = BASE_URL+"login.php";
    public static final String API_NAME_SIGN_OUT = BASE_URL+"logout.php";
    public static final String API_NAME_UPDATE_DRIVER_STATUS = BASE_URL+"update_driver_status.php";
    public static final String API_NAME_UPDATE_BOOKING_RIDE_STATUS = BASE_URL+"update_booking_request_status.php";
    public static final String API_NAME_UPDATE_DRIVER_LOCATION = BASE_URL+"update_driver_location.php";
    public static final String API_NAME_UPDATE_RATING_BY_DRIVER = BASE_URL+"update_request_rating_driver.php";
    public static final String API_NAME_GET_BOOKING_DETAILS = BASE_URL+"get_booking_detail_driver.php";
    public static final String API_NAME_GET_PREVIOUS_RIDES = BASE_URL+"get_drivers_bookings.php";
    public static final String API_NAME_UPDATE_BOOKING_REQUEST_STATUS_DRIVER = BASE_URL+"update_booking_request_status_driver.php";

    public static final String BACK_STACK_REGISTRATION_1 = "registration_1";
    public static final String BACK_STACK_REGISTRATION_2 = "registration_2";
    public static final String BACK_STACK_REGISTRATION_3 = "registration_3";
    public static final String DIALOG_NAME_SIGN_OUT = "dialog_name_sign_out";
    public static final String DIALOG_NAME_LOGIN_RETRY = "dialog_name_login_retry";
    public static final String DIALOG_NAME_ARRIVED = "dialog_name_arrived";
    public static final String PAYMENT_BASE_DAILY = "Daily";
    public static final String PAYMENT_BASE_WEEKLY = "Weekly";
    public static final String PAYMENT_BASE_MONTHLY = "Monthly";
    public static final String PLAIN_VEHICLE = "Plain";
    public static final String PAINTED_VEHICLE = "Painted";
    public static final String DRIVER_STATUS_ONLINE = "ONLINE";
    public static final String DRIVER_STATUS_OFFLINE = "OFFLINE";
    public static final String RIDE_STATUS_ACCEPTED = "ACCEPTED";
    public static final String RIDE_STATUS_CANCELLED = "REJECTED";
    public static final String RIDE_STATUS_EXPIRED = "EXPIRED";
    public static final String RIDE_STATUS_STARTED = "STARTED";
    public static final String RIDE_STATUS_ARRIVED = "ARRIVED";
    public static final String RIDE_STATUS_COMPLETED = "COMPLETED";
    public static final String APP_LIFE_STATUS_FOREGROUND = "app_life_status_foreground";
    public static final String REQUEST_TYPE_REGULAR = "Regular";
    public static final String PAYMENT_METHOD_PAYPAL = "Paid via Paypal";
    public static final String PAYMENT_METHOD_CASH = "Paid via Cash";
    public static final String PAYMENT_METHOD_CARD = "Paid via Card";
    public static final String NOTIFICATION_STATUS_NEW_LOGIN_DETECTED = "New Login Detected";
    public static final String NOTIFICATION_STATUS_NEW_REQUEST_RECEIVED = "New Booking Received";
    public static final String BUTTON_NAME_START_RIDE = "Start Ride";
    public static final String BUTTON_NAME_ARRIVED_PICK_UP = "Arrived Pick Up";
    public static final int SPLASH_TIMEOUT = 5000;

    public static final String INTENT_EXTRA_SCREEN_NAME = "screen_name";
    public static final String INTENT_EXTRA_REQUEST_ID = "request_id";
    public static final String INTENT_EXTRA_IS_OPEN_DRAWER_SCREEN = "open_drawer_screen";
    public static final String INTENT_EXTRA_DRAWER_SCREEN_NUMBER = "drawer_screen_number";
    public static final String INTENT_EXTRA_IS_PUSH_NOTIFICATION_AVAILABLE = "is_push_notification";
    public static final String INTENT_EXTRA_RIDE_RECEIVER_STATUS = "receiver_status";
    public static final String INTENT_EXTRA_RIDE_APP_LIFE_STATUS = "app_life_status";
    public static final String INTENT_EXTRA_NOTIFICATION_NAME = "notification_name";
    public static final String INTENT_IS_REQUEST_FROM_BACKGROUND = "is_request_from_back_ground";
    public static final String INTENT_EXTRA_IS_ACTIVE_BOOKING_AVAILABLE = "is_active_booking_available";

    public static final String BUNDLE_EXTRA_BOOKING_DETAIL = "bundle_booking_detail";

    public static final String SHARED_PREFERENCE_APP = "app_shared_preference";
    public static final String PREFERENCE_EXTRA_DRIVER_ID = "driver_id";
    public static final String PREFERENCE_EXTRA_REGISTRATION_ID = "registration_id";
    public static final String PREFERENCE_EXTRA_FIRST_NAME = "first_name";
    public static final String PREFERENCE_EXTRA_LAST_NAME = "last_name";
    public static final String PREFERENCE_EXTRA_PASSWORD = "password";
    public static final String PREFERENCE_EXTRA_MOBILE_COUNTRY = "mobile_country";
    public static final String PREFERENCE_EXTRA_MOBILE_CODE = "mobile_country_code";
    public static final String PREFERENCE_EXTRA_MOBILE_NUMBER = "mobile_number";
    public static final String PREFERENCE_EXTRA_EMAIL = "email";
    public static final String PREFERENCE_EXTRA_DRIVER_PICTURE = "driver_picture";
    public static final String PREFERENCE_EXTRA_DRIVER_PICTURE_NAME = "driver_picture_name";
    public static final String PREFERENCE_EXTRA_LICENSE_NUMBER = "license_number";
    public static final String PREFERENCE_EXTRA_VEHICLE_REG_NUMBER = "vehicle_reg_number";
    public static final String PREFERENCE_EXTRA_VEHICLE_MAKER_MODEL = "vehicle_maker_model";
    public static final String PREFERENCE_EXTRA_VEHICLE_MODEL_YEAR = "vehicle_model_year";
    public static final String PREFERENCE_EXTRA_VEHICLE_CLASS_ID = "vehicle_class_id";
    public static final String PREFERENCE_EXTRA_VEHICLE_TYPE = "vehicle_type";
    public static final String PREFERENCE_EXTRA_IS_PLAIN = "is_plain";
    public static final String PREFERENCE_EXTRA_IS_AC_AVAILABLE = "is_ac_available";
    public static final String PREFERENCE_EXTRA_LICENSE_PICTURE = "license_picture";
    public static final String PREFERENCE_EXTRA_VAN_INSURANCE_PICTURE = "van_insurance_picture";
    public static final String PREFERENCE_EXTRA_TRANSIT_INSURANCE_PICTURE = "transit_insurance_picture";
    public static final String PREFERENCE_EXTRA_LICENSE_PICTURE_NAME = "license_picture_name";
    public static final String PREFERENCE_EXTRA_VAN_INSURANCE = "van_insurance";
    public static final String PREFERENCE_EXTRA_TRANSIT_INSURANCE = "transit_insurance";
    public static final String PREFERENCE_EXTRA_BANK_NAME = "bank_name";
    public static final String PREFERENCE_EXTRA_ACCOUNT_NAME = "account_name";
    public static final String PREFERENCE_EXTRA_ACCOUNT_NUMBER = "account_number";
    public static final String PREFERENCE_EXTRA_SORT_CODE = "sort_code";
    public static final String PREFERENCE_EXTRA_DRIVER_HELPERS = "driver_helpers";
    public static final String PREFERENCE_EXTRA_USER_CURRENT_LATITUDE = "current_latitude";
    public static final String PREFERENCE_EXTRA_USER_CURRENT_LONGITUDE = "current_longitude";
    public static final String PREFERENCE_EXTRA_IS_LOGGED_IN = "is_logged_in";
    public static final String PREFERENCE_EXTRA_REQUEST_ID = "request_id";
    public static final String PREFERENCE_EXTRA_CURRENCY_SYMBOL = "currency_symbol";
    public static final String PREFERENCE_EXTRA_REQUEST_PICK_UP_LOCATION = "request_pick_up";
    public static final String PREFERENCE_EXTRA_REQUEST_DROP_OFF_LOCATION = "request_drop_off";
    public static final String PREFERENCE_EXTRA_REQUEST_SPECIAL_INSTRUCTIONS = "special_instructions";
    public static final String PREFERENCE_EXTRA_REQUEST_PICK_UP_LATITUDE = "pick_up_latitude";
    public static final String PREFERENCE_EXTRA_REQUEST_PICK_UP_LONGITUDE = "pick_up_longitude";
    public static final String PREFERENCE_EXTRA_REQUEST_TYPE = "request_type";
    public static final String PREFERENCE_EXTRA_REQUEST_INVENTORY_ITEMS = "inventory_items";
    public static final String PREFERENCE_EXTRA_IS_APP_IN_FOREGROUND = "is_app_in_foreground";
    public static final String PREFERENCE_EXTRA_IS_REQUEST_RECEIVED = "is_request_received";
    public static final String PREFERENCE_EXTRA_IS_FROM_WELCOME_SCREEN = "is_from_welcome_screen";
    public static final String PREFERENCE_EXTRA_IS_REGISTRATION_IN_PROCESS = "is_registration_in_process";
    public static final String PREFERENCE_EXTRA_OPERATION_NAME = "operation_name_add_edit";
    public static final String PREFERENCE_EXTRA_DRIVER_ACTIVE_HELPERS = "driver_helpers";
    public static final String PREFERENCE_EXTRA_IS_DRIVER_ACTIVE = "is_driver_active";

    public static final int REQUEST_CODE_PORTER_PICTURE = 111;
    public static final int REQUEST_CODE_PORTER_ID_PICTURE = 222;
    public static final int MY_PERMISSION_REQUEST_CODE = 7171;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    public static final int UPDATE_INTERVAL = 10000; // SEC
    public static final int FATEST_INTERVAL = 5000; // SEC
    public static final int DISPLACEMENT = 3; // METERS
    public static final int DISTANCE_TO_ROTATE = 20;
    public static final int REQUEST_CODE_CAMERA = 7000;
    public static final double ONE_MILE = 0.621371;

    /*LOGIN DATA*/
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String TOKEN_TYPE = "TOKEN_TYPE";
    public static final String EXPIRES_IN ="EXPIRES_IN";
    public static final String FIRSTNAME = "FIRSTNAME";
    public static final String LASTNAME = "LASTNAME";
    public static final String SUBORGANIZATION_ID = "SUBORGANIZATION_ID";
    public static final String DRIVER_PLANLANE_ID = "DRIVER_PLANLANE_ID";
    public static final String DEPARTURE_DATE = "DEPARTURE_DATE";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LINE_NAME = "LINE_NAME";
    public static final String SUBORGANIZATION_NAME = "SUBORGANIZATION_NAME";
    public static final String ISSUED = "ISSUED";
    public static final String EXPIRES = "EXPIRES";

}