package save;

public class GameSaveData {

    // Dados de Posição e Estado do Jogador
    public float posX;
    public float posY;
    public float oxigenio;
    public float energia;
    public float tempoVivo;
    public String fase;

    // Progresso e Estatísticas
    public int oxigenioColetado;
    public int comidaColetada;
    public boolean abrigoDescoberto;

    // Controle de Versão do Save (útil para migração de dados no futuro)
    public int versao = 1;

    /**
     * Construtor padrão obrigatório para frameworks de serialização JSON.
     */
    public GameSaveData() {
    }

    /**
     * Construtor rápido para salvar estado inicial/básico.
     */
    public GameSaveData(float posX, float posY, float oxigenio, float energia, float tempoVivo, String fase) {
        this.posX = posX;
        this.posY = posY;
        this.oxigenio = oxigenio;
        this.energia = energia;
        this.tempoVivo = tempoVivo;
        this.fase = fase;
    }

    /**
     * Construtor completo.
     */
    public GameSaveData(float posX, float posY, float oxigenio, float energia, float tempoVivo,
                        String fase, int oxigenioColetado, int comidaColetada,
                        boolean abrigoDescoberto, int versao) {
        this(posX, posY, oxigenio, energia, tempoVivo, fase);
        this.oxigenioColetado = oxigenioColetado;
        this.comidaColetada = comidaColetada;
        this.abrigoDescoberto = abrigoDescoberto;
        this.versao = versao;
    }

    @Override
    public String toString() {
        return "GameSaveData{" +
            "posX=" + posX +
            ", posY=" + posY +
            ", oxigenio=" + oxigenio +
            ", energia=" + energia +
            ", tempoVivo=" + tempoVivo +
            ", fase='" + fase + '\'' +
            ", oxigenioColetado=" + oxigenioColetado +
            ", comidaColetada=" + comidaColetada +
            ", abrigoDescoberto=" + abrigoDescoberto +
            ", versao=" + versao +
            '}';
    }
}
