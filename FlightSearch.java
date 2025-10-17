package flight; 

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class FlightSearch {
    private String departureDate;
    private String departureAirportCode;
    private boolean emergencyRowSeating;
    private String returnDate;
    private String destinationAirportCode;
    private String seatingClass;
    private int adultPassengerCount;
    private int childPassengerCount;
    private int infantPassengerCount;

    private static final Set<String> VALID_AIRPORT_CODES = 
        Set.of("syd", "mel", "lax", "cdg", "del", "pvg", "doh");
    
    private static final Set<String> VALID_SEATING_CLASSES = 
        Set.of("economy", "premium economy", "business", "first");
        
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private boolean isValidDate(String dateStr, LocalDate minAllowedDate) {
        if (dateStr == null || dateStr.isBlank()) return false;
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            if (date.isBefore(minAllowedDate)) {
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean runFlightSearch(String departureDate, String departureAirportCode, boolean emergencyRowSeating,
                                   String returnDate, String destinationAirportCode, String seatingClass,
                                   int adultPassengerCount, int childPassengerCount, int infantPassengerCount) {
        
        int totalPassengers = adultPassengerCount + childPassengerCount + infantPassengerCount;
        if (totalPassengers < 1 || totalPassengers > 9) {
            return false;
        }

        if (childPassengerCount > (adultPassengerCount * 2)) {
            return false;
        }

        if (infantPassengerCount > adultPassengerCount) {
            return false;
        }

        if (seatingClass == null || !VALID_SEATING_CLASSES.contains(seatingClass)) {
            return false;
        }

        if (emergencyRowSeating && !"economy".equals(seatingClass)) {
            return false;
        }

        if (childPassengerCount > 0) {
            if (emergencyRowSeating || "first".equals(seatingClass)) {
                return false;
            }
        }

        if (infantPassengerCount > 0) {
            if (emergencyRowSeating || "business".equals(seatingClass)) {
                return false;
            }
        }
        
        if (departureAirportCode == null || destinationAirportCode == null ||
            !VALID_AIRPORT_CODES.contains(departureAirportCode) ||
            !VALID_AIRPORT_CODES.contains(destinationAirportCode) ||
            departureAirportCode.equals(destinationAirportCode)) {
            return false;
        }

        LocalDate currentDate = LocalDate.now();
        if (!isValidDate(departureDate, currentDate)) {
            return false;
        }
        
        LocalDate depDate = LocalDate.parse(departureDate, DATE_FORMATTER);
        if (!isValidDate(returnDate, depDate)) {
            return false;
        }
        
        this.departureDate = departureDate;
        this.departureAirportCode = departureAirportCode;
        this.emergencyRowSeating = emergencyRowSeating;
        this.returnDate = returnDate;
        this.destinationAirportCode = destinationAirportCode;
        this.seatingClass = seatingClass;
        this.adultPassengerCount = adultPassengerCount;
        this.childPassengerCount = childPassengerCount;
        this.infantPassengerCount = infantPassengerCount;

        return true;
    }

    public String getDepartureDate() { return departureDate; }
    public String getDepartureAirportCode() { return departureAirportCode; }
    public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
    public String getReturnDate() { return returnDate; }
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public String getSeatingClass() { return seatingClass; }
    public int getAdultPassengerCount() { return adultPassengerCount; }
    public int getChildPassengerCount() { return childPassengerCount; }
    public int getInfantPassengerCount() { return infantPassengerCount; }
}
