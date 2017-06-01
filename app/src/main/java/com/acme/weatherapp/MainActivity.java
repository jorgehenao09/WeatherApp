package com.acme.weatherapp;

import butterknife.BindView;
import retrofit2.Call;

import android.Manifest;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.acme.weatherapp.dto.Currently;
import com.acme.weatherapp.dto.Weather;
import com.acme.weatherapp.endpoint.CurrentWeather;
import com.acme.weatherapp.util.BaseActivity;
import com.acme.weatherapp.util.RestApiUtil;

import java.util.Arrays;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_estadoTiempo) TextView estadoTiempo;
    @BindView(R.id.tv_gradosTemperatura) TextView gradosTemperatura;
    @BindView(R.id.tv_humedad) TextView humedad;
    @BindView(R.id.tv_presion) TextView presion;
    @BindView(R.id.tv_probabilidadLluvia) TextView probabilidadLluvia;
    @BindView(R.id.tv_sensacionTermica) TextView sensacionTermica;
    @BindView(R.id.tv_velocidadViento) TextView velocidadViento;
    @BindView(R.id.tv_visibilidad) TextView visibilidad;

    private static final String KEY = "786a596d542718ff2e7d735e446f58e2";
    private static final String baseURL = "https://api.darksky.net/";

    /**
     * Instancia de retrofit
     */
    private Retrofit retrofit;

    /**
     * {@inheritDoc}
     */
    @Override
    public void inicializarView() {
        super.inicializarView();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            //Se inicializa la instancia de retrofit con la URL base
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create(RestApiUtil.getGsonInstance()))
                    .build();

            CurrentWeather service = retrofit.create(CurrentWeather.class);

            //Se verifica que las coordenadas sean correctas
            if (getLatitudActual() != null && getLongitudActual() != null){
                Call<Weather> call = service.getCurrentWeather(KEY, getLatitudActual(), getLongitudActual());

                Response<Weather> response = call.execute();

                inicializarDatos(response.body().getCurrently());
            } else {
                mostrarMensajeError(getResources().getString(R.string.configuracionGPS),getResources().getString(R.string.error_activandoGPS));
            }

        } catch (Exception ex){
            Log.e("Weather", "Error", ex);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //prepara el localizador
        prepararLocalizador();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //suspender el localizador
        pararLocalizador();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * Metodo utilizado para asociar los datos obtenidos con los campos en pantalla
     *
     * @param datos
     */
    private void inicializarDatos(Currently datos){

        estadoTiempo.setText(datos.getSummary());
        probabilidadLluvia.setText(String.valueOf(datos.getPrecipProbability() * 100));
        gradosTemperatura.setText(datos.getTemperature().toString() + "°");
        velocidadViento.setText(datos.getWindSpeed().toString());
        sensacionTermica.setText(datos.getApparentTemperature().toString() + "°");
        humedad.setText(datos.getHumidity().toString());
        visibilidad.setText(datos.getVisibility().toString());
        presion.setText(datos.getPressure().toString() + " mbar");
    }

    /**
     * Retorna la lista de permisos que necesita la actividad
     */
    @Override
    protected List<String> obtenerListaPermisos() {
        return Arrays.asList(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
        );
    }

    /**
     * Metodo utilizado al momento de dar clic en el boton de refrescar
     *
     * @param view
     */
    public void sincronizar(View view){
        try{
            //Se inicializa la instancia de retrofit con la URL base
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create(RestApiUtil.getGsonInstance()))
                    .build();

            CurrentWeather service = retrofit.create(CurrentWeather.class);

            //Se verifica que las coordenadas sean correctas
            if (getLatitudActual() != null && getLongitudActual() != null){
                Call<Weather> call = service.getCurrentWeather(KEY, getLatitudActual(), getLongitudActual());

                Response<Weather> response = call.execute();

                inicializarDatos(response.body().getCurrently());
            } else {
                mostrarMensajeError(getResources().getString(R.string.configuracionGPS),getResources().getString(R.string.error_activandoGPS));
            }

        } catch (Exception ex){
            Log.e("Weather", "Error", ex);
        }
    }
}
