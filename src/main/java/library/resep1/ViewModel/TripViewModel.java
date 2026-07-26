package library.resep1.ViewModel;

import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Collections.TripList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Entities.Trip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TripViewModel {

  private final TripList tripList;

  private static final TripViewModel INSTANCE =
          new TripViewModel();

  private TripViewModel() {
    tripList = new TripList();
  }

  public static TripViewModel getInstance() {
    return INSTANCE;
  }
  public void addTrip(
          String destination,
          LocalDateTime startTime,
          LocalDateTime endTime,
          String additionalDetails,
          String busNumber,
          String chauffeurName
  ) {
    String tripId = UUID.randomUUID().toString();

    Trip trip = new Trip(
            tripId,
            destination,
            startTime,
            endTime,
            additionalDetails,
            busNumber,
            chauffeurName
    );

    tripList.addTrip(trip);
  }

  public boolean editTrip(
          String tripId,
          String destination,
          LocalDateTime startTime,
          LocalDateTime endTime,
          String additionalDetails,
          String busNumber,
          String chauffeurName
  ) {
    Trip trip = tripList.getTripById(tripId);

    if (trip == null) {
      return false;
    }

    trip.setBusNumber(busNumber);
    trip.setChauffeurName(chauffeurName);

    return tripList.editTrip(
            tripId,
            destination,
            startTime,
            endTime,
            additionalDetails
    );
  }

  public boolean deleteTrip(String tripId) {
    return tripList.deleteTrip(tripId);
  }

  public Trip getTripById(String tripId) {
    return tripList.getTripById(tripId);
  }

  public List<Trip> getAllTrips() {
    return tripList.getAllTrips();
  }

  public List<Bus> getAvailableBuses(
          LocalDateTime startTime,
          LocalDateTime endTime,
          List<Bus> allBuses
  ) {
    return tripList.getAvailableBuses(
            startTime,
            endTime,
            allBuses
    );
  }

  public List<Chauffeur> getAvailableChauffeurs(
          LocalDateTime startTime,
          LocalDateTime endTime,
          List<Chauffeur> allChauffeurs
  ) {
    return tripList.getAvailableChauffeurs(
            startTime,
            endTime,
            allChauffeurs
    );
  }

  public boolean hasBusConflict(
          String busNumber,
          LocalDateTime startTime,
          LocalDateTime endTime,
          String excludeTripId
  ) {
    if (busNumber == null || busNumber.isBlank()) {
      return false;
    }

    for (Trip trip : tripList.getAllTrips()) {

      if (excludeTripId != null
              && trip.getTripId().equals(excludeTripId)) {
        continue;
      }

      if (trip.hasBusAssigned()
              && trip.getBusNumber().equalsIgnoreCase(busNumber)
              && trip.overlaps(startTime, endTime)) {

        return true;
      }
    }

    return false;
  }

  public boolean hasChauffeurConflict(
          String chauffeurName,
          LocalDateTime startTime,
          LocalDateTime endTime,
          String excludeTripId
  ) {
    if (chauffeurName == null || chauffeurName.isBlank()) {
      return false;
    }

    for (Trip trip : tripList.getAllTrips()) {

      if (excludeTripId != null
              && trip.getTripId().equals(excludeTripId)) {
        continue;
      }

      if (trip.hasChauffeurAssigned()
              && trip.getChauffeurName()
              .equalsIgnoreCase(chauffeurName)
              && trip.overlaps(startTime, endTime)) {

        return true;
      }
    }

    return false;
  }
}