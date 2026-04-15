package k.huk.reservation_system_kh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservation")
class ReservationController {
    private final ReservationService reservationService;
    private final Logger log = LoggerFactory.getLogger(ReservationController.class);

    ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id){
        log.info("getReservationById called");
        return reservationService.getReservationById(id);
    }

    @GetMapping
    public List<Reservation> getAllReservation(){
        log.info("getAllReservation called");
        return reservationService.findAllReservations();
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationToCreate){
        log.info("createReservation called");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("test-header", "123")
                .body(reservationService.createReservation(reservationToCreate));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationToUpdate){
        log.info("updateReservation called id={} reservationToUpdate={}", id, reservationToUpdate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reservationService.updateReservation(id, reservationToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id){
        log.info("deleteReservation called id={}", id);
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok()
                    .build();
        } catch (NoSuchElementException ex){
            return ResponseEntity.status(404)
                    .build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(@PathVariable Long id){
        log.info("approveReservation called id={}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
