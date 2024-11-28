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
            if (validarBlockchain(blockchain, transacciones)) {
                solucion.add(new ArrayList<>(copiarBlockchain(blockchain)));
                return;
            } else {
                return;
            }
        }

        if (!blockchain.isEmpty()) {
            for (int j = 0; j < blockchain.size(); j++) {
                Bloque bloqueBlockchain = blockchain.get(j);
                for (int i = indice; i < transacciones.size(); i++) {
                    Transaccion transaccion = transacciones.get(i);
                    if (transaccionValidaEnBloque(blockchain, bloqueBlockchain, transaccion, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
                        //agregamos transacción al bloque
                        List<Transaccion> transaccionesDeBloque = bloqueBlockchain.getTransacciones();
                        transaccionesDeBloque.add(transaccion);
                        bloqueBlockchain.setTamanioTotal(bloqueBlockchain.getTamanioTotal() + transaccion.getTamanio());
                        bloqueBlockchain.setValorTotal(bloqueBlockchain.getValorTotal() + transaccion.getValor());
                        bloqueBlockchain.setTransacciones(transaccionesDeBloque);

                        backtrackingBlockchain(transacciones, i + 1, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);

                        //Deshacemos cambios
                        bloqueBlockchain.setTamanioTotal(bloqueBlockchain.getTamanioTotal() - transaccion.getTamanio());
                        bloqueBlockchain.setValorTotal(bloqueBlockchain.getValorTotal() - transaccion.getValor());
                        transaccionesDeBloque.remove(transaccion);
                        bloqueBlockchain.setTransacciones(transaccionesDeBloque);
                    }
                }
            }
        }
        Bloque nuevoBloque = new Bloque();

        Transaccion transaccion2 = transacciones.get(indice);

        if (transaccionValidaEnBloque(blockchain, nuevoBloque, transaccion2, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
            //creamos nuevo bloque
            List<Transaccion> transaccionesDeBloqueVacia = new ArrayList<>(nuevoBloque.getTransacciones());
            transaccionesDeBloqueVacia.add(transaccion2);
            nuevoBloque.setTamanioTotal(transaccion2.getTamanio());
            nuevoBloque.setValorTotal(transaccion2.getValor());
            nuevoBloque.setTransacciones(transaccionesDeBloqueVacia);
            blockchain.add(nuevoBloque);

            backtrackingBlockchain(transacciones, indice + 1, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);

            //deshacemos bloque
            nuevoBloque.setTamanioTotal(nuevoBloque.getTamanioTotal() - transaccion2.getTamanio());
            nuevoBloque.setValorTotal(nuevoBloque.getValorTotal() - transaccion2.getValor());
            nuevoBloque.setTransacciones(new ArrayList<>());
            blockchain.remove(nuevoBloque);
        }

    }

    public static boolean transaccionValidaEnBloque(List<Bloque> BC,
                                                    Bloque B,
                                                    Transaccion Tr,
                                                    int maxTamanioB,
                                                    int maxValorB,
                                                    int maxTransaccionesPorB) {

        if (B.getTamanioTotal() + Tr.getTamanio() > maxTamanioB) {
            return false;
        }

        for (Bloque bloque : BC) {
            for (Transaccion transaccion : bloque.getTransacciones()) {
                if (transaccion.equals(Tr)) {
                    return false;
                }
            }
        }
        for (Transaccion t : B.getTransacciones()) {
            if (t.equals(Tr)) {
                return false;
            }
        }

        if (B.getValorTotal() + Tr.getValor() > maxValorB) {
            return false;
        }

        if (B.getTransacciones().size() + 1 > maxTransaccionesPorB) {
            return false;
        }

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

        if (!Tr.isFirmada()) {
            return false;
        }

        return true;
    }

    private List<Bloque> copiarBlockchain(List<Bloque> blockchain) {
        List<Bloque> copia = new ArrayList<>();
        for (Bloque bloque : blockchain) {
            Bloque nuevoBloque = new Bloque();
            nuevoBloque.setTamanioTotal(bloque.getTamanioTotal());
            nuevoBloque.setValorTotal(bloque.getValorTotal());
            List<Transaccion> transaccionesCopia = new ArrayList<>();
            for (Transaccion transaccion : bloque.getTransacciones()) {
                Transaccion transaccionCopia = new Transaccion(transaccion.getTamanio(),
                                                                transaccion.getValor(),
                                                                transaccion.getDependencia(),
                                                                transaccion.getFirmasRequeridas() );
                transaccionesCopia.add(transaccionCopia);
            }
            nuevoBloque.setTransacciones(transaccionesCopia);
            copia.add(nuevoBloque);
        }
        return copia;
    }

    private boolean validarBlockchain(List<Bloque> blockchain, List<Transaccion> transacciones) {
        boolean flag = true;
        int contTransacciones = 0;

        //prueba de trabajo
        for (Bloque bloque : blockchain) {
            if (bloque.getValorTotal() % 10 != 0) {
                flag = false;
            }
            contTransacciones += bloque.getTransacciones().size();
        }

        //verificamos tamaño de bloque
            if (contTransacciones < transacciones.size()) {
            flag = false;
        }
        return flag;
    }
}