public class Main {
    public static void main(String[] args) {

        //Todos os valores serão false
        Memoria.zerarMemoria();
        Processo processo;
        int numProcessosRemovidos = Utils.getRandomNumber(1, 2);
        double tamanhoTotalProcessosGerados = 0;

        try {
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    //Opcional pra ver o processo funcionando
                    //Thread.sleep(500);
                    processo = GeradorDeProcessos.getNovoProcesso();
                    tamanhoTotalProcessosGerados += processo.getTamAlocacao();
                    //Memoria.alocarProcessoFirstFit(processo);
                    //Memoria.alocarProcessoNextFit(processo);
                    //Memoria.alocarProcessoBestFit(processo);
                    //Memoria.alocarProcessoWorstFit(processo);
                    System.out.println("\nNOVO PROCESSO!\nTamanho: " + processo.getTamAlocacao() + '\n' + "Memória: " + Memoria.printMemoria());

                    //A cada 1 segundo, remover 1 ou 2 processos
                    if (j % 2 == 0 && j > 0) {
                        for (int k = 0; k < numProcessosRemovidos; k++) {
                            Memoria.removerProcessoAleatorio();
                        }
                    }
                }
            }
            System.out.println("-------------RELATÓRIO--------------");
            System.out.println("Tamanho médio dos processos gerados: " + tamanhoTotalProcessosGerados/10000);
            System.out.println("Ocupação média da memória por segundo (2 ciclos): " + String.format("%.2f", Memoria.getTaxaOcupacao()) + "%");
            System.out.println("Taxa de descarte: " + String.format("%.2f", Memoria.getTaxaDescarte()) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
