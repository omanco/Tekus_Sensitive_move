package smarttouch.tekus_sensitive_move;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import smarttouch.tekus_sensitive_move.Base_datos_y_contrato.Contrato;


/**
 * Created by ASUS on 18/01/2017.
 */

public class WebApi_Volley extends AppCompatActivity {

    private String DATE,DURATION;
    private int NOTIFICATIONID;
    public static final String LOG_NOTI = "LOG_NOTI";


    public void Post_movimiento (final Context context,String dateANDhour) {

        RequestQueue queue = Volley.newRequestQueue(context);

        Map<String, String> datos = new HashMap<>();
        datos.put("NotificationId","123");
        datos.put("Date",dateANDhour.toString());
        datos.put("Duration","no-put");

        final String url ="http://proyectos.tekus.co/Test/api/notifications/";
        // Inicio de Volley utilizando un Json como objeto para hacer el post,  tambien se podia hacer con un objeto plano string request
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,                        //  metodo a utilizar ( post)
                url,                                        // direccion de la pagina
                new JSONObject(datos),                      // parametros o datos a enviar
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {  //  recibimos el dato que devuelve el post

                        // Manejo de la respuesta

                        String Notification_temporal = response.optString("NotificationId");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        sharedPreferences.edit().putString(Preferencias.NOTIFICATION_TEMPORAL,Notification_temporal).apply();
                        Log.d(LOG_NOTI,Notification_temporal);
                        Toast.makeText(context,"hola",Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() { //  escuchador de errores

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores
                        Toast.makeText(context,"Notificacion error",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Basic 1084733503");
                headers.put("Content-Type","application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsArrayRequest);
    }

    public void Put_movimiento (final Context context,String dateANDhour,String durationFinal ){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String NotificacionID_actualizar = sharedPreferences.getString(Preferencias.NOTIFICATION_TEMPORAL,"123");
        Log.d(LOG_NOTI,NotificacionID_actualizar);
        RequestQueue queue = Volley.newRequestQueue(context);

        //  mapeamos los datos a eviar al post con el metodo volley
        HashMap<String, String> parametros2 =new HashMap<String, String>();
        parametros2.put("NotificationId",NotificacionID_actualizar);
        parametros2.put("Date",dateANDhour);
        parametros2.put("Duration",durationFinal);


        Log.d("DATOS",parametros2.toString());

        String url ="http://proyectos.tekus.co/Test/api/notifications/"+NotificacionID_actualizar;

        // Inicio de Volley utilizando un Json como objeto para hacer el post,  tambien se podia hacer con un objeto plano string request
        JsonObjectRequest jsArrayRequestput = new JsonObjectRequest(
                Request.Method.PUT,  //  metodo a utilizar ( post)
                url,  // direccion de la pagina
                new JSONObject(parametros2), // parametros o datos a enviar
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {  //  recibimos el dato que devuelve el post

                        // Manejo de la respuesta
                        Toast.makeText(context,"excelente",Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() { //  escuchador de errores


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String hola = "";// Manejo de errores

                        Toast.makeText(context,"grave",Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Basic 1084733503");
                headers.put("Content-Type","application/json; charset=utf-8");
                return headers;
            }
        };


        queue.add(jsArrayRequestput);


    }

    public void Get_alarmas_all (final Context context){

        //  obtenemos el content resolver
        ContentResolver contentResolver = context.getContentResolver();

        // eliminamos todos los registros de almacen
        contentResolver.delete(Contrato.Tekus_Alarmas.URI_CONTENIDO,null,null);


        RequestQueue queue = Volley.newRequestQueue(context);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url ="http://proyectos.tekus.co/Test/api/notifications/";

        final JsonArrayRequest jsonarrayget = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject row = response.optJSONObject(i);

                            try {
                                NOTIFICATIONID = row.getInt("NotificationId");
                                DATE = row.getString("Date");
                                DURATION = row.getString("Duration");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*Toast.makeText(context,"Id: "+NOTIFICATIONID +"\n"+"fecha: "+DATE+"\n"+"duracion"+DURATION,
                                    Toast.LENGTH_LONG).show();*/


                            insertar_registros_Categorias(context);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            //  sobreescribimos los headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Authorization","Basic 1084733503");
                headers.put("Content-Type","application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(jsonarrayget);
    }
    public void Delete_alarma (final Context context, final String NotificacionID){

        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="http://proyectos.tekus.co/Test/api/notifications/"+NotificacionID;

        // Inicio de Volley utilizando un Json como objeto para hacer el post,  tambien se podia hacer con un objeto plano string request
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.DELETE,  //  metodo a utilizar ( post)
                url,  // direccion de la pagina
                null, // parametros o datos a enviar
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {  //  recibimos el dato que devuelve el post



                    }
                },
                new Response.ErrorListener() { //  escuchador de errores

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String hola = "";// Manejo de errores

                    }
                }){

            //  sobreescribimos los headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Authorization","Basic 1084733503");
                headers.put("Content-Type","application/json; charset=utf-8");
                return headers;
            }
        };


        queue.add(jsArrayRequest);

    }

    private void insertar_registros_Categorias(Context context) {

        // el content provider genera todos los cambios en un content resolver
        ContentResolver contentResolver = context.getContentResolver();

        // creamos un contenedor de valores nuevos
        ContentValues values = new ContentValues();
        values.put(Contrato.Tekus_Alarmas.NOTIFICATIONID,NOTIFICATIONID);
        values.put(Contrato.Tekus_Alarmas.DATE,DATE);
        values.put(Contrato.Tekus_Alarmas.DURATION,DURATION);
        values.put(Contrato.Tekus_Alarmas.INFORMACION,"");

        // insertamos la nueva fila con sus nuevos valores
        contentResolver.insert(Uri.parse(String.valueOf(Contrato.Tekus_Alarmas.URI_CONTENIDO)),values);

    }


}