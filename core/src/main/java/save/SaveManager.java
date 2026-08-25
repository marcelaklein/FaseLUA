package save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveManager {

    private static final String PREFS_NAME = "echoes_mars_save";
    private static final String KEY_HAS_SAVE = "has_save";
    private static final String KEY_SAVE_DATA = "save_data_json";
    private static final String KEY_LAST_SAVE_TIME = "last_save_time";
    private static final String KEY_VERSION = "save_version";

    // Formatador estático reutilizável para evitar alocação excessiva de GC
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private final Preferences prefs;
    private final Json json;

    public SaveManager() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        this.json.setTypeName(null);
    }

    public void save(GameSaveData data) {
        if (data == null) {
            Gdx.app.error("SaveManager", "Tentativa de salvar dados nulos ignorada.");
            return;
        }

        try {
            String jsonString = json.toJson(data);

            prefs.putBoolean(KEY_HAS_SAVE, true);
            prefs.putString(KEY_SAVE_DATA, jsonString);
            prefs.putLong(KEY_LAST_SAVE_TIME, System.currentTimeMillis());
            prefs.putInteger(KEY_VERSION, data.versao);
            prefs.flush();

            Gdx.app.log("SaveManager", "Jogo salvo com sucesso!");
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Erro ao salvar o jogo: " + e.getMessage(), e);
        }
    }

    public GameSaveData load() {
        if (!hasSave()) {
            Gdx.app.log("SaveManager", "Nenhum save encontrado.");
            return null;
        }

        try {
            String jsonString = prefs.getString(KEY_SAVE_DATA, null);
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return null;
            }

            GameSaveData data = json.fromJson(GameSaveData.class, jsonString);
            Gdx.app.log("SaveManager", "Save carregado com sucesso.");
            return data;
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Erro ao carregar save (JSON corrompido?): " + e.getMessage(), e);
            return null;
        }
    }

    public boolean hasSave() {
        return prefs.getBoolean(KEY_HAS_SAVE, false);
    }

    public String getLastSaveTime() {
        long time = prefs.getLong(KEY_LAST_SAVE_TIME, 0);
        if (time == 0) return "Nunca";

        synchronized (DATE_FORMATTER) {
            return DATE_FORMATTER.format(new Date(time));
        }
    }

    public void deleteSave() {
        prefs.clear();
        prefs.flush();
        Gdx.app.log("SaveManager", "Save apagado com sucesso.");
    }

    public GameSaveData createNewGameData() {
        return new GameSaveData(200f, 200f, 100f, 100f, 0f, "MARTE");
    }
}
