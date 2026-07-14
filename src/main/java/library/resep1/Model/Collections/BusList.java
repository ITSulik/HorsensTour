package library.resep1.Model.Collections;

import library.resep1.Model.Entities.Bus;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import library.resep1.Model.Entities.Bus;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import library.resep1.Model.Enums.BusType;

public class BusList {

  private static final String FILE_NAME = "buses.json";
  private final Gson gson;
  private final List<Bus> buses;

  public BusList() {
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
  public boolean deleteBus(String busNumber) {
    Bus busToDelete = null;

    for (Bus bus : buses) {
      if (bus.getBusNumber().equalsIgnoreCase(busNumber)) {
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
      String currentBusNumber,
      String newBusNumber,
      BusType newType,
      int newCapacity,
      String newPurpose
  ) {
    for (Bus bus : buses) {
      if (bus.getBusNumber().equalsIgnoreCase(currentBusNumber)) {

        bus.setBusNumber(newBusNumber);
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
    try (FileWriter writer = new FileWriter(FILE_NAME)) {
      gson.toJson(buses, writer);
    } catch (IOException exception) {
      throw new RuntimeException("Could not save buses.", exception);
    }
  }

  private List<Bus> loadBuses() {
    try (FileReader reader = new FileReader(FILE_NAME)) {
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