package library.resep1.Model.Collections;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import library.resep1.Model.Entities.Chauffeur;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChauffeurList {

  private static final String FILE_NAME = "chauffeurs.json";
  private final Gson gson;
  private final List<Chauffeur> chauffeurs;

  public ChauffeurList() {
    gson = new Gson();
    chauffeurs = loadChauffeurs();}

  public void addChauffeur(Chauffeur chauffeur) {
    chauffeurs.add(chauffeur);
    saveChauffeurs();}

  public List<Chauffeur> getAllChauffeurs() {
    return new ArrayList<>(chauffeurs);
  }

  private void saveChauffeurs() {
    try (FileWriter writer = new FileWriter(FILE_NAME)) {
      gson.toJson(chauffeurs, writer);
    } catch (IOException e) {
      throw new RuntimeException(e);}
  }

  private List<Chauffeur> loadChauffeurs() {
    try (FileReader reader = new FileReader(FILE_NAME)) {
      Type type = new TypeToken<ArrayList<Chauffeur>>() {}.getType();
      List<Chauffeur> loaded = gson.fromJson(reader, type);
      return loaded == null ? new ArrayList<>() : loaded;
    } catch (IOException e) {
      return new ArrayList<>();}
  }
}