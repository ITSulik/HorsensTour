package library.resep1.Model.Collections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;

public class BusList {

  private static final String FILE_NAME = "buses.json";
  private final String fileName;
  private final Gson gson;
  private final List<Bus> buses;

  public BusList() {
    this(FILE_NAME);}

  public BusList(String fileName) {
    this.fileName = fileName;
    gson = new GsonBuilder().setPrettyPrinting().create();
    buses = loadBuses();}

  public void addBus(Bus bus) {
    if (bus == null) {
      throw new IllegalArgumentException("Bus cannot be null.");}

    for (Bus existingBus : buses) {
      if (existingBus.getBusNumber()
          .equalsIgnoreCase(bus.getBusNumber())) {
        throw new IllegalArgumentException(
            "A bus with this number already exists."
        );}
    }

    buses.add(bus);
    saveBuses();}
  public boolean deleteBus(String busID) {
    Bus busToDelete = null;

    for (Bus bus : buses) {
      if (bus.getBusID().equalsIgnoreCase(busID)) {
        busToDelete = bus;
        break;
      }
    }

    if (busToDelete == null) {
      return false;
    }

    buses.remove(busToDelete);
    saveBuses();
    return true;
  }
  public boolean editBus(
      String busID,
      String busNumber,
      BusType newType,
      int newCapacity,
      String newPurpose
  ) {
    for (Bus bus : buses) {
      if (bus.getBusID().equalsIgnoreCase(busID)) {

        bus.setBusNumber(busNumber);
        bus.setType(newType);
        bus.setCapacity(newCapacity);
        bus.setPurpose(newPurpose);

        saveBuses();
        return true;
      }
    }

    return false;
  }

  public List<Bus> getAllBuses() {
    return new ArrayList<>(buses);}

  private void saveBuses() {
    try (FileWriter writer = new FileWriter(fileName)) {
      gson.toJson(buses, writer);
    } catch (IOException exception) {
      throw new RuntimeException("Could not save buses.", exception);
    }
  }

  private List<Bus> loadBuses() {
    try (FileReader reader = new FileReader(fileName)) {
      Type listType = new TypeToken<ArrayList<Bus>>() {
      }.getType();

      List<Bus> loadedBuses = gson.fromJson(reader, listType);

      return loadedBuses == null
          ? new ArrayList<>()
          : loadedBuses;

    } catch (IOException exception) {
      return new ArrayList<>();
    }
  }
}
