package library.resep1.Model.Collections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Entities.Trip;

public class TripList {
  private static final String FILE_NAME = "trips.json";
  private final String fileName;
  private final Gson gson;
  private final List<Trip> trips;

  public TripList() {
    this(FILE_NAME);
  }

  public TripList(String fileName) {
    this.fileName = fileName;
    gson = createGson();
    trips = loadTrips();
  }

  public void addTrip(Trip trip) {
    if (trip == null) {
      throw new IllegalArgumentException("Trip cannot be null.");
    }

    if (getTripById(trip.getTripId()) != null) {
      throw new IllegalArgumentException("A trip with this ID already exists.");
    }

    trips.add(trip);
    saveTrips();
  }

  public boolean editTrip(
      String tripId,
      String destination,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String additionalDetails
  ) {
    Trip trip = getTripById(tripId);

    if (trip == null) {
      return false;
    }

    trip.setDestination(destination);
    trip.setTimeSlot(startTime, endTime);
    trip.setAdditionalDetails(additionalDetails);
    saveTrips();
    return true;
  }

  public boolean deleteTrip(String tripId) {
    Trip trip = getTripById(tripId);

    if (trip == null) {
      return false;
    }

    trips.remove(trip);
    saveTrips();
    return true;
  }

  public Trip getTripById(String tripId) {
    if (tripId == null) {
      return null;
    }

    for (Trip trip : trips) {
      if (trip.getTripId().equalsIgnoreCase(tripId)) {
        return trip;
      }
    }

    return null;
  }

  public List<Trip> getAllTrips() {
    return new ArrayList<>(trips);
  }

  public List<Bus> getAvailableBuses(
      LocalDateTime startTime,
      LocalDateTime endTime,
      List<Bus> allBuses
  ) {
    validateTimeSlot(startTime, endTime);
    Set<String> unavailableBusNumbers = new HashSet<>();

    // Mark buses that are already assigned to overlapping trips
    for (Trip trip : trips) {
      if (trip.hasBusAssigned() && trip.overlaps(startTime, endTime)) {
        unavailableBusNumbers.add(trip.getBusNumber().toLowerCase());
      }
    }

    List<Bus> availableBuses = new ArrayList<>();
    for (Bus bus : allBuses) {
      if (!unavailableBusNumbers.contains(bus.getBusNumber().toLowerCase())) {
        availableBuses.add(bus);
      }
    }

    return availableBuses;
  }

  public List<Chauffeur> getAvailableChauffeurs(
      LocalDateTime startTime,
      LocalDateTime endTime,
      List<Chauffeur> allChauffeurs
  ) {
    validateTimeSlot(startTime, endTime);
    Set<String> unavailableChauffeurNames = new HashSet<>();

    // Mark chauffeurs that are already assigned to overlapping trips
    for (Trip trip : trips) {
      if (trip.hasChauffeurAssigned() && trip.overlaps(startTime, endTime)) {
        unavailableChauffeurNames.add(trip.getChauffeurName().toLowerCase());
      }
    }

    List<Chauffeur> availableChauffeurs = new ArrayList<>();
    for (Chauffeur chauffeur : allChauffeurs) {
      if (!unavailableChauffeurNames.contains(
          chauffeur.getName().toLowerCase()
      )) {
        availableChauffeurs.add(chauffeur);
      }
    }

    return availableChauffeurs;
  }

  private void saveTrips() {
    try (FileWriter writer = new FileWriter(fileName)) {
      gson.toJson(trips, writer);
    } catch (IOException exception) {
      throw new RuntimeException("Could not save trips.", exception);
    }
  }

  private List<Trip> loadTrips() {
    try (FileReader reader = new FileReader(fileName)) {
      Type listType = new TypeToken<ArrayList<Trip>>() {
      }.getType();

      List<Trip> loadedTrips = gson.fromJson(reader, listType);
      return loadedTrips == null ? new ArrayList<>() : loadedTrips;
    } catch (IOException exception) {
      return new ArrayList<>();
    }
  }

  private static Gson createGson() {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Store LocalDateTime values as readable text in trips.json
    JsonSerializer<LocalDateTime> serializer =
        (value, type, context) -> context.serialize(value.format(formatter));

    // Convert saved trip date and time text back into LocalDateTime
    JsonDeserializer<LocalDateTime> deserializer =
        (json, type, context) -> LocalDateTime.parse(
            json.getAsString(),
            formatter
        );

    return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime.class, serializer)
        .registerTypeAdapter(LocalDateTime.class, deserializer)
        .create();
  }

  private static void validateTimeSlot(
      LocalDateTime startTime,
      LocalDateTime endTime
  ) {
    if (startTime == null) {
      throw new IllegalArgumentException("Start time is required.");
    }

    if (endTime == null) {
      throw new IllegalArgumentException("End time is required.");
    }

    if (!endTime.isAfter(startTime)) {
      throw new IllegalArgumentException("End time must be after start time.");
    }
  }
}
