package library.resep1.Model.Collections;
import library.resep1.Model.Entities.Chauffeur;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChauffeurListTest {

  @Test
  void addChauffeurShouldStoreChauffeur() {
    ChauffeurList chauffeurList = new ChauffeurList();

    Chauffeur chauffeur = new Chauffeur(
        "Mateo",
        5,
        "Long distance trips");

    chauffeurList.addChauffeur(chauffeur);
    assertEquals(1, chauffeurList.getAllChauffeurs().size());
    assertEquals(
        "Mateo",
        chauffeurList.getAllChauffeurs().get(0).getName()
    );}
}