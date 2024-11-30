package servidor;

import java.net.InetAddress;
import java.util.jar.Attributes.Name;

public class Jugador {
    private String nombre;
    private InetAddress ip;

    public Jugador(String nombre, InetAddress ip){
        this.ip = ip;
        this.nombre=nombre;
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
}