package library.resep1.Model.Entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Trip {
  private String tripId;
  private String destination;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String additionalDetails;
  private String busNumber;
  private String chauffeurName;

  public Trip(
      String destination,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String additionalDetails
  ) {
    this(destination, startTime, endTime, additionalDetails, "", "");
  }

  public Trip(
      String destination,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String additionalDetails,
      String busNumber,
      String chauffeurName
  ) {
    validateRequired(destination, "Destination is required.");
    validateTimeSlot(startTime, endTime);

    this.tripId = UUID.randomUUID().toString();
    this.destination = destination;
    this.startTime = startTime;
    this.endTime = endTime;
    this.additionalDetails = additionalDetails == null ? "" : additionalDetails;
    this.busNumber = busNumber == null ? "" : busNumber;
    this.chauffeurName = chauffeurName == null ? "" : chauffeurName;
  }

  public String getTripId() {
    return tripId;
  }

  public String getDestination() {
    return destination;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public String getAdditionalDetails() {
    return additionalDetails;
  }

  public String getBusNumber() {
    return busNumber;
  }

  public String getChauffeurName() {
    return chauffeurName;
  }

  public void setTripId(String tripId) {
    validateRequired(tripId, "Trip ID is required.");
    this.tripId = tripId;
  }

  public void setDestination(String destination) {
    validateRequired(destination, "Destination is required.");
    this.destination = destination;
  }

  public void setTimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
    validateTimeSlot(startTime, endTime);
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public void setAdditionalDetails(String additionalDetails) {
    this.additionalDetails = additionalDetails == null ? "" : additionalDetails;
  }

  public void setBusNumber(String busNumber) {
    this.busNumber = busNumber == null ? "" : busNumber;
  }

  public void setChauffeurName(String chauffeurName) {
    this.chauffeurName = chauffeurName == null ? "" : chauffeurName;
  }

  public boolean hasBusAssigned() {
    return busNumber != null && !busNumber.isBlank();
  }

  public boolean hasChauffeurAssigned() {
    return chauffeurName != null && !chauffeurName.isBlank();
  }

  // Checks whether this trip blocks the requested time slot
  public boolean overlaps(LocalDateTime startTime, LocalDateTime endTime) {
    validateTimeSlot(startTime, endTime);
    return this.startTime.isBefore(endTime) && startTime.isBefore(this.endTime);
  }

  private static void validateRequired(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
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
