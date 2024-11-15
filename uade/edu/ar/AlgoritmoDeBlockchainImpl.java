package uade.edu.ar;

import uade.edu.progra3.AlgoritmoDeBlockchain;
import uade.edu.progra3.model.Bloque;
import uade.edu.progra3.model.Transaccion;

import java.util.ArrayList;
import java.util.List;
/**
 * @author AlejandroFoglino
 */
public class AlgoritmoDeBlockchainImpl implements AlgoritmoDeBlockchain {
    @Override
    public List<List<Bloque>> construirBlockchain(List<Transaccion> transacciones,
                                                  int maxTamanioBloque,
                                                  int maxValorBloque,
                                                  int maxTransacciones,
                                                  int maxBloques) {
        List<List<Bloque>> solucion = new ArrayList<>();
        List<Bloque> blockchain = new ArrayList<>();
        backtrackingBlockchain(transacciones, 0, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);
        return solucion;
    }

    private void backtrackingBlockchain(List<Transaccion> transacciones,
                                        int indice,
                                        List<List<Bloque>> solucion,
                                        List<Bloque> blockchain,
                                        int maxTamanioBloque,
                                        int maxValorBloque,
                                        int maxTransacciones) {
        if (indice == transacciones.size()) {
            solucion.add(blockchain);
            return;
        }

        for (int i = indice; i < transacciones.size(); i++) {
            Transaccion transaccion = transacciones.get(i);
            if (!blockchain.isEmpty()) {
                for (int j = 0; j < blockchain.size(); j++) {
                    Bloque bloqueBlockchain = blockchain.get(j);
                    if (transaccionValidaEnBloque(blockchain, bloqueBlockchain, transaccion, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
                        //agregamos la transaccion al bloque
                        List<Transaccion> transaccionesDeBloque = bloqueBlockchain.getTransacciones();
                        transaccionesDeBloque.add(transaccion);
                        bloqueBlockchain.setTamanioTotal(bloqueBlockchain.getTamanioTotal() + transaccion.getTamanio());
                        bloqueBlockchain.setValorTotal(bloqueBlockchain.getValorTotal() + transaccion.getValor());
                        bloqueBlockchain.setTransacciones(transaccionesDeBloque);
                        //agregamos el bloque a la transaccion y llamamos recursivamente
                        blockchain.add(bloqueBlockchain);
                        backtrackingBlockchain(transacciones, i + 1, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);
                        //removemos la transaccion del bloque
                        blockchain.remove(bloqueBlockchain);
                        //removemos los datos del bloque que agregamos
                        bloqueBlockchain.setTamanioTotal(bloqueBlockchain.getTamanioTotal() - transaccion.getTamanio());
                        bloqueBlockchain.setValorTotal(bloqueBlockchain.getValorTotal() - transaccion.getValor());
                        transaccionesDeBloque.remove(transaccion);
                    }
                }
            }
        }
        //creamos un nuevo bloque
        Bloque nuevoBloque = new Bloque();
        Transaccion transaccion = transacciones.get(indice);
        if (transaccionValidaEnBloque(blockchain, nuevoBloque, transaccion, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
            //agregamos la transaccion al nuevo bloque
            List<Transaccion> transaccionesDeBloqueVacia = new ArrayList<>();
            transaccionesDeBloqueVacia.add(transaccion);
            nuevoBloque.setTamanioTotal(transaccion.getTamanio());
            nuevoBloque.setValorTotal(transaccion.getValor());
            nuevoBloque.setTransacciones(transaccionesDeBloqueVacia);
            blockchain.add(nuevoBloque);

            backtrackingBlockchain(transacciones, indice + 1, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);

            blockchain.remove(nuevoBloque);
        }

    }
    public static boolean transaccionValidaEnBloque(List<Bloque> BC,
                                                    Bloque B,
                                                    Transaccion Tr,
                                                    int maxTamanioB,
                                                    int maxValorB,
                                                    int maxTransaccionesPorB) {
        // Verificar el tamaño del bloque
        if (B.getTamanioTotal() + Tr.getTamanio() > maxTamanioB) {
            return false;
        }

        // Verificar el valor total del bloque
        if (B.getValorTotal() + Tr.getValor() > maxValorB) {
            return false;
        }

        // Verificar la cantidad de transacciones en el bloque
        if (B.getTransacciones().size() + 1 > maxTransaccionesPorB) {
            return false;
        }

        // Verificar si el valor total del bloque más el de la transacción es múltiplo de 10
        if ((B.getValorTotal() + Tr.getValor()) % 10 != 0) {
            return false;
        }

        // Verificar dependencia
        if (Tr.getDependencia() != null) {
            boolean encontrado = false;

            for (Bloque bloque : BC) {
                for (Transaccion transaccion : bloque.getTransacciones()) {
                    if (transaccion.equals(Tr.getDependencia())) {
                        encontrado = true;
                        break;
                    }
                }
            }

            if (!encontrado) {
                return false;
            }
        }
        //verificar firmas
        return Tr.isFirmada();
    }

}
