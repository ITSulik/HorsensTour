package library.resep1.Model.Collections;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusListTest {

  @Test
  void addBusShouldStoreBus() {
    BusList busList = new BusList();

    Bus bus = new Bus(
        "Bus 01",
        BusType.MINIBUS,
        16,
        "City trips"
    );

    busList.addBus(bus);

    assertEquals(1, busList.getAllBuses().size());
    assertEquals("Bus 01",
        busList.getAllBuses().getFirst().getBusNumber());}

  @Test
  void addBusShouldRejectDuplicateBusNumber() {
    BusList busList = new BusList();

    Bus firstBus = new Bus(
        "Bus 01",
        BusType.MINIBUS,
        16,
        "City trips"
    );

    Bus duplicateBus = new Bus(
        "bus 01",
        BusType.TOURIST_BUS,
        50,
        "Tourism"
    );

    busList.addBus(firstBus);

    assertThrows(
        IllegalArgumentException.class,
        () -> busList.addBus(duplicateBus)
    );
  }
}