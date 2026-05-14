package org.example.proyectojuan;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Auditoria {
    private String accion;
    private String usuario;
    private com.google.cloud.Timestamp fecha; // Campo para Firebase

    public Auditoria() {

    }

    public Auditoria(String accion, String usuario, com.google.cloud.Timestamp fecha) {
        this.accion = accion;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public String getAccion() { return accion; }
    public String getUsuario() { return usuario; }
    public com.google.cloud.Timestamp getFecha() { return fecha; }

    // Método auxiliar para mostrar la fecha bonita en la tabla
    public String getFechaFormateada() {
        if (fecha == null) return "";
        Date date = fecha.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }
}