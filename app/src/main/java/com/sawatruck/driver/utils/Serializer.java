package com.sawatruck.driver.utils;

import com.google.gson.Gson;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.entities.AddressDetail;
import com.sawatruck.driver.entities.Advertisement;
import com.sawatruck.driver.entities.Balance;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.entities.OfferDetail;
import com.sawatruck.driver.entities.SawaTruckLocation;
import com.sawatruck.driver.entities.Truck;
import com.sawatruck.driver.entities.User;

/**
 * Created by royal on 8/28/2017.
 */

public class Serializer {
    private final Gson gson = BaseApplication.getGson();
    public Serializer() {}

    public static Serializer _instance ;
    static {
        _instance = new Serializer();
    }

    public static Serializer getInstance(){
        return _instance;
    }

    public String serializeUser(User user) {
        String jsonString = gson.toJson(user, User.class);
        return jsonString;
    }

    public User deserializeUser(String jsonString) {
        User user = gson.fromJson(jsonString, User.class);
        return user;
    }

    public String serializeBalance(Balance balance) {
        String jsonString = gson.toJson(balance, Balance.class);
        return jsonString;
    }

    public Balance deserializeBalance(String jsonString) {
        Balance balance = gson.fromJson(jsonString, Balance.class);
        return balance;
    }

    public String serializeTruck(Truck truck) {
        String jsonString = gson.toJson(truck, Truck.class);
        return jsonString;
    }

    public Truck deserializeTruck(String strTruck) {
        Truck truck = gson.fromJson(strTruck, Truck.class);
        return truck;
    }

    public String serializeLoad(Load load) {
        String jsonString = gson.toJson(load, Load.class);
        return jsonString;
    }

    public Load deserializeLoad(String strLoad) {
        Load load = gson.fromJson(strLoad, Load.class);
        return load;
    }

    public SawaTruckLocation deserializeLocation(String strFromLocation) {
        SawaTruckLocation location = gson.fromJson(strFromLocation, SawaTruckLocation.class);
        return location;
    }

    public String serializeLocation(SawaTruckLocation toLocation) {
        String jsonString = gson.toJson(toLocation, SawaTruckLocation.class);
        return jsonString;
    }

    public String serializeAddressDetail(AddressDetail addressDetail) {
        String jsonString = gson.toJson(addressDetail, AddressDetail.class);
        return jsonString;
    }

    public AddressDetail deserializeAddressDetail(String strAddressDetail) {
        AddressDetail addressDetail = gson.fromJson(strAddressDetail, AddressDetail.class);
        return addressDetail;
    }

    public String serializeAdvertisement(Advertisement advertisement) {
        String jsonString = gson.toJson(advertisement, Advertisement.class);
        return jsonString;
    }

    public Advertisement deserializeAdvertisement(String strAdvertisement) {
        Advertisement advertisement = gson.fromJson(strAdvertisement, Advertisement.class);
        return advertisement;
    }

    public String serializeOfferDetail(OfferDetail offer) {
        String jsonString = gson.toJson(offer, OfferDetail.class);
        return jsonString;
    }

    public OfferDetail deserializeOfferDetail(String strOfferDetail){
        OfferDetail offerDetail = gson.fromJson(strOfferDetail, OfferDetail.class);
        return offerDetail;
    }
}

