package flight; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

public class FlightSearchTest {

    private FlightSearch flightSearch;
    private final String FUTURE_DATE_DEP;
    private final String FUTURE_DATE_RET;
    private final String PAST_DATE;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FlightSearchTest() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        this.FUTURE_DATE_DEP = tomorrow.format(DATE_FORMATTER);
        this.FUTURE_DATE_RET = tomorrow.plusDays(7).format(DATE_FORMATTER);
        this.PAST_DATE = LocalDate.now().minusDays(1).format(DATE_FORMATTER);
    }

    @BeforeEach
    void setUp() {
        flightSearch = new FlightSearch();
    }
    
    private Object[] getValidParams(int adults, int children, int infants, String seatingClass, boolean emergencyRow) {
        return new Object[]{
            FUTURE_DATE_DEP,  
            "syd",            
            emergencyRow,     
            FUTURE_DATE_RET,  
            "mel",            
            seatingClass,     
            adults,           
            children,         
            infants           
        };
    }

    private void assertFailsAndNotInitialized(Object[] params, String failureMessage) {
        boolean result = flightSearch.runFlightSearch(
            (String) params[0], (String) params[1], (boolean) params[2], 
            (String) params[3], (String) params[4], (String) params[5],
            (int) params[6], (int) params[7], (int) params[8]
        );
        assertFalse(result, failureMessage);
        assertNull(flightSearch.getDepartureDate(), "Attributes must NOT be initialized on validation failure.");
    }

    @Test
    void runFlightSearch_ValidSearch_ReturnsTrueAndInitialisesAttributes() {
        Object[] params = getValidParams(1, 2, 1, "economy", false); 
        boolean result = flightSearch.runFlightSearch(
            (String) params[0], (String) params[1], (boolean) params[2], 
            (String) params[3], (String) params[4], (String) params[5],
            (int) params[6], (int) params[7], (int) params[8]
        );
        assertTrue(result, "Valid search should return true.");
        assertEquals("syd", flightSearch.getDepartureAirportCode(), "Attributes must be correctly initialized on success.");
    }

    @Test
    void testCondition1_TooManyPassengers_ReturnsFalse() {
        Object[] params = getValidParams(9, 1, 0, "economy", false);
        assertFailsAndNotInitialized(params, "C1: Total passengers must not exceed 9.");
    }

    @Test
    void testCondition2_ChildInFirstClass_ReturnsFalse() {
        Object[] params = getValidParams(1, 1, 0, "first", false);
        assertFailsAndNotInitialized(params, "C2: Children cannot be in first class.");
    }

    @Test
    void testCondition3_InfantInBusinessClass_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 1, "business", false);
        assertFailsAndNotInitialized(params, "C3: Infants cannot be in business class.");
    }

    @Test
    void testCondition4_TooManyChildrenPerAdult_ReturnsFalse() {
        Object[] params = getValidParams(1, 3, 0, "economy", false); 
        assertFailsAndNotInitialized(params, "C4: Max 2 children per adult.");
    }

    @Test
    void testCondition5_TooManyInfantsPerAdult_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 2, "economy", false);
        assertFailsAndNotInitialized(params, "C5: Max 1 infant per adult.");
    }
    
    @Test
    void testCondition6_PastDepartureDate_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 0, "economy", false);
        params[0] = PAST_DATE;
        assertFailsAndNotInitialized(params, "C6: Departure date cannot be in the past.");
    }

    @Test
    void testCondition7_InvalidDateCombination_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 0, "economy", false);
        params[0] = "29/02/2025";
        assertFailsAndNotInitialized(params, "C7: Invalid date combination (strict validation).");
    }

    @Test
    void testCondition8_ReturnDateBeforeDepartureDate_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 0, "economy", false);
        params[3] = FUTURE_DATE_DEP;
        params[0] = LocalDate.parse(FUTURE_DATE_DEP, DATE_FORMATTER).plusDays(1).format(DATE_FORMATTER);
        assertFailsAndNotInitialized(params, "C8: Return date cannot be before departure date.");
    }

    @Test
    void testCondition9_InvalidSeatingClass_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 0, "basic", false);
        assertFailsAndNotInitialized(params, "C9: Seating class must be one of the approved options.");
    }

    @Test
    void testCondition10_EmergencyRowInBusiness_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 0, "business", true);
        assertFailsAndNotInitialized(params, "C10: Emergency row only allowed in economy.");
    }

    @Test
    void testCondition11_SameDepartureAndDestinationAirport_ReturnsFalse() {
        Object[] params = getValidParams(1, 0, 0, "economy", false);
        params[1] = "syd";
        params[4] = "syd";
        assertFailsAndNotInitialized(params, "C11: Departure and destination airports cannot be the same.");
    }
}
