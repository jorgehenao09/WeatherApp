package com.acme.weatherapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.acme.weatherapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Actividad Base con funciones para las actividades del proyecto
 *
 * @author Jorge Henao
 */

public abstract class BaseActivity extends AppCompatActivity implements LocationListener {

    private static Logger logger = Logger.getLogger(BaseActivity.class);

    private ProgressDialog waitDialog;

    private static final Integer SOLICITUD_PERMISOS_ACTIVIDAD = 2017;

    //Para manejo de la localizacion
    private LocationManager locationManager = null;

    private String latitudActual;
    private String longitudActual;
    private String fuenteCoordenada;

    protected Boolean permisosRequeridosOtorgados = Boolean.TRUE;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        bindViews();
        construirPresenter();

        verificacionPermisos(obtenerListaPermisos());
        inicializarView();
    }

    /**
     * Verifica cada uno de los permisos y si alguno no es otorgado, se cierra la actividad
     *
     * @param permisosRequeridos
     */
    private void verificacionPermisos(List<String> permisosRequeridos){
        if(permisosRequeridos.size() > 0 && !permisosRequeridos.isEmpty()) {

            permisosRequeridosOtorgados = Boolean.FALSE;

            //Lista para los permisos no otorgados
            List<String> permisosNoOtorgados = new ArrayList<>();

            //Revisamos cuales de los permisos no estan otorgados
            for(String permiso : permisosRequeridos){
                if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                    permisosNoOtorgados.add(permiso);
                }
            }

            //Si hay permisos no otorgados, se solicita al usuario autorizacion
            if(!permisosNoOtorgados.isEmpty()){
                ActivityCompat.requestPermissions(
                        this,
                        permisosNoOtorgados.toArray(new String[permisosNoOtorgados.size()]),
                        SOLICITUD_PERMISOS_ACTIVIDAD);
            }else{
                //Todos los permisos requeridos estan otorgados
                permisosRequeridosOtorgados = Boolean.TRUE;
            }
        }else{
            permisosRequeridosOtorgados = Boolean.TRUE;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean todosPermisosFueronOtorgados = false;

        if(requestCode == SOLICITUD_PERMISOS_ACTIVIDAD){
            for(int resultado : grantResults){
                if(resultado == PackageManager.PERMISSION_DENIED){
                    todosPermisosFueronOtorgados = false;

                    mostrarMensajeError("Permisos no otorgados", "Usted debe dar acceso a todos los permisos requeridos");
                    break;
                }
            }
        }

        if(todosPermisosFueronOtorgados){
            permisosRequeridosOtorgados = Boolean.TRUE;
        }else{
            permisosRequeridosOtorgados = Boolean.FALSE;
        }
    }

    /**
     * Use este metodo para inicializar los componentes de la vista
     */
    public void inicializarView() {}

    /**
     * Use este metodo para inicializar el objeto presenter de la vista actual
     */
    public void construirPresenter() {}

    /**
     * Metodo que activa el enlace de los controles con ButterKnife
     */
    private void bindViews() {
        ButterKnife.bind(this);
    }

    /**
     * @return El layout id que contiene toda la vista
     */
    protected abstract int getLayoutId();

    /**
     * Retorna la lista de permisos que necesita la actividad
     *
     * @return
     */
    protected List<String> obtenerListaPermisos(){
        return new ArrayList<>();
    }

    /**
     * Muestra un mensaje para notificar el resultado de una operacion
     *
     * @param textoMensaje
     */
    public void mostrarMensajeInformacion(String textoMensaje){
        Toast.makeText(getApplicationContext(), textoMensaje, Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra una ventana con un mensaje de error
     *
     * @param tituloVentana
     * @param textoMensaje
     */
    public void mostrarMensajeError(String tituloVentana, String textoMensaje){
        //Se oculta el mensaje de espera si se esta visualizando
        ocultarMensajeEspera();

        if(getApplicationContext() != null || !((Activity) getApplicationContext()).isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //No hacer nada
                }
            });

            builder.setTitle(tituloVentana);
            builder.setMessage(textoMensaje);
            AlertDialog currentDialog = builder.create();

            currentDialog.show();
        }
    }

    /**
     * Muestra un mensaje para indicar que hay un proceso actualmente en el sistema
     */
    public void mostrarMensajeEspera(){
        if(waitDialog == null){
            waitDialog = new ProgressDialog(this);
            waitDialog.setTitle(getString(R.string.estamosTrabajando));
            waitDialog.setMessage(getString(R.string.informacion_procesandoPeticionEspere));
            waitDialog.setIndeterminate(true);
            waitDialog.setCancelable(false);
            waitDialog.setCanceledOnTouchOutside(false);
            waitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    onCancelCurrentTask(dialog);
                }
            });
        }else{
            waitDialog.setMessage(getString(R.string.informacion_procesandoPeticionEspere));
        }

        waitDialog.show();
    }

    protected void onCancelCurrentTask(DialogInterface dialog){
        //By default, close wait screen
        ocultarMensajeEspera();
    }

    /**
     * Oculta el mensaje de espera que indicaba un procesos en curso
     */
    public void ocultarMensajeEspera(){
        if(waitDialog != null && waitDialog.isShowing()){
            waitDialog.dismiss();
        }
    }

    protected void prepararLocalizador(){
        latitudActual = null;
        longitudActual = null;
        fuenteCoordenada = null;

        if ( Build.VERSION.SDK_INT >= 17 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // localizacion
        locationManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);

        if ( locationManager == null ) {
            mostrarMensajeInformacion(getString(R.string.error_activandoGPS));
        }

        try{
            // recepcion de datos del GPS
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, (LocationListener)this);
        } catch (Exception e){
            mostrarMensajeInformacion(getString(R.string.error_activandoGPS));
            logger.error("Error inicializado el GPS", e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitudActual = String.valueOf(location.getLatitude());
        longitudActual = String.valueOf(location.getLongitude());
        fuenteCoordenada = location.getProvider();

        logger.debug("La coordenada se ha actualizado a lat=" + latitudActual + ", lon=" + longitudActual + ", proveedor=" + fuenteCoordenada);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        logger.debug("El sensor de localizacion ha cambiado su estado a " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        logger.debug("El sensor de localizacion ha sido activado");
    }

    @Override
    public void onProviderDisabled(String provider) {
        mostrarAlertaGPS();

        if ( Build.VERSION.SDK_INT >= 17 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        logger.debug("El sensor de localizacion esta desactivado para el provider " + provider);

        if ("GPS".equalsIgnoreCase(provider)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, (LocationListener)this);
        }
    }

    public void pararLocalizador(){
        if ( Build.VERSION.SDK_INT >= 17 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if ( locationManager != null ) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Obtiene la ultima latitud leida por el sensor
     *
     * @return
     */
    public String getLatitudActual() {
        return latitudActual;
    }

    /**
     * * Obtiene la ultima longitud leida por el sensor
     *
     * @return
     */
    public String getLongitudActual() {
        return longitudActual;
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void mostrarAlertaGPS(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.configuracionGPS));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.advertencia_gpsNoHabilitado));

        // On pressing the Settings button.
        alertDialog.setPositiveButton(getString(R.string.configurar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
