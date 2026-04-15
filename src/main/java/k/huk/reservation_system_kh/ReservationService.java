package k.huk.reservation_system_kh;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
class ReservationService {
    private Map<Long, Reservation> reservationMap;
    private final AtomicLong idCounter;

    public ReservationService(){
        this.reservationMap = new HashMap<>();
        this.idCounter = new AtomicLong();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null){
            throw new IllegalArgumentException("Id should be empty");
        }
        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("Status should be empty");
        }

        Reservation newReservation = new Reservation(
                this.idCounter.incrementAndGet(),
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(newReservation.id(), newReservation);

        return newReservation;
    }

    public Reservation getReservationById(Long id) {
        if ( ! this.reservationMap.containsKey(id) ){
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }

        return reservationMap.get(id);
    }

    public List<Reservation> findAllReservations() {
        return this.reservationMap.values().stream().toList();
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        if ( !reservationMap.containsKey(id) ){
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }
        var currentReservation = reservationMap.get(id);
        if ( !currentReservation.status().equals(ReservationStatus.PENDING) ){
            throw new IllegalStateException("Cannot modify reservation of status = " + currentReservation.status());
        }

        var updatedReservation = new Reservation(
                currentReservation.id(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                reservationToUpdate.status()
        );
        reservationMap.put(id, updatedReservation);
        return updatedReservation;
    }

    public void deleteReservation(Long id) {
        if ( !reservationMap.containsKey(id) ){
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }

        reservationMap.remove(id);
    }

    public Reservation approveReservation(Long id) {
        if ( !reservationMap.containsKey(id) ){
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }

        var reservation = reservationMap.get(id);

        if ( !reservation.status().equals(ReservationStatus.PENDING) ){
            throw new IllegalStateException("Cannot approve reservation of status = " + reservation.status());
        }

        boolean isConflict = isReservationConflict(reservation);
        if (isConflict){
            throw new IllegalStateException("Cannot approve reservation due to conflict");
        }

        return new Reservation(
            reservation.id(),
            reservation.userId(),
            reservation.roomId(),
            reservation.startDate(),
            reservation.endDate(),
            ReservationStatus.APPROVED
        );
    }

    private boolean isReservationConflict( Reservation reservation ){
        for (Reservation nextReservation : reservationMap.values()){
            if ( reservation.id().equals(nextReservation.id()) ){
                continue;
            }

            if ( !reservation.roomId().equals(nextReservation.roomId()) ){
                continue;
            }

            if ( !nextReservation.status().equals(ReservationStatus.APPROVED) ){
                continue;
            }

            if ( reservation.startDate().isBefore(nextReservation.endDate()) && nextReservation.startDate().isBefore(reservation.endDate()) ){
                return true;
            }
        }

        return false;
    }
}
