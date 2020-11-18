package com.bp.loginsmartworddemo.services;

public class APIUtils {
    private static final String base_Url1 = "https://maps.googleapis.com/maps/api/place/textsearch/";
    private static final String base_Url_Firebase = "https://loginsmartworddemo.firebaseio.com/";


    /* Send and receive data inside interface "DataClient" from base_url */
    public static DataClient getDataMap(){
        return RetrofitClient.getClient(base_Url1).create(DataClient.class);
    }

    public static DataClient getDataEbike(){
        return RetrofitClient.getClient(base_Url_Firebase).create(DataClient.class);
    }

    public static DataClient getDataGara(){
        return RetrofitClient.getClient(base_Url1).create(DataClient.class);
    }
}
