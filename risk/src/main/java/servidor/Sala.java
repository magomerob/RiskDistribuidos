package servidor;

import java.io.Serializable;

public class Sala implements Serializable{
    private String nombre;
    private int capacidad;
    private int jugadores;
    public Sala(String nombre, int capacidad ){
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.jugadores = 1;
    }

    public String getNombre(){
        return this.nombre;
    }
    public int getCapacidad(){
        return this.capacidad;
    }
    public int getJugadores(){
        return this.jugadores;
    }
}
