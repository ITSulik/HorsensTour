package library.resep1.Model.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Entities.Trip;
import library.resep1.Model.Enums.BusType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TripListTest {
  @TempDir
  Path tempDir;

  @Test
  void addTripShouldStoreAndLoadTripFromJson() {
    String fileName = tempDir.resolve("trips.json").toString();
    TripList tripList = new TripList(fileName);

    tripList.addTrip(new Trip(
        "TRP-101",
        "Skagen",
        LocalDateTime.of(2026, 7, 12, 8, 0),
        LocalDateTime.of(2026, 7, 12, 19, 0),
        "Day tour"
    ));

    TripList reloadedTripList = new TripList(fileName);

    assertEquals(1, reloadedTripList.getAllTrips().size());
    assertEquals(
        "Skagen",
        reloadedTripList.getTripById("TRP-101").getDestination()
    );
  }

  @Test
  void editTripShouldUpdateTripDetails() {
    TripList tripList = new TripList(
        tempDir.resolve("trips.json").toString()
    );
    tripList.addTrip(sampleTrip());

    boolean edited = tripList.editTrip(
        "TRP-101",
        "Aarhus",
        LocalDateTime.of(2026, 7, 13, 9, 0),
        LocalDateTime.of(2026, 7, 13, 17, 0),
        "Updated details"
    );

    Trip editedTrip = tripList.getTripById("TRP-101");

    assertTrue(edited);
    assertEquals("Aarhus", editedTrip.getDestination());
    assertEquals(
        LocalDateTime.of(2026, 7, 13, 9, 0),
        editedTrip.getStartTime()
    );
    assertEquals("Updated details", editedTrip.getAdditionalDetails());
  }

  @Test
  void deleteTripShouldRemoveTrip() {
    TripList tripList = new TripList(
        tempDir.resolve("trips.json").toString()
    );
    tripList.addTrip(sampleTrip());

    boolean deleted = tripList.deleteTrip("TRP-101");

    assertTrue(deleted);
    assertNull(tripList.getTripById("TRP-101"));
    assertTrue(tripList.getAllTrips().isEmpty());
  }

  @Test
  void getTripByIdShouldReturnTripDetails() {
    TripList tripList = new TripList(
        tempDir.resolve("trips.json").toString()
    );
    tripList.addTrip(sampleTrip());

    Trip trip = tripList.getTripById("trp-101");

    assertNotNull(trip);
    assertEquals("TRP-101", trip.getTripId());
    assertEquals("Bus 01", trip.getBusNumber());
    assertEquals("Mette Holm", trip.getChauffeurName());
  }

  @Test
  void availabilityShouldExcludeResourcesAssignedToOverlappingTrips() {
    TripList tripList = new TripList(
        tempDir.resolve("trips.json").toString()
    );
    tripList.addTrip(sampleTrip());

    List<Bus> buses = List.of(
        new Bus("Bus 01", BusType.MINIBUS, 16, "City trips"),
        new Bus("Bus 02", BusType.TOURIST_BUS, 50, "Tourism")
    );
    List<Chauffeur> chauffeurs = List.of(
        new Chauffeur("Mette Holm", 6, "No night driving"),
        new Chauffeur("Jens Berg", 9, "International routes")
    );

    List<Bus> availableBuses = tripList.getAvailableBuses(
        LocalDateTime.of(2026, 7, 12, 9, 0),
        LocalDateTime.of(2026, 7, 12, 10, 0),
        buses
    );
    List<Chauffeur> availableChauffeurs = tripList.getAvailableChauffeurs(
        LocalDateTime.of(2026, 7, 12, 9, 0),
        LocalDateTime.of(2026, 7, 12, 10, 0),
        chauffeurs
    );

    assertEquals(1, availableBuses.size());
    assertEquals("Bus 02", availableBuses.get(0).getBusNumber());
    assertEquals(1, availableChauffeurs.size());
    assertEquals("Jens Berg", availableChauffeurs.get(0).getName());
  }

  @Test
  void availabilityShouldAllowResourcesAfterTripEnds() {
    TripList tripList = new TripList(
        tempDir.resolve("trips.json").toString()
    );
    tripList.addTrip(sampleTrip());

    List<Bus> buses = List.of(
        new Bus("Bus 01", BusType.MINIBUS, 16, "City trips")
    );

    List<Bus> availableBuses = tripList.getAvailableBuses(
        LocalDateTime.of(2026, 7, 12, 19, 0),
        LocalDateTime.of(2026, 7, 12, 20, 0),
        buses
    );

    assertEquals(1, availableBuses.size());
    assertEquals("Bus 01", availableBuses.get(0).getBusNumber());
  }

  @Test
  void tripShouldRejectInvalidTimeSlot() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Trip(
            "TRP-101",
            "Skagen",
            LocalDateTime.of(2026, 7, 12, 19, 0),
            LocalDateTime.of(2026, 7, 12, 8, 0),
            "Invalid"
        )
    );
  }

  @Test
  void deleteTripShouldReturnFalseWhenTripDoesNotExist() {
    TripList tripList = new TripList(
        tempDir.resolve("trips.json").toString()
    );

    assertFalse(tripList.deleteTrip("TRP-404"));
  }

  private Trip sampleTrip() {
    return new Trip(
        "TRP-101",
        "Skagen",
        LocalDateTime.of(2026, 7, 12, 8, 0),
        LocalDateTime.of(2026, 7, 12, 19, 0),
        "Day tour",
        "Bus 01",
        "Mette Holm"
    );
  }
}
