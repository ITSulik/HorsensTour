package library.resep1.ViewModel;

import java.time.LocalDateTime;
import java.util.List;
import library.resep1.Model.Collections.TripList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Entities.Trip;

public class TripViewModel {
  private final TripList tripList;

  public TripViewModel() {
    tripList = new TripList();
  }

  public TripViewModel(String fileName) {
    tripList = new TripList(fileName);
  }

  public void addTrip(
      String tripId,
      String destination,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String additionalDetails,
      String busNumber,
      String chauffeurName
  ) {
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
      String additionalDetails
  ) {
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
    return tripList.getAvailableBuses(startTime, endTime, allBuses);
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
}
