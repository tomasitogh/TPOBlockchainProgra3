package uade.edu.ar;

import uade.edu.ar.util.TransaccionUtils;
import uade.edu.progra3.AlgoritmoDeBlockchain;
import uade.edu.progra3.model.Transaccion;
import uade.edu.progra3.model.Bloque;

import java.util.List;

public class Main {
    public static void main(String[] args){

        AlgoritmoDeBlockchain algoritmoDeBlockchain = new AlgoritmoDeBlockchainImpl();
        List<Transaccion> transacciones = TransaccionUtils.crearTransaccionesSimples(12, 10, 10, 1);
        uade.edu.progra3.TransaccionUtils.firmarTransacciones(transacciones);

        List<List<Bloque>> soluciones = algoritmoDeBlockchain.construirBlockchain(transacciones, 18, 18, 2, 100);

        // Imprimir las soluciones encontradas
        System.out.println("Soluciones encontradas:" + soluciones.size());

        /*
        for (List<Bloque> bloques : soluciones) {
            System.out.println("Bloques en una solución:");
            for (Bloque bloque : bloques) {
                System.out.println("  Bloque:");
                System.out.println("    Tamaño Total: " + bloque.getTamanioTotal());
                System.out.println("    Valor Total: " + bloque.getValorTotal());
                System.out.println("    Transacciones:");
                for (Transaccion transaccion : bloque.getTransacciones()) {
                    System.out.println("      - " + transaccion);
                }
            }
        }
        */

    }
}

