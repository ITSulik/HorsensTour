package library.resep1.Model.Entities;
import library.resep1.Model.Enums.BusType;

public class Bus {
  private String busNumber;
  private BusType type;
  private int capacity;
  private String purpose;

  public Bus(String busNumber, BusType type, int capacity, String purpose) {
    if (busNumber == null || busNumber.isBlank()) {
      throw new IllegalArgumentException("Bus number is required.");}

    if (type == null) {
      throw new IllegalArgumentException("Bus type is required.");}

    if (capacity <= 0) {
      throw new IllegalArgumentException(
          "Capacity must be a positive number."
      );}

    if (purpose == null || purpose.isBlank()) {
      throw new IllegalArgumentException("Purpose is required.");}

    this.busNumber = busNumber;
    this.type = type;
    this.capacity = capacity;
    this.purpose = purpose;
  }

  public String getBusNumber() {
    return busNumber;}

  public BusType getType() {
    return type;}

  public int getCapacity() {
    return capacity;}

  public String getPurpose() {
    return purpose;}

// Added setters to support editing bus records.

public void setBusNumber(String busNumber) {
  if (busNumber == null || busNumber.isBlank()) {
    throw new IllegalArgumentException("Bus number is required.");
  }
  this.busNumber = busNumber;
}

public void setType(BusType type) {
  if (type == null) {
    throw new IllegalArgumentException("Bus type is required.");
  }
  this.type = type;
}

public void setCapacity(int capacity) {
  if (capacity <= 0) {
    throw new IllegalArgumentException(
        "Capacity must be a positive number."
    );
  }
  this.capacity = capacity;
}

public void setPurpose(String purpose) {
  if (purpose == null || purpose.isBlank()) {
    throw new IllegalArgumentException("Purpose is required.");
  }
  this.purpose = purpose;
}
}
