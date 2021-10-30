package ua.service.vehicles.vehicle;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.service.vehicles.services.DecodedVin;
import ua.service.vehicles.services.VinDecoderService;

import javax.validation.Valid;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("vehicle")
@Valid
public class VehicleController {

    private final VehicleRepository vehicleRepository;
    private final VinDecoderService vinDecoderService;
    private final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    public VehicleController(VehicleRepository vehicleRepository, VinDecoderService vinDecoderService) {
        this.vehicleRepository = vehicleRepository;
        this.vinDecoderService = vinDecoderService;
    }

    @GetMapping(value = "all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Vehicle> all() {
        return vehicleRepository.findAll();
    }

    @PostMapping(value = "addNew")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Collection<String>> addNew(@Valid @RequestBody Vehicle vehicle, BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    bindingResult.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList())
            );
        }
        if (Strings.isNullOrEmpty(vehicle.getMake()) || Strings.isNullOrEmpty(vehicle.getModel())) {
            try {
                DecodedVin decodedVin = vinDecoderService.decodeVin(vehicle.getVin());
                vehicle.setMake(decodedVin.getMake());
                vehicle.setModel(decodedVin.getModel());
                vehicle.setYear(decodedVin.getYear());
            } catch (UnknownHostException exception) {
                logger.info("Problems with connection to vin decoder website. Make, model or year can be empty", exception);
            }
        }

        vehicleRepository.save(vehicle);
        return ResponseEntity.ok(Collections.emptyList());
    }

    @DeleteMapping(value = "delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(@PathVariable Long id) {
        vehicleRepository.deleteById(id);
    }

}
