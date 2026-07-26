package library.resep1.ViewModel;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Collections.ChauffeurList;
import library.resep1.Model.Entities.Chauffeur;
import java.util.List;

public class ChauffeurViewModel {

  private final ChauffeurList chauffeurList;

  private static final ChauffeurViewModel INSTANCE =
          new ChauffeurViewModel();

  private ChauffeurViewModel() {
    chauffeurList = new ChauffeurList();
  }

  public static ChauffeurViewModel getInstance() {
    return INSTANCE;
  }
  public void addChauffeur(
      String name,
      int experience,
      String preferences
  ) {
    Chauffeur chauffeur =
        new Chauffeur(name, experience, preferences);

    chauffeurList.addChauffeur(chauffeur);}

  public boolean editChauffeur(
          String ID,
          String name,
          int experience,
          String preferences
  ) { return chauffeurList.editChauffeur(ID, name, experience, preferences);}

  public boolean deleteChauffeur(String ID) {
    return chauffeurList.deleteChauffeur(ID);
  }

  public List<Chauffeur> getAllChauffeurs() {
    return chauffeurList.getAllChauffeurs();}
}