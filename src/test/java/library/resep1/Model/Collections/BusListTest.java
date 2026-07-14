package library.resep1.Model.Collections;

import java.nio.file.Path;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class BusListTest {
  @TempDir
  Path tempDir;

  @Test
  void addBusShouldStoreBus() {
    BusList busList = new BusList(
        tempDir.resolve("buses.json").toString()
    );

    Bus bus = new Bus(
        "Bus 01",
        BusType.MINIBUS,
        16,
        "City trips"
    );

    busList.addBus(bus);

    assertEquals(1, busList.getAllBuses().size());
    assertEquals("Bus 01",
        busList.getAllBuses().get(0).getBusNumber());}

  @Test
  void addBusShouldRejectDuplicateBusNumber() {
    BusList busList = new BusList(
        tempDir.resolve("buses.json").toString()
    );

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
