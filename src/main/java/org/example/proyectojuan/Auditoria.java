package org.example.proyectojuan;

public class Auditoria {
    private String accion;
    private String usuario;

    public Auditoria(String accion, String usuario) {
        this.accion = accion;
        this.usuario = usuario;
    }

    public String getAccion() { return accion; }
    public String getUsuario() { return usuario; }
}
