package com.bytesbee.provpnapp.rests;

import com.bytesbee.provpnapp.models.APIKey;
import com.bytesbee.provpnapp.models.CallbackServers;
import com.bytesbee.provpnapp.models.CallbackSettings;
import com.bytesbee.provpnapp.models.CallbackSubscription;
import com.bytesbee.provpnapp.models.SubscriptionInfo;
import com.bytesbee.provpnapp.models.VPNServers;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link http://bytesbee.com
 */
public interface ApiInterface {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "Data-Agent: My VPN Pro";

    @Headers({CACHE, AGENT})
    @POST("api/getServers")
    Call<CallbackServers> getServers(@Body VPNServers vpnServers);

    @Headers({CACHE, AGENT})
    @POST("api/getAppSettings")
    Call<CallbackSettings> getSettings(@Body APIKey apiKey);

    @Headers({CACHE, AGENT})
    @POST("api/insertSubscription")
    Call<CallbackSubscription> insertSubscription(@Body SubscriptionInfo subscriptionInfo);
}
