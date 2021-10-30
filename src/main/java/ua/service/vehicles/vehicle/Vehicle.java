package ua.service.vehicles.vehicle;

import org.hibernate.annotations.GenericGenerator;
import ua.service.vehicles.Identifiable;
import ua.service.vehicles.IdentifiableCountry;
import ua.service.vehicles.manufacturer.Manufacturer;
import ua.service.vehicles.person.Person;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Parameter;

@Entity
@RegistrationFormat
public class Vehicle implements Identifiable<Long> , IdentifiableCountry<String> {
    private final static String UA_INITIAL_VALUE = "200000000";
    private final static String All_INITIAL_VALUE = "500000000";

    private final static String UA_SEQUENCE_NAME = "vehicle_ua_sequence";
    private final static String All_SEQUENCE_NAME = "vehicle_h_sequence";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @GenericGenerator(name = "sequence", strategy = "ua.service.humanity.vehicle.CountryBasedSequenceIdentifierGenerator",
            parameters = {
                    @Parameter(name = "ua_initial_value", value = UA_INITIAL_VALUE),
                    @Parameter(name = "h_initial_value", value = All_INITIAL_VALUE),
                    @Parameter(name = "ua_sequence_name", value = UA_SEQUENCE_NAME),
                    @Parameter(name = "h_sequence_name", value = All_SEQUENCE_NAME)
            })
    private Long id;

    @NotEmpty(message = "Vin cannot be empty")
    private String vin;
    @NotEmpty(message = "Country cannot be empty")
    private String country;
    private String make;
    private String model;
    @Min(value = 1950,message = "Year cannot be less than 1950")
    private int year;
    @NotEmpty(message = "Registration cannot be empty")
    private String registration;

    @NotNull(message = "FuelType cannot be Null")
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "TransmissionType can not be Null")
    private TransmissionType transmissionType;
    @Min(value = 1,message = "Can not be less than 1 doors")
    @Max(value = 6, message = "Can not be more than 6 doors")
    private int doorsCount;
    @NotNull(message = "rwd can not be Null")
    private Boolean rwd;

    @ManyToOne
    @EntityExists(message = "Manufacturer doesn't exist")
    private Manufacturer manufacturer;

    @ManyToOne
    @EntityExists(message = "Owner doesn't exist")
    private Person owner;

    public Vehicle() {}

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public int getDoorsCount() {
        return doorsCount;
    }

    public void setDoorsCount(int doorsCount) {
        this.doorsCount = doorsCount;
    }

    public Boolean isRwd() {
        return rwd;
    }

    public void setRwd(Boolean rwd) {
        this.rwd = rwd;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
}
