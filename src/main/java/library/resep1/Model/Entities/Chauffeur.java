package library.resep1.Model.Entities;

public class Chauffeur {
  private String name;
  private int experience;
  private String preferences;

  public Chauffeur(String name, int experience, String preferences) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name is required.");}

    if (experience < 0) {
      throw new IllegalArgumentException(
          "Experience must be zero or a positive number."
      );}

    this.name = name;
    this.experience = experience;
    this.preferences = preferences == null ? "" : preferences;}
  public String getName() {
    return name;}

  public int getExperience() {
    return experience;}

  public String getPreferences() {
    return preferences;}
  // Added setters to support editing chauffeur records.

  public void setName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name is required.");
    }
    this.name = name;
  }

  public void setExperience(int experience) {
    if (experience < 0) {
      throw new IllegalArgumentException(
          "Experience must be zero or a positive number."
      );
    }
    this.experience = experience;
  }

  public void setPreferences(String preferences) {
    this.preferences = preferences == null ? "" : preferences;
  }
}
