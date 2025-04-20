package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Booking.Status.CANCELLED;

import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.exception.AccommodationAvailabilityException;
import com.chertiavdev.bookingapp.exception.BookingAlreadyCancelledException;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.BookingMapper;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.booking.BookingRepository;
import com.chertiavdev.bookingapp.repository.booking.BookingSpecificationBuilder;
import com.chertiavdev.bookingapp.service.BookingService;
import com.chertiavdev.bookingapp.service.NotificationService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final String CAN_T_FIND_BOOKING_BY_ID = "Can't find booking by id: ";
    private static final String ACTION_CREATED = "created";
    private static final String ACTION_CANCELLED = "cancelled";
    private final BookingRepository bookingRepository;
    private final BookingSpecificationBuilder bookingSpecificationBuilder;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public BookingDto save(CreateBookingRequestDto requestDto, User user) {
        validateBookingAvailability(requestDto, isBookingAvailable(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        ));
        Booking booking = bookingMapper.toModel(requestDto, user);
        bookingRepository.save(booking);
        notificationService.sendNotification(
                generateBookingNotification(booking, user, ACTION_CREATED));
        return bookingMapper.toDto(booking);
    }

    @Override
    public Page<BookingDto> search(BookingSearchParameters searchParameters, Pageable pageable) {
        Specification<Booking> bookSpecification = bookingSpecificationBuilder
                .build(searchParameters);
        return bookingRepository.findAll(bookSpecification, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingDto> findByUserId(Long id, Pageable pageable) {
        return bookingRepository.findBookingsByUserId(id, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public BookingDto findByIdAndUserId(Long bookingId, Long userId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_BOOKING_BY_ID
                        + bookingId));
    }

    @Transactional
    @Override
    public BookingDto updatedByIdAndUserId(
            Long bookingId,
            Long userId,
            CreateBookingRequestDto requestDto) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't update booking by id: "
                        + bookingId));
        validateBookingStatus(bookingId, booking);
        validateBookingAvailability(requestDto, isBookingAvailableToUpdate(
                bookingId,
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()));

        bookingMapper.updateBookingFromDto(requestDto, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public void cancelById(Long bookingId, User user) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_BOOKING_BY_ID
                        + bookingId));
        validateBookingStatus(bookingId, booking);
        booking.setStatus(CANCELLED);
        bookingRepository.save(booking);
        notificationService.sendNotification(
                generateBookingNotification(booking, user, ACTION_CANCELLED));
    }

    private void validateBookingAvailability(
            CreateBookingRequestDto requestDto,
            boolean isAvailable) {
        if (!isAvailable) {
            throw new AccommodationAvailabilityException(
                    "Accommodation is not available for the requested dates: "
                            + requestDto.getCheckIn() + " - " + requestDto.getCheckOut());
        }
    }

    private boolean isBookingAvailable(
            Long accommodationId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return bookingRepository
                .findOverlappingBookings(accommodationId, startDate, endDate, CANCELLED)
                .isEmpty();
    }

    private String generateBookingNotification(Booking booking, User user, String action) {
        return String.format("""
                        Booking has been %s.
                        - User: %s %s
                        - Booking ID: %s
                        - Accommodation ID: %s
                        - Check-in: %s
                        - Check-out: %s
                        """,
                action,
                user.getFirstName(),
                user.getLastName(),
                booking.getId(),
                booking.getAccommodation().getId(),
                booking.getCheckIn(),
                booking.getCheckOut()
        );
    }

    private static void validateBookingStatus(Long bookingId, Booking booking) {
        if (booking.getStatus().equals(CANCELLED)) {
            throw new BookingAlreadyCancelledException("Booking with ID " + bookingId
                    + " has already been cancelled.");
        }
    }

    private boolean isBookingAvailableToUpdate(
            Long bookingId,
            Long accommodationId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return bookingRepository
                .findOverlappingBookings(accommodationId, startDate, endDate, CANCELLED).stream()
                .filter(booking -> !bookingId.equals(booking.getId()))
                .findAny()
                .isEmpty();
    }
}
