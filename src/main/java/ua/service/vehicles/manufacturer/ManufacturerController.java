package ua.service.vehicles.manufacturer;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("manufacturer")
public class ManufacturerController {

    private final ManufacturerRepository manufacturerRepository;

    public ManufacturerController(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    @GetMapping(value = "all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Manufacturer> all() {
        return manufacturerRepository.findAll();
    }

    @PostMapping(value="addNew")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Collection<String>> addNew(@Valid @RequestBody Manufacturer manufacturer, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
        }

        manufacturerRepository.save(manufacturer);
        return ResponseEntity.ok(Collections.emptyList());
    }
}
