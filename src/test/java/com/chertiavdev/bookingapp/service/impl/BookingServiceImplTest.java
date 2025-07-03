package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Role.RoleName.ADMIN;
import static com.chertiavdev.bookingapp.util.helpers.NotificationUtils.bookingNotificationForAdmins;
import static com.chertiavdev.bookingapp.util.helpers.NotificationUtils.bookingNotificationToUser;
import static com.chertiavdev.bookingapp.util.helpers.NotificationUtils.buildBookingExpiredAlert;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_APARTMENT_NUMBER_25;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_CITY_KYIV;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_HOUSE_NUMBER_15B;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_STREET_KHRESHCHATYK;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_ACTION_CANCELED;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_ACTION_CREATED;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_ALREADY_CANCELLED_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_HAS_PENDING_PAYMENTS_COUNT;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_NOT_FOUND_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_NO_EXPIRED_BOOKINGS_TODAY;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_PENDING_PAYMENTS_COUNT;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_PENDING_PAYMENT_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_UNAVAILABLE_ACCOMMODATION_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_UPDATE_ERROR_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_UPDATE_STATUS_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_USER_HAS_PAYMENT_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SEARCH_STATUS_KEY;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SEARCH_USER_ID_KEY;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createAddressString;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createBookingExpiredNotificationDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createBookingSearchParameters;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.getBookingSpecification;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.data.builders.AccommodationTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.AmenityCategoryTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.AmenityTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.BookingTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingExpiredNotificationDto;
import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.exception.AccommodationAvailabilityException;
import com.chertiavdev.bookingapp.exception.BookingAlreadyCancelledException;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.exception.PendingPaymentsException;
import com.chertiavdev.bookingapp.mapper.BookingMapper;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.booking.BookingRepository;
import com.chertiavdev.bookingapp.repository.booking.BookingSpecificationBuilder;
import com.chertiavdev.bookingapp.service.NotificationService;
import com.chertiavdev.bookingapp.service.PaymentService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking Service Implementation Test")
class BookingServiceImplTest {
    private BookingTestDataBuilder bookingsTestDataBuilder;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingSpecificationBuilder bookingSpecificationBuilder;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        bookingsTestDataBuilder = new BookingTestDataBuilder(
                new AccommodationTestDataBuilder(
                        new AmenityTestDataBuilder(
                                new AmenityCategoryTestDataBuilder()
                        )
                ),
                new UserTestDataBuilder()
        );
    }

    @Test
    @DisplayName("Save a booking successfully when valid data is provided")
    void save_ValidData_ShouldReturnSavedBookingDto() {
        //Given
        User user = bookingsTestDataBuilder.getUserJohn();
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder.getPendingBookingRequestDto();
        Booking bookingModel = bookingsTestDataBuilder.getPendingBookingToModel();
        Booking savedBooking = bookingsTestDataBuilder.getPendingBooking();
        BookingDto expected = bookingsTestDataBuilder.getPendingBookingDto();

        when(paymentService.getPendingPaymentsCountByUserId(user.getId()))
                .thenReturn(BOOKING_PENDING_PAYMENTS_COUNT);
        when(bookingRepository.findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        )).thenReturn(Collections.emptyList());
        when(bookingMapper.toModel(requestDto, user)).thenReturn(bookingModel);
        when(bookingRepository.save(bookingModel)).thenReturn(savedBooking);
        when(bookingMapper.toDto(savedBooking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.save(requestDto, user);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentService).getPendingPaymentsCountByUserId(user.getId());
        verify(bookingRepository).findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        );
        verify(bookingMapper).toModel(requestDto, user);
        verify(bookingRepository).save(bookingModel);
        verify(notificationService).sendNotification(
                bookingNotificationForAdmins(savedBooking, user, BOOKING_ACTION_CREATED), ADMIN
        );
        verify(notificationService)
                .sendNotificationByUserId(
                        bookingNotificationToUser(savedBooking, BOOKING_PENDING_PAYMENT_MESSAGE),
                        user.getId()
                );
        verify(bookingMapper).toDto(savedBooking);
        verifyNoMoreInteractions(
                paymentService, bookingRepository, bookingMapper, notificationService);
    }

    @Test
    @DisplayName("Save a booking should throw an exception when user has pending payments")
    void save_UserHasPending_ShouldReturnException() {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder.getPendingBookingRequestDto();
        User user = bookingsTestDataBuilder.getUserJohn();

        when(paymentService.getPendingPaymentsCountByUserId(user.getId()))
                .thenReturn(BOOKING_HAS_PENDING_PAYMENTS_COUNT);

        //When
        Exception exception = assertThrows(PendingPaymentsException.class,
                () -> bookingService.save(requestDto, user));

        //Then
        assertEquals(BOOKING_USER_HAS_PAYMENT_MESSAGE, exception.getMessage(),
                EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentService).getPendingPaymentsCountByUserId(user.getId());
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    @DisplayName("Save a booking should throw an exception when an accommodation is unavailable.")
    void save_UnavailableAccommodation_ShouldReturnException() {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder.getPendingBookingRequestDto();
        User user = bookingsTestDataBuilder.getUserJohn();
        Booking booking = bookingsTestDataBuilder.getPendingBookingToModel();

        when(paymentService.getPendingPaymentsCountByUserId(user.getId()))
                .thenReturn(BOOKING_PENDING_PAYMENTS_COUNT);
        when(bookingRepository.findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        )).thenReturn(List.of(booking));

        //When
        Exception exception = assertThrows(AccommodationAvailabilityException.class,
                () -> bookingService.save(requestDto, user));

        //Then
        String expected = BOOKING_UNAVAILABLE_ACCOMMODATION_MESSAGE
                + requestDto.getCheckIn() + " - " + requestDto.getCheckOut();
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentService).getPendingPaymentsCountByUserId(user.getId());
        verify(bookingRepository).findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut());
        verifyNoMoreInteractions(paymentService, bookingRepository);
    }

    @Test
    @DisplayName("Search bookings with given parameters and pagination")
    void search_ValidParameters_ShouldReturnPageOfBookingDto() {
        //Given
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        BookingDto bookingDto = bookingsTestDataBuilder.getPendingBookingDto();
        Pageable pageable = bookingsTestDataBuilder.getPageable();
        BookingSearchParameters searchParameters = createBookingSearchParameters(
                String.valueOf(SAMPLE_TEST_ID_1),
                Booking.Status.PENDING.name()
        );
        Specification<Booking> specification = getBookingSpecification(
                SEARCH_USER_ID_KEY,
                String.valueOf(SAMPLE_TEST_ID_1),
                SEARCH_STATUS_KEY,
                Booking.Status.PENDING.name()
        );
        Page<Booking> bookingPage = bookingsTestDataBuilder.buildExpectedPendingBookingsPage();

        when(bookingSpecificationBuilder.build(searchParameters)).thenReturn(specification);
        when(bookingRepository.findAll(specification, pageable)).thenReturn(bookingPage);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        //When
        Page<BookingDto> actual = bookingService.search(searchParameters, pageable);

        //Then
        Page<BookingDto> expected = bookingsTestDataBuilder.buildExpectedPendingBookingDtosPage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(
                expected.getNumberOfElements(),
                actual.getNumberOfElements(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalPages(),
                actual.getTotalPages(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalElements(),
                actual.getTotalElements(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);

        verify(bookingSpecificationBuilder).build(searchParameters);
        verify(bookingRepository).findAll(specification, pageable);
        verify(bookingMapper).toDto(booking);
        verifyNoMoreInteractions(bookingSpecificationBuilder, bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("Find all bookings by userId should return BookingDto when a valid ID is provided")
    void findByUserId_ValidUserId_ShouldReturnPageOfBookingDto() {
        //Given
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();
        Booking pendingBooking = bookingsTestDataBuilder.getPendingBooking();
        Booking confirmedBooking = bookingsTestDataBuilder.getConfirmedBooking();
        BookingDto pendingBookingDto = bookingsTestDataBuilder.getPendingBookingDto();
        BookingDto confirmedBookingDto = bookingsTestDataBuilder.getConfirmedBookingDto();
        Pageable pageable = bookingsTestDataBuilder.getPageable();
        Page<Booking> bookingPage = bookingsTestDataBuilder.buildExpectedAllBookingsPage();

        when(bookingRepository.findBookingsByUserId(userId, pageable))
                .thenReturn(bookingPage);
        when(bookingMapper.toDto(pendingBooking)).thenReturn(pendingBookingDto);
        when(bookingMapper.toDto(confirmedBooking)).thenReturn(confirmedBookingDto);

        //When
        Page<BookingDto> actual = bookingService.findByUserId(userId, pageable);

        //Then
        Page<BookingDto> expected = bookingsTestDataBuilder.buildExpectedAllBookingDtosPage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(
                expected.getNumberOfElements(),
                actual.getNumberOfElements(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalPages(),
                actual.getTotalPages(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalElements(),
                actual.getTotalElements(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);

        verify(bookingRepository).findBookingsByUserId(userId, pageable);
        verify(bookingMapper).toDto(pendingBooking);
        verify(bookingMapper).toDto(confirmedBooking);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("Find all bookings by userId when repository returns empty page")
    void findAll_WhenRepositoryReturnsEmptyPage_ShouldReturnEmptyPage() {
        //Given
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();
        Pageable pageable = bookingsTestDataBuilder.getPageable();
        Page<Booking> bookingPage = bookingsTestDataBuilder.buildExpectedEmptyBookingsPage();
        Page<BookingDto> expected = bookingsTestDataBuilder.buildExpectedEmptyBookingDtosPage();

        when(bookingRepository.findBookingsByUserId(userId, pageable))
                .thenReturn(bookingPage);

        //When
        Page<BookingDto> actual = bookingService.findByUserId(userId, pageable);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(
                expected.getNumberOfElements(),
                actual.getNumberOfElements(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalPages(),
                actual.getTotalPages(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalElements(),
                actual.getTotalElements(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);

        verify(bookingRepository).findBookingsByUserId(userId, pageable);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Finding a booking by ID and user ID should return a "
            + "BookingDto when valid data is provided")
    void findByIdAndUserId_ValidData_ShouldReturnBookingDto() {
        //Given
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        BookingDto expected = bookingsTestDataBuilder.getPendingBookingDto();

        when(bookingRepository.findByIdAndUserId(booking.getId(), userId))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.findByIdAndUserId(booking.getId(), userId);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(booking.getId(), userId);
        verify(bookingMapper).toDto(booking);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("Finding a booking by ID and user ID should throw exception when "
            + "the ID is invalid")
    void findByIdAndUserId_InvalidId_ShouldReturnException() {
        //Given
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();

        when(bookingRepository.findByIdAndUserId(INVALID_TEST_ID, userId))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.findByIdAndUserId(INVALID_TEST_ID, userId));

        //Then
        String expected = BOOKING_NOT_FOUND_MESSAGE + INVALID_TEST_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(INVALID_TEST_ID, userId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Updating a booking by ID and user ID should return a "
            + "BookingDto when valid data is provided")
    void updatedByIdAndUserId_ValidData_ShouldReturnBookingDto() {
        //Given
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getUpdatedPendingBookingRequestDto();
        BookingDto expected = bookingsTestDataBuilder.getUpdatedPendingBookingDto();

        when(bookingRepository.findByIdAndUserId(booking.getId(), userId))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        )).thenReturn(Collections.emptyList());
        doAnswer(invocation -> {
            Booking updatedBooking = invocation.getArgument(1);
            updatedBooking.setCheckOut(requestDto.getCheckOut().minusDays(1));
            return null;
        }).when(bookingMapper).updateBookingFromDto(requestDto, booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.updatedByIdAndUserId(
                booking.getId(), userId, requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(booking.getId(), userId);
        verify(bookingRepository).findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        );
        verify(bookingMapper).updateBookingFromDto(requestDto, booking);
        verify(bookingRepository).save(booking);
        verify(bookingMapper).toDto(booking);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("Updating a booking by ID and user ID should throw an exception "
            + "when the ID is invalid")
    void updatedByIdAndUserId_InvalidId_ShouldReturnException() {
        //Given
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder.getPendingBookingRequestDto();

        when(bookingRepository.findByIdAndUserId(INVALID_TEST_ID, userId))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService
                        .updatedByIdAndUserId(INVALID_TEST_ID, userId, requestDto)
        );

        //Then
        String expected = BOOKING_UPDATE_ERROR_MESSAGE + INVALID_TEST_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(INVALID_TEST_ID, userId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Updating a booking by ID and user ID should throw an exception "
            + "when the booking's status is CANCELED.")
    void updatedByIdAndUserId_InvalidBookingStatus_ShouldReturnException() {
        //Given
        Long userId = bookingsTestDataBuilder.getUserSansa().getId();
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getUpdatedPendingBookingRequestDto();
        Booking booking = bookingsTestDataBuilder.getCanceledBooking();

        when(bookingRepository.findByIdAndUserId(booking.getId(), userId))
                .thenReturn(Optional.of(booking));

        //When
        Exception exception = assertThrows(BookingAlreadyCancelledException.class,
                () -> bookingService
                        .updatedByIdAndUserId(booking.getId(), userId, requestDto)
        );

        //Then
        String expected = String.format(BOOKING_ALREADY_CANCELLED_MESSAGE, booking.getId());
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(booking.getId(), userId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Updating a booking by ID and user ID should throw "
            + "an exception when an accommodation is unavailable.")
    void updatedByIdAndUserId_UnavailableAccommodation_ShouldReturnException() {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getOverlappingBookingRequestDto();
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        Booking overlappingBooking = bookingsTestDataBuilder.getConfirmedBooking();

        when(bookingRepository.findByIdAndUserId(booking.getId(), userId))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        )).thenReturn(List.of(overlappingBooking));

        //When
        Exception exception = assertThrows(AccommodationAvailabilityException.class,
                () -> bookingService
                        .updatedByIdAndUserId(booking.getId(), userId, requestDto)
        );

        //Then
        String expected = BOOKING_UNAVAILABLE_ACCOMMODATION_MESSAGE
                + requestDto.getCheckIn() + " - " + requestDto.getCheckOut();
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(SAMPLE_TEST_ID_1, userId);
        verify(bookingRepository).findOverlappingBookings(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        );
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Canceling a booking by ID and user ID successfully when valid data is provided")
    void cancelById_ValidData_ShouldCancelBooking() {
        //Given
        User user = bookingsTestDataBuilder.getUserJohn();
        Booking booking = bookingsTestDataBuilder.getPendingBooking();

        when(bookingRepository.findByIdAndUserId(booking.getId(), user.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //When
        assertDoesNotThrow(() -> bookingService.cancelById(booking.getId(), user));

        //Then
        assertEquals(Booking.Status.CANCELED, booking.getStatus(),
                String.format(BOOKING_UPDATE_STATUS_MESSAGE, Booking.Status.CANCELED));

        verify(bookingRepository).findByIdAndUserId(booking.getId(), user.getId());
        verify(bookingRepository).save(booking);
        verify(paymentService).updateStatusByBookingId(booking.getId(), Payment.Status.CANCELED);
        verify(notificationService).sendNotification(
                bookingNotificationForAdmins(booking, user, BOOKING_ACTION_CANCELED), ADMIN
        );
        verify(notificationService).sendNotificationByUserId(
                bookingNotificationToUser(booking, BOOKING_ACTION_CANCELED), user.getId());
        verifyNoMoreInteractions(bookingRepository, paymentService, notificationService);
    }

    @Test
    @DisplayName("Canceling a booking by ID and user ID "
            + "should throw exception when the booking ID is invalid")
    void cancelById_InvalidBookingId_ShouldReturnException() {
        //Given
        User user = bookingsTestDataBuilder.getUserJohn();

        when(bookingRepository.findByIdAndUserId(INVALID_TEST_ID, user.getId()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.cancelById(INVALID_TEST_ID, user));

        //Then
        String expected = BOOKING_NOT_FOUND_MESSAGE + INVALID_TEST_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(INVALID_TEST_ID, user.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Canceling a booking by ID and user ID should throw an exception "
            + "when the booking's status is CANCELED.")
    void cancelById_InvalidBookingStatus_ShouldReturnException() {
        //Given
        User user = bookingsTestDataBuilder.getUserJohn();
        Booking booking = bookingsTestDataBuilder.getCanceledBooking();

        when(bookingRepository.findByIdAndUserId(booking.getId(), user.getId()))
                .thenReturn(Optional.of(booking));

        //When
        Exception exception = assertThrows(BookingAlreadyCancelledException.class,
                () -> bookingService.cancelById(booking.getId(), user)
        );

        //Then
        String expected = String.format(BOOKING_ALREADY_CANCELLED_MESSAGE, booking.getId());
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository).findByIdAndUserId(booking.getId(), user.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Checking and updating a booking when the check-out date of booking has expired")
    void checkAndNotifyExpiredBookings_ExpiredBookingCheckOut_ShouldSetExpiredForBooking() {
        //Given
        User user = bookingsTestDataBuilder.getUserJohn();
        Booking booking = bookingsTestDataBuilder.getPendingBooking();

        BookingExpiredNotificationDto notificationDto = createBookingExpiredNotificationDto(
                booking,
                user,
                createAddressString(
                        ADDRESS_STREET_KHRESHCHATYK,
                        ADDRESS_HOUSE_NUMBER_15B,
                        ADDRESS_APARTMENT_NUMBER_25,
                        ADDRESS_CITY_KYIV
                )
        );

        when(bookingRepository.findUpcomingBookings(booking.getCheckOut()))
                .thenReturn(List.of(booking));
        when(bookingRepository.save(booking))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingMapper.toBookingExpiredNotificationDto(booking)).thenReturn(notificationDto);

        //When
        assertDoesNotThrow(() -> bookingService
                .checkAndNotifyExpiredBookings(booking.getCheckOut()));

        //Then
        assertEquals(Booking.Status.EXPIRED, booking.getStatus(),
                String.format(BOOKING_UPDATE_STATUS_MESSAGE, Booking.Status.EXPIRED));

        verify(bookingRepository).findUpcomingBookings(booking.getCheckOut());
        verify(bookingRepository).save(booking);
        verify(bookingMapper).toBookingExpiredNotificationDto(booking);
        verify(notificationService).sendNotification(
                buildBookingExpiredAlert(notificationDto), ADMIN);
        verifyNoMoreInteractions(bookingRepository, bookingMapper, notificationService);
    }

    @Test
    @DisplayName("Checking a booking when the check-out date of booking hasn't expired")
    void checkAndNotifyExpiredBookings_ValidBookingCheckOut_ShouldSetExpiredForBooking() {
        //Given
        LocalDate expiredToDate = LocalDate.now();

        when(bookingRepository.findUpcomingBookings(expiredToDate)).thenReturn(List.of());

        //When
        assertDoesNotThrow(() -> bookingService
                .checkAndNotifyExpiredBookings(expiredToDate));

        //Then

        verify(bookingRepository).findUpcomingBookings(expiredToDate);
        verify(notificationService).sendNotification(
                BOOKING_NO_EXPIRED_BOOKINGS_TODAY, ADMIN);
        verifyNoMoreInteractions(bookingRepository, notificationService);
    }
}
