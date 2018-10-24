package com.sawatruck.driver;

import android.net.Uri;

import com.paypal.android.sdk.payments.PayPalConfiguration;

/**
 * Created by royal on 8/26/2017.
 */

public class Constant {
    public static final String SAWATRUCK_CLIENT_ID = "sawaTruckDriverApplication";
    public static final String SAWATRUCK_CLIENT_SECRET = "pn546(*&hjd8421amasdkjh*GK==%^np";

    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String DISTANCE_RADIUS = "500";
//    public static final String PLACES_API_KEY = "AIzaSyDbyAkQGz9tSBdyHPagbcaDDRfxl9czPJc";
    public static final String PLACES_API_KEY = "AIzaSyAAzs-C69wqP43m33lSXc1VlW8-MCFJ3S4";

    public static final String GOOGLE_MAP_KEY = "AIzaSyDLMZ0087pCg-HgZpMpYbveY6EYUN5J3g4";

    public static final String GEOCODE_API_KEY = "AIzaSyDISGEuHxELRW1YRN9Yw7-Z56CoGoSuVkE";

    public static final String STRIPE_API_KEY = "pk_live_gTAtlSBDBRJdd4M8HOMoBpme";

    public static final String GOOGLE_API_SCOPE_URL = "https://www.googleapis.com/auth/plus.login";

    public static final int GET_PLACE_REQUEST_CODE = 2017;

    public static final String PAYPAL_CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
//    public static final String PAYPAL_CONFIG_CLIENT_ID = "AQ1M1C63SjloTTOiL3hGpTCtrjUTUtUs3d7aaVJjENlM0814uoyHq0VJySQd8paqNIgWPIBbHNg89pLJ";
    public static final String PAYPAL_CONFIG_CLIENT_ID = "AeEI95tx7BXxzJmw-kBr7FlyKIMx-SKAE1CO2BTStLkJ9lMYUR6u-Sfjyrsx8gsP_NBOO4f24iCPf6QY";

    public static final int PAYPAL_REQUEST_CODE = 2020;
    public static final String IS_EMAIL_VERIFIED_API = "http://api.sawatruck.com/api/users/IsEmailConfirmed";
    public static final String TRAVEL_COMPLAINT_REASON_API = "http://api.sawatruck.com/api/ComplaintReasons/get";
    public static final String POST_COMPLAIN_API = "http://api.sawatruck.com/api/Complaints/Post";
    public static final String GET_CLIENT_DUES_API = "http://api.sawatruck.com/api/Middleman/ClientDues";
    public static final String INTENT_SUBMIT_TRUCK = "edit_or_add_truck";
    public static final String INTENT_SUBMIT_AD = "edit_or_post_ad";
    public static final String INTENT_TRAVEL_TYPE = "travel_type";

    public static final String CANCEL_OFFER_API = "http://api.sawatruck.com/api/Offers/CancelOffer/";
    public static final String REJECT_OFFER_API = "http://api.sawatruck.com/api/Offers/RejectOffer/";
    public static final int TRACKING_REQUEST_CODE = 0;
    public static final long TRACKING_INTERVAL = 5000;
    public static final int FIREBASE_NOTIFY = 220;
    public static final String INTENT_OFFER_ID = "offer_id";
    public static final String INTENT_FROM_LOCATION = "from_location";
    public static final String INTENT_TO_LOCATION = "to_location";
    public static final String INTENT_LOCATION = "map_location";
    public static final String PARAM_AUTHORIZATION =  "Authorization";
    public static final String INTENT_ADVERTISEMENT_ID = "advertisement_id";
    public static final String INTENT_USER_ID = "user_id";
    public static final String INTENT_USERNAME = "username";
    public static final String INTENT_TRAVEL_ID = "travel_id";
    public static final String INTENT_LOAD_ID = "load_id";
    public static final String INTENT_TRACKING_STATUS = "trackingStatus";

    public static final String TRUCK_ID = "truck_id";
    public static final String GET_ACTIVE_SESSION_API = "http://api.sawatruck.com/api/Tracking/GetActiveSession";


    public static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(PAYPAL_CONFIG_ENVIRONMENT)
            .clientId(PAYPAL_CONFIG_CLIENT_ID)

            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("sawatruckdriver")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


    public static final String BROADCAST_ACTION = "Send Message";

    public static final String API_URL = "http://api.sawatruck.com/api/";
    public static final String USER_GETBALANCE_API = "http://api.sawatruck.com/api/Users/GetUserBalance";
    public static final String GET_MY_TRUCKS_API = "http://api.sawatruck.com/api/Trucks/GetMyTrucks";
    public static final String USER_SIGNIN_API = "http://api.sawatruck.com/Token";

    public static final String USER_CONTROLLER = API_URL.concat("Users/");

    public static final String USER_SIGNUPCOMPANY_API = USER_CONTROLLER.concat("SignupCompany");
    public static final String USER_SIGNUPDRIVER_API = USER_CONTROLLER.concat("SignupDriver");

    public static final String USER_FORGOTPASSWORD_API = USER_CONTROLLER.concat("ForgetPassword");
    public static final String USER_CHANGEPASSWORD_API = USER_CONTROLLER.concat("ChangeUserPassword");

    public static final String LOADS_SEARCH_API = "http://api.sawatruck.com/api/loads/Search1";
    public static final String USER_EDITPROFILE_API = USER_CONTROLLER.concat("Put");


    public static final String RATE_TRAVEL_API = API_URL.concat("rating/rateLoader");
    public static final String GET_USER_RATING_API = USER_CONTROLLER.concat("GetUserRatingStatistics");


    public static final String GET_CURRENCIES_API = "http://api.sawatruck.com/api/Currencies/get";


    public static final String GET_MY_AD_API = "http://api.sawatruck.com/api/Advertisements/GetMyAdvertisements";
    public static final String ADD_AD_API = "http://api.sawatruck.com/api/Advertisements/Post";
    public static final String GET_LOAD_TYPES_API = "http://api.sawatruck.com/Api/LoadTypes/Get";
    public static final String GET_TRUCK_TYPE_API = "http://api.sawatruck.com/Api/VehicleTypes";
    public static final String GET_LOCATIONS_API = "http://api.sawatruck.com/api/locatios/Get";
    public static final String GET_TRUCK_BRAND_API = "http://api.sawatruck.com/Api/VehicleBrands/Get";
    public static final String USER_VIEWPROFILE_API = "http://api.sawatruck.com/api/users/MyInfo";
    public static final String GET_COLOR_API = "http://api.sawatruck.com/Api/ColorMultiLinguals/";
    public static final String GET_TRUCK_CLASSES_API = "http://api.sawatruck.com/Api/TruckClasses";
    public static final String GET_LOADS_API = "http://api.sawatruck.com/api/Loads/Get";
    public static final String GET_OFFER_API = "http://api.sawatruck.com/api/Offers/Get";
    public static final String ADD_NEW_OFFER_API = "http://api.sawatruck.com/api/Offers/post";
    public static final String VERIFY_PHONE_API = "http://api.sawatruck.com/api/users/VerifyPhoneCode";
    public static final String CONFIRM_PHONE_API = "http://api.sawatruck.com/api/users/ConfirmPhone";
    public static final String ADD_PROMOCODE_API = "http://api.sawatruck.com/Api/UserActiveCopon/AddCoponToUser";
    public static final String ADD_TRUCK_API = "http://api.sawatruck.com/api/Trucks/Post";
    public static final String EDIT_TRUCK_API = "http://api.sawatruck.com/api/Trucks/Put";

    public static final String GET_AD_BY_ID_API = "http://api.sawatruck.com/api/Advertisements/Get";
    public static final String TRANSACTION_HISTORY_API = "http://api.sawatruck.com/api/TransactionDetail/search";
    public static final String LOAD_SEARCH_API = "http://api.sawatruck.com/api/Loads/search";
    public static final String GET_MY_OFFERS_API = "http://api.sawatruck.com/api/Offers/GetMyOffers";
    public static final String GET_USER_INBOX_API = "http://api.sawatruck.com/api/Messages/GetMyMessages";
    public static final String GET_MESSAGE_API = "http://api.sawatruck.com/api/Messages/History";
    public static final String SEND_MESSAGE_API = "http://api.sawatruck.com/api/Messages/Post";
    public static final String GET_NOTIFICATIONS_UNSEEN_API = "http://api.sawatruck.com/api/NotificationUser/GetUnseen";
    public static final String MAKE_SEEN_NOTIFICATION_API = "http://api.sawatruck.com/api/NotificationUser/put";
    public static final String GET_MY_NOTIFICATIONS_API = "http://api.sawatruck.com/api/NotificationUser/GetMyNotification";
    public static final String ACCEPT_AD_BOOKING_API = "http://api.sawatruck.com/api/AdvertisementBookings/Approve" ;
    public static final String REJECT_AD_BOOKING_API = "http://api.sawatruck.com/api/AdvertisementBookings/Decline";
    public static final String EDIT_AD_API = "http://api.sawatruck.com/api/Advertisements/Put";
    public static final String CANCEL_DELIVERY_API = "http://api.sawatruck.com/api/Travels/Cancel";
    public static final String CHARGE_BALANCE_STRIPE_API = "http://api.sawatruck.com/api/AccountFinance/ChargeByStripe_DoCheckoutPayment";
    public static final String CANCEL_REASON_API = "http://api.sawatruck.com/api/CancelReasons/Get";
    public static final String CONFIRM_OFFER_API = "http://api.sawatruck.com/api/Offers/ConfirmOffer";

    public static final long LOCATION_TRACK_INTERVAL = 1000 * 20;
    public static final String START_TRAVEL_API = "http://api.sawatruck.com/api/Tracking/StartTravel";
    public static final String ADD_NEW_POINT_API = "http://api.sawatruck.com/api/Tracking/NewPoint";
    public static final String PICKUP_API = "http://api.sawatruck.com/api/Tracking/PickUp";
    public static final String END_TRACKING_API = "http://api.sawatruck.com/api/Tracking/EndTracking";
    public static final String UPLOAD_USER_PHOTO_API = "http://api.sawatruck.com/api/Upload/PostUserPhoto";
    //public static final String UPLOAD_PHOTO_API = "http://api.sawatruck.com/api/Upload/PostFormData";
    public static final String UPLOAD_PHOTO_API = "http://api.sawatruck.com/api/Upload/PostTruckPhoto";

    public static final String GET_TO_DO_API = "http://api.sawatruck.com/api/Tracking/GetToDo";


    public static final int PICKUP_LOCATION_REQUEST_CODE = 2040;
    public static final int DELIVERY_LOCATION_REQUEST_CODE = 2041;
    public static final int LOCATION_REQUEST_CODE = 2042;

    public static final String ARRIVED_PICKUP_API = "http://api.sawatruck.com/api/Tracking/Arrive";

    public static final String GET_PAYPAL_TOKEN_API = "http://api.sawatruck.com/api/AccountFinance/ChargeByPaybal_ShortcutExpressCheckout";
    public static final String CHARGE_BALANCE_PAYPAL_API = "http://api.sawatruck.com/api/AccountFinance/ChargeByPaybal_DoCheckoutPayment";

    public static final String GET_ALL_COUNTRIES_API = "http://api.sawatruck.com/api/Countries/get";
    public static final String GET_CITIES_BY_COUNTRY_API = "http://api.sawatruck.com/api/Cities/GetCitiesNameByCountry";
    public static final String GET_LOCATIONS_BYCITY_API = "http://api.sawatruck.com/api/Locatios/GetLocationByCityName";

    public static final String SEARCH_CITY_API = "http://api.sawatruck.com/api/cities/search";

    public static final String SEARCH_LOCATION_API = "http://api.sawatruck.com/api/Locatios/search";

    public static final String GET_TRAVEL_BY_ID = "http://api.sawatruck.com/api/travels/get";

    public static final String VERIFY_EMAIL_API = "http://api.sawatruck.com/api/users/ConfirmEmail";
    public static final String COLLECT_PAYMENT_API = "http://api.sawatruck.com/api/Middleman/CollectPayment";



    public enum OfferStatus {
        Deleted(0) , Active(1), Accepted(2), Rejected(3), Canceled(4), Confirmed(5), CanceledAfterConfirmed(6), CanceledAfterAccepted(7);
        public final int index;
        OfferStatus(int index) {
            this.index = index;
        }
    }

    public enum AdvertisementBookingStatus {
        Pending(0), Approved(1),Declined(2),Canceled (3),CanceledAfterApproved(4);
        public final int index;
        AdvertisementBookingStatus(int index) {
            this.index = index;
        }
    }

    public enum AdvertisementStatus {
        Canceled(0),Pending(1),Booked(2), CanceledAfterBooked(3);
        public final int index;
        AdvertisementStatus(int index) {
            this.index = index;
        }
    }

    public enum LoadStatus {
        Active(1), Reserved(2),Completed(3), Canceled(4),CanceledAfterConfirmed(5), CancelBeforConfirmed(6);
        public final int index;
        LoadStatus(int index) {
            this.index = index;
        }
    }

    public enum TravelStatus {
        Active(0),Canceled(1),Completed(2),Rated(3),CollectPayment(4),Deleted(5);
        public final int index;
        TravelStatus(int index) {
            this.index = index;
        }
    }

    public enum TrackingStatus {
        None(0) ,GoToPickUp(1),OnThePickUpWay(2),PickedUp(3),StartRealTravel(4),OnTheWay(5),End(6);
        public final int index;
        TrackingStatus(int index) {
            this.index = index;
        }
    }

//    http://api.sawatruck.com/api/general/TestFCM/TestFCM?ScreenName=LoadDetails&ObjectId=AF3B8D7A-B24A-43DC-B515-0138B3BBC09D
    //
    //https://docs.google.com/document/d/16HJg1vuPFILZQ3ahxtn47UIiunDNt6lu-gNApKukKSI/edit?usp=sharing
}

