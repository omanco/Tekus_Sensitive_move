package smarttouch.tekus_sensitive_move.Adaptadores;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import smarttouch.tekus_sensitive_move.Base_datos_y_contrato.Contrato;
import smarttouch.tekus_sensitive_move.R;
import smarttouch.tekus_sensitive_move.WebApi_Volley;

/**
 * Created by ASUS on 18/01/2017.
 */

public class Adapter_alarmas extends RecyclerView.Adapter<Adapter_alarmas.ViewHolder> {

    private final Context contexto;
    private Cursor items;
    public OnItemClickListener escuchaAlarmas;

    public interface OnItemClickListener {
        public void onClick(ViewHolder holder, String idAlarmas);
    }

    public Adapter_alarmas(Context contexto , OnItemClickListener escuchaAlarmas) {
        this.escuchaAlarmas = escuchaAlarmas;
        this.contexto = contexto;
    }

    @Override
    public Adapter_alarmas.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarmas,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_alarmas.ViewHolder holder, int position) {

        items.moveToPosition(position);
        String s;

        // asignacion de ui
        s= items.getString(ConsultaAlarma.INFORMACION);
        holder.Informacion.setText(s);

        s= items.getString(ConsultaAlarma.DATE);
        holder.Fecha.setText(s);

        s= items.getString(ConsultaAlarma.NOTIFICATIONID);
        holder.NotificationId.setText(s);

        s= items.getString(ConsultaAlarma.DURATION);
        holder.Duration.setText(s);

    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor nuevoCursor) {
        if (nuevoCursor != null) {
            items = nuevoCursor;
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() {
        return items;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView Informacion, Fecha ,Duration, NotificationId;
        private Button btnEliminar;
        public CardView cardView;
        public RelativeLayout linearLayout_button_menu;
        public Boolean flag_control = false;


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            Informacion = (TextView)itemView.findViewById(R.id.txtInformacion);
            Fecha = (TextView)itemView.findViewById(R.id.txtFechaAlarma);
            NotificationId = (TextView)itemView.findViewById(R.id.txtNotificationId) ;
            Duration = (TextView)itemView.findViewById(R.id.txtDuration) ;
            btnEliminar = (Button)itemView.findViewById(R.id.btnEliminarAlarma);
            cardView = (CardView)itemView.findViewById(R.id.card_desliza);
            linearLayout_button_menu = (RelativeLayout) itemView.findViewById(R.id.layout_button_menu);

            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentResolver contentResolver = contexto.getContentResolver();
                    Uri UriIdentificador = Contrato.Tekus_Alarmas.construirUriAlarmas(obtenerIdAlarmas(getAdapterPosition()));
                    contentResolver.delete(UriIdentificador,null,null);

                    WebApi_Volley Eliminar_alarma = new WebApi_Volley();
                    Eliminar_alarma.Delete_alarma(contexto,obtenerNOTIFICATIONID(getAdapterPosition()));
                }
            });


        }

        @Override
        public void onClick(View view) {

            escuchaAlarmas.onClick(this,obtenerIdAlarmas(getAdapterPosition()));

            // preguntamos por la bandera de control , saber si esta mostrando el menu editar y eliminar o no
            if (!flag_control){                                                       // si la bandera es false  ( NO se ha presionado boton)
                Animation desplaza = AnimationUtils.loadAnimation(itemView.getContext(),R.anim.deslizar); // identificamos la animacion desplzar a la  derecha
                flag_control = true;                                                // invertimos bandera, se presiono
                NotificationId.setText(items.getString(ConsultaAlarma.NOTIFICATIONID));
                desplaza.setFillAfter(true);                                       //accion para que la animacion no se restablezca
                cardView.setAnimation(desplaza);                            //iniciamos animacion  ir a la derecha cardview
                linearLayout_button_menu.setVisibility(View.VISIBLE);     // colocamo visibles los botones editar y eliminar
                linearLayout_button_menu.setClickable(true);            //  activamos que puedas darle click


            }
            else {                                                          //  si la bandera esta true,  boton esta presionado
                Animation desplaza_back = AnimationUtils.loadAnimation(itemView.getContext(),R.anim.deslizar_back);  // identificamos animacion ir a la izquierda
                Animation alpha_back = AnimationUtils.loadAnimation(itemView.getContext(),R.anim.alpha_back);  // identificamos animacion desaparcer
                linearLayout_button_menu.setClickable(false);                           //  desabilitamos que se pueda dar click
                linearLayout_button_menu.setAnimation(alpha_back);                      // iniciamos animacion desaparecer de botones editar y eliminar
                flag_control= false;                                                    //  colcoamos control en false ,
                cardView.setAnimation(desplaza_back);                                   //iniciamos animacion para ir atras del cardview
                linearLayout_button_menu.setVisibility(View.INVISIBLE);                 // colocamos invisible  los botones editar y eliminar
            }
        }
    }
    private String obtenerIdAlarmas(int adapterPosition) {
        if (items != null) {
            if (items.moveToPosition(adapterPosition)) {
                return items.getString(ConsultaAlarma.ID);
            }
        }

        return null;
    }

    private String obtenerNOTIFICATIONID(int adapterPosition) {
        if (items != null) {
            if (items.moveToPosition(adapterPosition)) {
                return items.getString(ConsultaAlarma.NOTIFICATIONID);
            }
        }

        return null;
    }

    interface ConsultaAlarma {
        int ID = 0;
        int NOTIFICATIONID = 1;
        int DATE = 2;
        int DURATION = 3;
        int INFORMACION = 4;

    }

}
