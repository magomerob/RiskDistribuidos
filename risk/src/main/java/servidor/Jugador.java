package servidor;

import java.net.InetAddress;

public class Jugador {
    private String nombre;
    private InetAddress ip;
    private boolean listo;

    public Jugador(String nombre, InetAddress ip){
        this.ip = ip;
        this.nombre=nombre;
        this.listo = false;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIp(InetAddress ip){
        this.ip=ip;
    }

    public InetAddress getIp(){
        return this.ip;
    }

    public boolean isListo(){
        return listo;
    }

    public void setListo(boolean listo){
        this.listo=listo;
    }
}