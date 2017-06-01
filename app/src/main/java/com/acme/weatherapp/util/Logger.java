package com.acme.weatherapp.util;

import android.util.Log;

/**
 * Clase para hacer el log de la aplicacion
 *
 * @author Jorge Henao
 */
public class Logger {

    private Class<?> origin;

    private String weather = "Wheater->";

    /**
     * Constructor privado que recibe la clase origen del log
     *
     * @param origin
     */
    private Logger(Class<?> origin){
        this.origin = origin;
    }

    /**
     * Construye un nuevo objeto para escritura de logs de la clase enviada
     *
     * @param origin
     * @return
     */
    public static Logger getLogger(Class<?> origin){
        return new Logger(origin);
    }

    /**
     * Escribe en el log un mensaje con prioridad DEBUG
     *
     * @param message Mensaje
     */
    public void debug(String message){
        Log.d(weather, origin.getName() + ": " + message);
    }

    /**
     * Escribe en el log un mensaje con prioridad INFO
     *
     * @param message Mensaje
     */
    public void info(String message){
        Log.i(weather, origin.getName() + ": " + message);
    }

    /**
     * Escribe en el log un mensaje con prioridad ERROR
     *
     * @param message Mensaje
     */
    public void error(String message){
        Log.e(weather, origin.getName() + ": " + message);
    }

    /**
     * Escribe en el log un mensaje con prioridad DEBUG
     *
     * @param message Mensaje
     * @param tr Excepcion
     */
    public void error(String message, Throwable tr){
        Log.e(weather, origin.getName() + ": " + message, tr);
    }
}