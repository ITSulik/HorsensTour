package library.resep1.ViewModel;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;
import java.util.List;

public class BusViewModel {
  private final BusList busList;

  public BusViewModel() {
    busList = new BusList();}

  public void addBus(
      String busNumber,
      BusType type,
      int capacity,
      String purpose
  )
  {
    Bus bus = new Bus(busNumber, type, capacity, purpose);
    busList.addBus(bus);}

  public List<Bus> getAllBuses() {
    return busList.getAllBuses();
  }
}