package com.bp.loginsmartworddemo.services;



import com.bp.loginsmartworddemo.model.AddressTextSearchResponse;
import com.bp.loginsmartworddemo.model.Ebike;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface DataClient {

    @GET("json")
    Call<AddressTextSearchResponse> loadAddressResponse(@Query("query") String query,
                                                        @Query("key") String key);

    @GET("/Ebike.json")
    Call<List<Ebike>> loadEBike();

    @GET("/json?query=garage&key=AIzaSyBU10WQMbL2hr8a-YzD0CxSot_1DVCAWlI")
    Call<List<Ebike>> loadgara();
}
