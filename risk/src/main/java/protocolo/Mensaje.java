package protocolo;

import servidor.Sala;
import java.util.List;

public class Mensaje {
    public static String listaSalas(List<Sala> salas){
        String mensaje = Protocolo.LISTA_SALAS;

        for (Sala sala : salas) {
            mensaje += Protocolo.DEL;
            mensaje += sala.getNombre();
            mensaje += Protocolo.DEL;
            mensaje += sala.getNumJugadores();
            mensaje += Protocolo.DEL;
            mensaje +=sala.getCapacidad();
        }

        return mensaje;
    }

    public static String nuevaSala(String nombre, int capacidad){
        String mensaje = Protocolo.CREAR_SALA;
        mensaje+=Protocolo.DEL;
        mensaje+=nombre;
        mensaje+=Protocolo.DEL;
        mensaje+=capacidad;
        return mensaje;
    }

    public static String salaCreada(boolean creada){
        if(creada){
            return Protocolo.CREAR_SALA+Protocolo.DEL+Protocolo.OK;
        }else{
            return Protocolo.CREAR_SALA+Protocolo.DEL+Protocolo.ERROR;
        }
    }
    
}
