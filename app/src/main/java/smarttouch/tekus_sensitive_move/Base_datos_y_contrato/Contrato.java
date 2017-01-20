package smarttouch.tekus_sensitive_move.Base_datos_y_contrato;

import android.net.Uri;

/**
 * Created by ASUS on 17/01/2017.
 */

public class Contrato {

    interface ColumnasAlarmas {

        String NOTIFICATIONID = "notificationid";
        String DATE = "date";
        String DURATION = "duration";
        String INFORMACION = "informacion";

    }

    // Autoridad del Content Provider
    public final static String AUTORIDAD = "smarttouch.tekus_sensitive_move";

    // Uri base
    public final static Uri URI_CONTENIDO_BASE = Uri.parse("content://" + AUTORIDAD);

    // String de Almacen
    public static class Tekus_Alarmas implements ColumnasAlarmas {

        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_ALARMAS).build();

        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_ALARMAS;

        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_ALARMAS;


        /**
         * Construye una {@link Uri} para el {@link #} solicitado.
         */
        public static Uri construirUriAlarmas(String idAlarmas) {
            return URI_CONTENIDO.buildUpon().appendPath(idAlarmas).build();
        }

        /*public static String generarIdAlarmas() {
            return "A-" + UUID.randomUUID();
        }*/

        public static String obtenerIdAlarmas(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    // fin de string alarmas

    // Recursosp
    public final static String RECURSO_ALARMAS = "alarmas";


}
