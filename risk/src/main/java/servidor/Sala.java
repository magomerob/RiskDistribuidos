package servidor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.print.PrintColor;

public class Sala implements Serializable{
    private int numJugadores;
    private String nombre;
    private int capacidad;
    private List<Socket> jugadores = new ArrayList<Socket>();
    public Sala(String nombre, int capacidad){
        this.nombre = nombre;
        this.capacidad = capacidad;
    }

    public String getNombre(){
        return this.nombre;
    }
    public int getCapacidad(){
        return this.capacidad;
    }
    public List<Socket> getJugadores(){
        return this.jugadores;
    }

    public void addJugador(Socket j){
        this.jugadores.add(j);
    }

    public void remJugador(Socket j){
        this.jugadores.remove(j);
    }

    public void setNumJugadores(int i){
        this.numJugadores = i;
    }

    public int getNumJugadores(){
        if(this.jugadores.isEmpty()){
            return this.numJugadores;
        }
        return this.jugadores.size();
    }
    
}
