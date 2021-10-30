package ua.service.vehicles.manufacturer;

import ua.service.vehicles.Identifiable;
import ua.service.vehicles.person.Person;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class Manufacturer implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    //@EntityExists(message = "Ceo doesn't exist")

    @PersonIsNotAvailable (message = "Ceo doesn't exist")
    private Person ceo;
    @NotEmpty(message = "Name Can not be empty")
    private String name;
    @NotEmpty(message = "Country Can not be empty")
    private String country;

    public Manufacturer(){}


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getCeo() {
        return ceo;
    }

    public void setCeo(Person ceo) {
        this.ceo = ceo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
