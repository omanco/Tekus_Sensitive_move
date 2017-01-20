package smarttouch.tekus_sensitive_move;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServicioAcelerometro extends IntentService implements SensorEventListener{

    private static final String VALOR = "VALOR";

    private static final String TAG = ServicioAcelerometro.class.getSimpleName();  //  para usar el log
    // Start with some variables
    private SensorManager sensorMan;
    private Sensor accelerometer;

    private float[] mGravity;

    double acel_actual_x;
    double acel_actual_y;
    double acel_actual_z;
    double acel_anterior_x;
    double acel_anterior_y;
    double acel_anterior_z;

    double vel_actual_x;
    double vel_actual_y;
    double vel_actual_z;
    double vel_anterior_x;
    double vel_anterior_y;
    double vel_anterior_z;

    double pos_actual_x;
    double pos_actual_y;
    double pos_actual_z;
    double pos_anterior_x;
    double pos_anterior_y;
    double pos_anterior_z;

    long timestamp_actual;
    long timestamp_anterior;

    float[] gravity=new float[3];
    boolean primerDato=false,stopEnvioPost = false;

    long tiempoInicio, tiempoFinal;
    double deltaPosicion, posicionAnterior,posicionActual;
    double deltaTiempoMovimiento;

    boolean EnMoviemiento = false;

    static final double THRESHOLD_ACCELERATION = 0.07;
    static final double THRESHOLD_MOVIMIENTO = 0.005;





    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "smarttouch.tekus_sensitive_move.action.FOO";

    public ServicioAcelerometro() {
        super("ServicioAcelerometro");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, ServicioAcelerometro.class);
        intent.setAction(ACTION_FOO);

        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {

                handleActionFoo();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        // TODO: Handle action Foo
        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorMan.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_UI);
//        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();


            if(!primerDato)
            {
                timestamp_anterior=event.timestamp;
                gravity[0] = mGravity[0];
                gravity[1] = mGravity[1];
                gravity[2] = mGravity[2];
                acel_actual_x=acel_actual_y=acel_actual_z=0;
                acel_anterior_x=acel_anterior_y=acel_anterior_z=0;
                vel_actual_x=vel_actual_y=vel_actual_z=0;
                vel_anterior_x=vel_anterior_y=vel_anterior_z=0;
                pos_actual_x=pos_actual_y=pos_actual_z=0;
                pos_anterior_x=pos_anterior_y=pos_anterior_z=0;
                primerDato=true;
            }
            else {
                // alpha is calculated as t / (t + dT)
                // with t, the low-pass filter's time-constant
                // and dT, the event delivery rate
                timestamp_actual = event.timestamp;

                final float alpha = 0.8f;


                gravity[0] = alpha * gravity[0] + (1 - alpha) * mGravity[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * mGravity[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * mGravity[2];

                //Calculamos la aceleracion lineal
                acel_actual_x = event.values[0] - gravity[0];
                acel_actual_y = event.values[1] - gravity[1];
                acel_actual_z = event.values[2] - gravity[2];

                //rango para eliminar ruido mecanico
                if (acel_actual_x > -THRESHOLD_ACCELERATION && acel_actual_x < THRESHOLD_ACCELERATION) {
                    acel_actual_x = 0;
                }
                if (acel_actual_y > -THRESHOLD_ACCELERATION && acel_actual_y < THRESHOLD_ACCELERATION) {
                    acel_actual_y = 0;
                }
                if (acel_actual_z > -THRESHOLD_ACCELERATION && acel_actual_z < THRESHOLD_ACCELERATION) {
                    acel_actual_z = 0;
                }

                //Deteccion de fin de movimiento
                if (acel_actual_x == 0) {
                    vel_actual_x = vel_anterior_x = 0;
                }
                if (acel_actual_y == 0) {
                    vel_actual_y = vel_anterior_y = 0;
                }
                if (acel_actual_z == 0) {
                    vel_actual_z = vel_anterior_z = 0;
                }

                double deltaTiempo = (double) (timestamp_actual - timestamp_anterior) / 1000000000;
                //Calculamos velocidad actual (integracion numerica regla del trapecio)
                vel_actual_x = vel_anterior_x + (acel_actual_x + acel_anterior_x) *deltaTiempo / 2;
                vel_actual_y = vel_anterior_y + (acel_actual_y + acel_anterior_y) *deltaTiempo / 2;
                vel_actual_z = vel_anterior_z + (acel_actual_z + acel_anterior_z) *deltaTiempo / 2;


                //Calculamos posicion actual
                pos_actual_x = pos_anterior_x + (vel_actual_x + vel_anterior_x) *deltaTiempo / 2;
                pos_actual_y = pos_anterior_y + (vel_actual_y + vel_anterior_y) *deltaTiempo / 2;
                pos_actual_z = pos_anterior_z + (vel_actual_z + vel_anterior_z) *deltaTiempo / 2;



                posicionAnterior = posicionActual;
                //Calculamos distancia euclidea
                double deltaX=pos_actual_x-pos_anterior_x;
                double deltaY=pos_actual_y-pos_anterior_y;
                double deltaZ=pos_actual_z-pos_anterior_z;

                deltaPosicion = Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ);

                if(deltaPosicion>THRESHOLD_MOVIMIENTO){
                    if(!EnMoviemiento){
                        EnMoviemiento = true;
                        tiempoInicio= timestamp_actual;
//                        salida10.setText("Tiempo inicio:"+tiempoInicio);
                        Log.d(TAG,"Tiempo inicio:"+tiempoInicio);
                    }
                    deltaTiempoMovimiento = (double) ((timestamp_actual-tiempoInicio)/1000000);
                    if(deltaTiempoMovimiento>=2000  && !stopEnvioPost)
                    {
                        Log.d(TAG,"Tiempo delta:"+deltaTiempoMovimiento);
                        //Enviar el post
                        Envio_mov_2Seg();
                        stopEnvioPost= true;     // Controla el envio de post ,  despues de un put
                    }

//                    salida12.setText("Tiempo delta:"+deltaTiempoMovimiento);

                }
                else {
                    if(EnMoviemiento){
                        tiempoFinal = timestamp_actual;
//                        salida11.setText("Tiempo final: "+tiempoFinal);
                        double deltaTiempoMovimientoFinal = (double) ((timestamp_actual-tiempoInicio)/1000000);

                        // Log.d(TAG,"Tiempo delta:"+deltaTiempoMovimiento);
                        if(deltaTiempoMovimientoFinal>deltaTiempoMovimiento && deltaTiempoMovimiento>=2000 && stopEnvioPost)
                        {
                            //Enviar el put
                            Envio_fin_mov ( deltaTiempoMovimientoFinal);
                            stopEnvioPost = false;
                        }

                    }
                    EnMoviemiento = false;
                }

                acel_anterior_x = acel_actual_x;
                acel_anterior_y = acel_actual_y;
                acel_anterior_z = acel_actual_z;
                vel_anterior_x = vel_actual_x;
                vel_anterior_y = vel_actual_y;
                vel_anterior_z = vel_actual_z;
                pos_anterior_x = pos_actual_x;
                pos_anterior_y = pos_actual_y;
                pos_anterior_z = pos_actual_z;

                timestamp_anterior = timestamp_actual;
            }
        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void Envio_mov_2Seg (){

        SimpleDateFormat FechaHoraFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateANDhour = FechaHoraFormat.format(new Date());
        Log.d(VALOR,dateANDhour);

        WebApi_Volley post_movimiento_2seg = new WebApi_Volley();
        post_movimiento_2seg.Post_movimiento(getApplicationContext(),dateANDhour);

    }

    private void Envio_fin_mov(double deltaTiempoMovimientoFinal) {

        int deltaTiempo = (int) (deltaTiempoMovimientoFinal/1000);

        String durationFinal = String.valueOf(deltaTiempo);

        SimpleDateFormat FechaHoraFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateANDhour = FechaHoraFormat.format(new Date());
        Log.d(VALOR,dateANDhour);

        WebApi_Volley put_movimiento_final = new WebApi_Volley();
        put_movimiento_final.Put_movimiento(getApplicationContext(),dateANDhour,durationFinal);
        Log.d(VALOR,durationFinal);

    }
}
