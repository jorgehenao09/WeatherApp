package com.acme.weatherapp.endpoint;

import com.acme.weatherapp.dto.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Jorge Henao on 1/06/2017.
 */

public interface CurrentWeather {

    @GET("forecast/{apikey}/{latitud},{longitud}")
    Call<Weather> getCurrentWeather(@Path("apikey") String apikey,@Path("latitud") String latitud,@Path("longitud") String longitud);
}
