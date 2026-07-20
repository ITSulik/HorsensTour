package library.resep1.Model.Collections;
import java.nio.file.Path;
import library.resep1.Model.Entities.Chauffeur;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChauffeurListTest {
  @TempDir
  Path tempDir;

  @Test
  void addChauffeurShouldStoreChauffeur() {
    ChauffeurList chauffeurList = new ChauffeurList(
        tempDir.resolve("chauffeurs.json").toString()
    );

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
