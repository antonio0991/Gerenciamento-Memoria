public class Main {
    public static void main(String[] args) {

        //Todos os valores serão false
        Memoria.zerarMemoria();
        Processo processo;
        int numProcessosRemovidos = Utils.getRandomNumber(1, 2);

        try {
            for (int i = 0; i < 100; i++) {
                //Opcional pra ver o processo funcionando
                //Thread.sleep(500);
                processo = GeradorDeProcessos.getNovoProcesso();
                //Memoria.alocarProcessoFirstFit(processo);
                Memoria.alocarProcessoNextFit(processo);
                //Memoria.alocarProcessoBestFit(processo);
                //Memoria.alocarProcessoWorstFit(processo);

                //A cada 1 segundo, remover 1 ou 2 processos
                if (i % 2 == 0 && i > 0) {
                    for (int j = 0; j < numProcessosRemovidos; j++) {
                        Memoria.removerProcessoAleatorio();
                    }
                }

                System.out.println("\nNOVO PROCESSO!\nTamanho: " + processo.getTamAlocacao() + '\n' + "Memória: " + Memoria.printMemoria());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
