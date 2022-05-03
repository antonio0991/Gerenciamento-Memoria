public class Processo {

    private static int MIN_INSTRUCOES = 10;
    private static int MAX_INSTRUCOES = 50;


    public Processo(int id) {
        this.id = id;
        this.tamAlocacao = Utils.getRandomNumber(MIN_INSTRUCOES, MAX_INSTRUCOES);//Número aleatório entre 10 e 50
    }


    private int id;
    private int tamAlocacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTamAlocacao() {
        return tamAlocacao;
    }

}
