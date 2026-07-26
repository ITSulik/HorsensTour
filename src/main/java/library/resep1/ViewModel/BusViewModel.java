package library.resep1.ViewModel;

import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;

import java.util.List;

public class BusViewModel {

  private final BusList busList;

  private static final BusViewModel INSTANCE =
          new BusViewModel();

  private BusViewModel() {
    busList = new BusList();
  }

  public static BusViewModel getInstance() {
    return INSTANCE;
  }

  public void addBus(
          String busNumber,
          BusType type,
          int capacity,
          String purpose
  ) {
    Bus bus =
            new Bus(
                    busNumber,
                    type,
                    capacity,
                    purpose
            );

    busList.addBus(bus);
  }

  public boolean editBus(
          String busId,
          String busNumber,
          BusType type,
          int capacity,
          String purpose
  ) {
    return busList.editBus(
            busId,
            busNumber,
            type,
            capacity,
            purpose
    );
  }

  public boolean deleteBus(String busID) {
    return busList.deleteBus(busID);
  }

  public List<Bus> getAllBuses() {
    return busList.getAllBuses();
  }
}