package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Booking.Status.CANCELED;
import static com.chertiavdev.bookingapp.model.Booking.Status.EXPIRED;
import static com.chertiavdev.bookingapp.model.Role.RoleName.ADMIN;
import static com.chertiavdev.bookingapp.util.helpers.NotificationUtils.bookingNotificationForAdmins;
import static com.chertiavdev.bookingapp.util.helpers.NotificationUtils.bookingNotificationToUser;
import static com.chertiavdev.bookingapp.util.helpers.NotificationUtils.buildBookingExpiredAlert;

import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.exception.AccommodationAvailabilityException;
import com.chertiavdev.bookingapp.exception.BookingAlreadyCancelledException;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.exception.PendingPaymentsException;
import com.chertiavdev.bookingapp.mapper.BookingMapper;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Booking.Status;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.booking.BookingRepository;
import com.chertiavdev.bookingapp.repository.booking.BookingSpecificationBuilder;
import com.chertiavdev.bookingapp.service.BookingService;
import com.chertiavdev.bookingapp.service.NotificationService;
import com.chertiavdev.bookingapp.service.PaymentService;
import java.time.LocalDate;
import java.util.List;
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
    private static final String ACTION_CANCELED = "canceled";
    private static final String BOOKING_PENDING_PAYMENT_MESSAGE =
            "Your booking request has been submitted and is awaiting payment";
    private static final String NO_EXPIRED_BOOKINGS_TODAY = "No expired bookings today!";
    private final BookingSpecificationBuilder bookingSpecificationBuilder;
    private final NotificationService notificationService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final PaymentService paymentService;

    @Transactional
    @Override
    public BookingDto save(CreateBookingRequestDto requestDto, User user) {
        checkPendingPayments(user);
        validateBookingAvailability(requestDto, isBookingAvailable(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        ));
        Booking booking = bookingRepository.save(bookingMapper.toModel(requestDto, user));
        notificationService.sendNotification(
                bookingNotificationForAdmins(booking, user, ACTION_CREATED), ADMIN);
        notificationService.sendNotificationByUserId(
                bookingNotificationToUser(booking, BOOKING_PENDING_PAYMENT_MESSAGE), user.getId());
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
        updateBookingStatus(booking, CANCELED);
        paymentService.updateStatusByBookingId(booking.getId(), Payment.Status.CANCELED);
        notificationService.sendNotification(
                bookingNotificationForAdmins(booking, user, ACTION_CANCELED), ADMIN);
        notificationService.sendNotificationByUserId(
                bookingNotificationToUser(booking, ACTION_CANCELED), user.getId());
    }

    @Transactional
    @Override
    public void checkAndNotifyExpiredBookings(LocalDate expiredToDate) {
        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookings(expiredToDate);
        if (!upcomingBookings.isEmpty()) {
            upcomingBookings.forEach(booking -> {
                updateBookingStatus(booking, EXPIRED);
                String message = buildBookingExpiredAlert(
                        bookingMapper.toBookingExpiredNotificationDto(booking));
                notificationService.sendNotification(message, ADMIN);
            });
        } else {
            notificationService.sendNotification(NO_EXPIRED_BOOKINGS_TODAY, ADMIN);
        }
    }

    private void checkPendingPayments(User user) {
        if (paymentService.getPendingPaymentsCountByUserId(user.getId()) > 0) {
            throw new PendingPaymentsException("User can't create new booking because has "
                    + "pending payments");
        }
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
                .findOverlappingBookings(accommodationId, startDate, endDate)
                .isEmpty();
    }

    private static void validateBookingStatus(Long bookingId, Booking booking) {
        if (booking.getStatus().equals(CANCELED)) {
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
                .findOverlappingBookings(accommodationId, startDate, endDate).stream()
                .filter(booking -> !bookingId.equals(booking.getId()))
                .findAny()
                .isEmpty();
    }

    private void updateBookingStatus(Booking booking, Status status) {
        booking.setStatus(status);
        bookingRepository.save(booking);
    }
}
