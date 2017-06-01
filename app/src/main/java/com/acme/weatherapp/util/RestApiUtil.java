package com.acme.weatherapp.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RestApiUtil {

	public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss SSS";
	
	/**
     * Construye el formateador de numero donde el separador de miles es la coma y decimal el punto
     *  
     * @return
     */
    public static DecimalFormat obtenerFormateadorNumeros(int minimoDecimales, int maximoDecimales){
        
        //Se definen los separadores validos para numeros
        DecimalFormatSymbols separadores = new DecimalFormatSymbols();
        separadores.setDecimalSeparator('.');
        separadores.setGroupingSeparator(',');
        
        DecimalFormat df = new DecimalFormat();
        //Se le definen al decimal format los simbolos validos
        df.setDecimalFormatSymbols(separadores);
        df.setGroupingSize(3);
                    
        //Minimo y maximo de decimales
        df.setMinimumFractionDigits(minimoDecimales);
        df.setMaximumFractionDigits(maximoDecimales);
            
        return df;
    }
    
    /**
     * Returns the value of a date in a text string, using the supplied format. If the date is null 
     * returns an empty string if an exception occurs during conversion, it returns null
     * 
     * @param date
     * @return formatted date
     */
    public static String formatDate(Date date) {
        if(date == null){
        	return null;
        }
    	
    	String s = new String();

        try {
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
                sdf.setLenient(false);
                s = sdf.format(date);
            }

            return s;
        } catch (Exception t) {
        	System.err.println("Error al formatear la fecha "+date+" al formato:"+DATE_TIME_FORMAT + t.getStackTrace());
            return null;
        }
    }
    
    /**
     * Returns the value of a date in a date object, using the supplied format. If the text string is null 
     * returns an null object
     * 
     * @param date
     * @return formatted date
     */
    public static Date formatDate(String date) {
    	if(date == null){
        	return null;
        }
    	
    	try {
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
                sdf.setLenient(false);
                return sdf.parse(date);
            }
        } catch (Exception t) {
        	System.err.println("Error al formatear la fecha "+date+" al formato:"+DATE_TIME_FORMAT + t.getStackTrace());
        }
        
        return null;
    }
    
    public static BigDecimal convertFloatToBigDecimal(Float number) {
    	if(number == null){
    		return null;
    	}
    	
    	return new BigDecimal(number);
    }
    
    public static Float convertBigDecimalToFloat(BigDecimal number) {
    	if(number == null){
    		return null;
    	}
    	
    	return new Float(number.floatValue());
    }
    
	/**
	 * Get the Gson instance with respectives serializers and deserializers
	 * 
	 * @return
	 */
	public static Gson getGsonInstance(){
		GsonBuilder gson = new GsonBuilder();
		gson.setDateFormat(DATE_TIME_FORMAT);
		
		return gson.create();
	}
}
