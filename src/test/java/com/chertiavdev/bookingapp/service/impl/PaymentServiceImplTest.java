package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Role.RoleName.ADMIN;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_CANCELLED_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOT_COMPLETED_NOTIFICATION;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.CAN_T_RETRIEVE_SESSION_BY_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.EXPECTED_BOOKING_STATUS_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.EXPECTED_PAYMENT_STATUS_PAID_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_RENEWAL_INVALID_STATUS_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_RENEWAL_INVALID_USER_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_NOT_FOUND_BY_ID_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_NOT_FOUND_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_PENDING_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SESSION_ID_RETRIEVAL_ERROR_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.data.builders.AccommodationTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.AmenityCategoryTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.AmenityTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.BookingTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.PaymentTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.StripleTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.exception.AccessDeniedException;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.exception.PaymentRenewException;
import com.chertiavdev.bookingapp.exception.StripeServiceException;
import com.chertiavdev.bookingapp.mapper.PaymentMapper;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.booking.BookingRepository;
import com.chertiavdev.bookingapp.repository.payment.PaymentRepository;
import com.chertiavdev.bookingapp.service.NotificationService;
import com.chertiavdev.bookingapp.service.StripeService;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Implementation Test")
class PaymentServiceImplTest {
    private PaymentTestDataBuilder paymentTestDataBuilder;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private StripeService stripeService;
    @Mock
    private PaymentMapper paymentMapper;

    @BeforeEach
    void setUp() {
        paymentTestDataBuilder = new PaymentTestDataBuilder(
                new BookingTestDataBuilder(
                        new AccommodationTestDataBuilder(
                                new AmenityTestDataBuilder(
                                        new AmenityCategoryTestDataBuilder()
                                )
                        ),
                        new UserTestDataBuilder()
                ),
                new StripleTestDataBuilder()
        );
    }

    @Test
    @DisplayName("Find by user ID should return page of PaymentDto when a valid user ID "
            + "is provided")
    void getPayments_ValidUserId_ShouldReturnPagePaymentDto() {
        //Given
        Payment paymentPending = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        Payment paymentConfirmed = paymentTestDataBuilder.getPaidPaymentConfirmedBooking();
        PaymentDto paymentPendingDto = paymentTestDataBuilder.getPendingPaymentPendingBookingDto();
        PaymentDto paymentConfirmedDto = paymentTestDataBuilder.getPaidPaymentConfirmedBookingDto();
        Pageable pageable = paymentTestDataBuilder.getPageable();
        Page<Payment> paymentPage = paymentTestDataBuilder.buildAllPaymentsUserJhonToPage();

        when(paymentRepository.findAllByUserId(SAMPLE_TEST_ID_2, pageable)).thenReturn(paymentPage);
        when(paymentMapper.toDto(paymentPending)).thenReturn(paymentPendingDto);
        when(paymentMapper.toDto(paymentConfirmed)).thenReturn(paymentConfirmedDto);

        //When
        Page<PaymentDto> actual = paymentService.getPayments(SAMPLE_TEST_ID_2, pageable);

        //Then
        Page<PaymentDto> expected = paymentTestDataBuilder.buildAllPaymentDtosUserJhonToPage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getNumberOfElements(),
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

        verify(paymentRepository).findAllByUserId(SAMPLE_TEST_ID_2, pageable);
        verify(paymentMapper).toDto(paymentPending);
        verify(paymentMapper).toDto(paymentConfirmed);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Searching for all payments should return a PaymentDto page"
            + "if the user ID is not provided.")
    void getPayments_UserIdIsNull_ShouldReturnPagePaymentDto() {
        //Given
        Payment paymentPending = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        Payment paymentConfirmed = paymentTestDataBuilder.getPaidPaymentConfirmedBooking();
        Payment paymentExpired = paymentTestDataBuilder.getExpiredPaymentPendingBooking();
        PaymentDto paymentPendingDto = paymentTestDataBuilder.getPendingPaymentPendingBookingDto();
        PaymentDto paymentConfirmedDto = paymentTestDataBuilder.getPaidPaymentConfirmedBookingDto();
        PaymentDto paymentExpiredDto = paymentTestDataBuilder.getExpiredPaymentPendingBookingDto();
        Pageable pageable = paymentTestDataBuilder.getPageable();
        Page<Payment> paymentPage = paymentTestDataBuilder.buildAllPaymentBookingsPage();

        when(paymentRepository.findAll(pageable)).thenReturn(paymentPage);
        when(paymentMapper.toDto(paymentPending)).thenReturn(paymentPendingDto);
        when(paymentMapper.toDto(paymentConfirmed)).thenReturn(paymentConfirmedDto);
        when(paymentMapper.toDto(paymentExpired)).thenReturn(paymentExpiredDto);

        //When
        Page<PaymentDto> actual = paymentService.getPayments(null, pageable);

        //Then
        Page<PaymentDto> expected = paymentTestDataBuilder.buildAllPaymentDtosPage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getNumberOfElements(),
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

        verify(paymentRepository).findAll(pageable);
        verify(paymentMapper).toDto(paymentPending);
        verify(paymentMapper).toDto(paymentConfirmed);
        verify(paymentMapper).toDto(paymentExpired);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("When valid data has been provided, the payment is successfully made.")
    void initiatePayment_ValidData_ShouldReturnPaymentDto() {
        //Given
        User user = paymentTestDataBuilder.getPendingBooking().getUser();
        CreatePaymentRequestDto requestDto = paymentTestDataBuilder
                .getPaymentRequestPendingBookingDto();
        Session session = paymentTestDataBuilder.getSessionPendingBooking();
        BigDecimal amountToPay = BigDecimal.TEN;
        Payment payment = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        Payment paymentModel = paymentTestDataBuilder.getPendingPaymentPendingBookingToModel();
        PaymentDto expected = paymentTestDataBuilder.getPendingPaymentPendingBookingDto();

        when(bookingRepository
                .calculateTotalPriceByBookingIdAndUserId(requestDto.getBookingId(), user.getId()))
                .thenReturn(amountToPay);
        when(stripeService.createSession(requestDto.getBookingId(), amountToPay))
                .thenReturn(session);
        when(paymentMapper.toModel(requestDto, session, amountToPay))
                .thenReturn(paymentModel);
        when(paymentRepository.save(paymentModel))
                .thenReturn(payment);
        when(paymentMapper.toDto(payment))
                .thenReturn(expected);

        //When
        PaymentDto actual = paymentService.initiatePayment(requestDto, user);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookingRepository)
                .calculateTotalPriceByBookingIdAndUserId(requestDto.getBookingId(), user.getId());
        verify(stripeService).createSession(requestDto.getBookingId(), amountToPay);
        verify(paymentMapper).toModel(requestDto, session, amountToPay);
        verify(paymentRepository).save(paymentModel);
        verify(paymentMapper).toDto(payment);
        verifyNoMoreInteractions(
                bookingRepository, stripeService, paymentMapper, paymentRepository);
    }

    @Test
    @DisplayName("When an exception occurs during the payment process, "
            + "should throw StripeServiceException")
    void initiatePayment_StripeServiceException_ShouldThrowStripeServiceException() {
        //Given
        User user = paymentTestDataBuilder.getPendingBooking().getUser();
        CreatePaymentRequestDto requestDto = paymentTestDataBuilder
                .getPaymentRequestPendingBookingDto();
        BigDecimal amountToPay = BigDecimal.TEN;
        String expected = String.format(SESSION_ID_RETRIEVAL_ERROR_TEMPLATE,
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID,
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID);

        when(bookingRepository
                .calculateTotalPriceByBookingIdAndUserId(requestDto.getBookingId(), user.getId()))
                .thenReturn(amountToPay);
        when(stripeService.createSession(requestDto.getBookingId(), amountToPay))
                .thenThrow(new StripeServiceException(expected));

        //When
        Exception exception = assertThrows(StripeServiceException.class,
                () -> paymentService.initiatePayment(requestDto, user));

        //Then
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(bookingRepository)
                .calculateTotalPriceByBookingIdAndUserId(requestDto.getBookingId(), user.getId());
        verify(stripeService).createSession(requestDto.getBookingId(), amountToPay);
        verifyNoMoreInteractions(bookingRepository, stripeService);
    }

    @Test
    @DisplayName("Handle success should update payment status and send notifications "
            + "when session is paid")
    void handleSuccess_SessionIsPaid_ShouldReturnPaymentDto() {
        //Given
        Payment payment = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        PaymentDto expected = paymentTestDataBuilder.getPaidPaymentPendingBookingDto();

        when(paymentRepository.findBySessionId(payment.getSessionId()))
                .thenReturn(Optional.of(payment));
        when(stripeService.isSessionPaid(payment.getSessionId())).thenReturn(true);
        doAnswer(invocation -> {
            Payment updatedPayment = invocation.getArgument(0);
            updatedPayment.setStatus(Payment.Status.PAID);
            return null;
        }).when(paymentRepository).save(payment);
        doAnswer(invocation -> {
            Booking updatedBooking = invocation.getArgument(0);
            updatedBooking.setStatus(Booking.Status.CONFIRMED);
            return null;
        }).when(bookingRepository).save(paymentTestDataBuilder.getPendingBooking());
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.handleSuccess(payment.getSessionId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(Payment.Status.PAID, payment.getStatus(),
                String.format(EXPECTED_PAYMENT_STATUS_PAID_MESSAGE, Payment.Status.PAID));
        assertEquals(
                Booking.Status.CONFIRMED,
                paymentTestDataBuilder.getPendingBooking().getStatus(),
                String.format(EXPECTED_BOOKING_STATUS_MESSAGE, Booking.Status.CONFIRMED)
        );

        User user = paymentTestDataBuilder.getPendingBooking().getUser();
        String paymentNotificationMessage = String.format(PAYMENT_NOTIFICATION,
                payment.getId(), payment.getAmountToPay());

        verify(paymentRepository).findBySessionId(payment.getSessionId());
        verify(stripeService).isSessionPaid(payment.getSessionId());
        verify(paymentRepository).save(payment);
        verify(bookingRepository).save(paymentTestDataBuilder.getPendingBooking());
        verify(notificationService).sendNotification(paymentNotificationMessage, ADMIN);
        verify(notificationService)
                .sendNotificationByUserId(paymentNotificationMessage, user.getId());
        verify(paymentMapper).toDto(payment);
        verifyNoMoreInteractions(paymentRepository, stripeService, paymentRepository,
                paymentMapper, bookingRepository, notificationService);
    }

    @Test
    @DisplayName("Handle success should return PaymentDto and "
            + "send notifications without updating payment status when session isn't paid")
    void handleSuccess_SessionIsNotPaid_ShouldReturnPaymentDto() {
        //Given
        Payment payment = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        PaymentDto expected = paymentTestDataBuilder.getPendingPaymentPendingBookingDto();

        when(paymentRepository.findBySessionId(payment.getSessionId()))
                .thenReturn(Optional.of(payment));
        when(stripeService.isSessionPaid(payment.getSessionId())).thenReturn(false);
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.handleSuccess(payment.getSessionId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(Payment.Status.PENDING, payment.getStatus(),
                String.format(EXPECTED_PAYMENT_STATUS_PAID_MESSAGE, Payment.Status.PENDING));
        assertEquals(Booking.Status.PENDING, paymentTestDataBuilder.getPendingBooking().getStatus(),
                String.format(EXPECTED_BOOKING_STATUS_MESSAGE, Booking.Status.PENDING));

        User user = paymentTestDataBuilder.getPendingBooking().getUser();
        String paymentNotificationMessage = String.format(
                PAYMENT_NOT_COMPLETED_NOTIFICATION, payment.getId());

        verify(paymentRepository).findBySessionId(payment.getSessionId());
        verify(stripeService).isSessionPaid(payment.getSessionId());
        verify(notificationService)
                .sendNotificationByUserId(paymentNotificationMessage, user.getId());
        verify(paymentMapper).toDto(payment);
        verifyNoMoreInteractions(
                paymentRepository, stripeService, paymentMapper, notificationService);
    }

    @Test
    @DisplayName("Handle success should throw an exception when the session ID is invalid")
    void handleSuccess_InvalidSessionId_ShouldReturnException() {
        //Given
        when(paymentRepository.findBySessionId(PAYMENT_SESSION_PENDING_ID))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> paymentService.handleSuccess(PAYMENT_SESSION_PENDING_ID));

        //Then
        String expected = PAYMENT_SESSION_NOT_FOUND_MESSAGE + PAYMENT_SESSION_PENDING_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findBySessionId(PAYMENT_SESSION_PENDING_ID);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Handle cancel should return PaymentDto and send notifications")
    void handleCancel_ValidSessionId_ShouldReturnPaymentDtoAndSendNotifications() {
        //Given
        Payment payment = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        PaymentDto expected = paymentTestDataBuilder.getPendingPaymentPendingBookingDto();

        when(paymentRepository.findBySessionId(payment.getSessionId()))
                .thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.handleCancel(payment.getSessionId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        User user = paymentTestDataBuilder.getPendingBooking().getUser();

        verify(paymentRepository).findBySessionId(payment.getSessionId());
        verify(notificationService)
                .sendNotificationByUserId(PAYMENT_CANCELLED_NOTIFICATION, user.getId());
        verify(paymentMapper).toDto(payment);
        verifyNoMoreInteractions(paymentRepository, paymentMapper, notificationService);
    }

    @Test
    @DisplayName("Handle cancel should throw an exception when the session ID is invalid")
    void handleCancel_InValidSessionId_ShouldReturnException() {
        //Given
        when(paymentRepository.findBySessionId(PAYMENT_SESSION_PENDING_ID))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> paymentService.handleCancel(PAYMENT_SESSION_PENDING_ID));

        //Then
        String expected = PAYMENT_SESSION_NOT_FOUND_MESSAGE + PAYMENT_SESSION_PENDING_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findBySessionId(PAYMENT_SESSION_PENDING_ID);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Expire payment when at least one session is expired")
    void expirePendingPayments_OneSessionExpired_ShouldUpdateStatusToExpiredAndSave() {
        //Given
        Payment nonExpiredpayment = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        Payment expiredPayment = paymentTestDataBuilder.getExpiredPaymentPendingBooking();

        when(paymentRepository.findAllByStatus(Payment.Status.PENDING))
                .thenReturn(List.of(nonExpiredpayment, expiredPayment));
        when(stripeService.isSessionExpired(nonExpiredpayment.getSessionId())).thenReturn(true);
        when(stripeService.isSessionExpired(expiredPayment.getSessionId())).thenReturn(false);
        when(paymentRepository.save(nonExpiredpayment)).thenReturn(nonExpiredpayment);

        //When
        assertDoesNotThrow(() -> paymentService.expirePendingPayments());

        //Then
        verify(paymentRepository).findAllByStatus(Payment.Status.PENDING);
        verify(stripeService).isSessionExpired(nonExpiredpayment.getSessionId());
        verify(stripeService).isSessionExpired(expiredPayment.getSessionId());
        verify(paymentRepository).save(nonExpiredpayment);
        verify(paymentRepository, never()).save(expiredPayment);

        verifyNoMoreInteractions(paymentRepository, stripeService);
    }

    @Test
    @DisplayName("Do not expire payment when no session is expired")
    void expirePendingPayments_NoSessionExpired_ShouldDoNothing() {
        //Given
        when(paymentRepository.findAllByStatus(Payment.Status.PENDING))
                .thenReturn(List.of());

        //When
        assertDoesNotThrow(() -> paymentService.expirePendingPayments());

        //Then
        verify(paymentRepository).findAllByStatus(Payment.Status.PENDING);

        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Expire payment when Invalid Session ID should throw an Exception")
    void expirePendingPayments_InvalidSessionId_ShouldThrowException() {
        //Given
        String expected = String.format(SESSION_ID_RETRIEVAL_ERROR_TEMPLATE,
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID,
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID);

        when(paymentRepository.findAllByStatus(Payment.Status.PENDING))
                .thenThrow(new StripeServiceException(expected));

        //When
        Exception exception = assertThrows(StripeServiceException.class,
                () -> paymentRepository.findAllByStatus(Payment.Status.PENDING));

        //Then
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findAllByStatus(Payment.Status.PENDING);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Renew payment should update the payment status and return the updated PaymentDto")
    void renewPayment_ValidData_ShouldReturnPaymentDto() {
        //Given
        Booking booking = paymentTestDataBuilder.getPendingBookingUserSansa();
        Payment payment = paymentTestDataBuilder.getExpiredPaymentPendingBooking();
        Session session = paymentTestDataBuilder.getRenewSession();
        PaymentDto expected = paymentTestDataBuilder.getPaymentRenewSessionDto();
        User user = booking.getUser();

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(stripeService.createSession(booking.getId(), payment.getAmountToPay()))
                .thenReturn(session);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.renewPayment(payment.getId(), user.getId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(stripeService).createSession(booking.getId(), payment.getAmountToPay());
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toDto(payment);

        verifyNoMoreInteractions(paymentRepository, paymentMapper, stripeService);
    }

    @Test
    @DisplayName("Renew payment should throw an exception when the payment ID is invalid")
    void renewPayment_InvalidPaymentId_ShouldThrowException() {
        //Given
        when(paymentRepository.findById(INVALID_TEST_ID)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> paymentService.renewPayment(INVALID_TEST_ID, SAMPLE_TEST_ID_2));

        //Then
        String expected = PAYMENT_SESSION_NOT_FOUND_BY_ID_MESSAGE + INVALID_TEST_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findById(INVALID_TEST_ID);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("RenewPayment should throw an exception if the payment doesn't belong to the user")
    void renewPayment_PaymentDoesNotBelongUser_ShouldReturnPaymentDto() {
        //Given
        Payment payment = paymentTestDataBuilder.getPendingPaymentPendingBooking();

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        //When
        Exception exception = assertThrows(AccessDeniedException.class,
                () -> paymentService.renewPayment(payment.getId(), SAMPLE_TEST_ID_1));

        //Then
        String expected = PAYMENT_RENEWAL_INVALID_USER_MESSAGE + payment.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findById(payment.getId());
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("RenewPayment should throw an exception when invalid status is provided")
    void renewPayment_InvalidStatus_ShouldThrowPaymentRenewException() {
        //Given
        User user = paymentTestDataBuilder.getPendingBooking().getUser();

        // Case 1: Payment is not EXPIRED, but booking status is PENDING
        Payment pendingPayment = paymentTestDataBuilder.getPendingPaymentPendingBooking();

        // Case 2: Payment is EXPIRED, but booking is not PENDING
        Payment expiredPayment = paymentTestDataBuilder.getPaidPaymentConfirmedBooking();

        when(paymentRepository.findById(pendingPayment.getId()))
                .thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.findById(expiredPayment.getId()))
                .thenReturn(Optional.of(expiredPayment));

        //When
        Exception exceptionConfirmedBooking = assertThrows(PaymentRenewException.class,
                () -> paymentService.renewPayment(pendingPayment.getId(), user.getId()));
        Exception exceptionExpiredPayment = assertThrows(PaymentRenewException.class,
                () -> paymentService.renewPayment(expiredPayment.getId(), user.getId()));

        //Then
        String expectedConfirmedBooking = String.format(PAYMENT_RENEWAL_INVALID_STATUS_MESSAGE,
                pendingPayment.getId(), Payment.Status.EXPIRED, Booking.Status.PENDING);

        String expectedPendingPayment = String.format(PAYMENT_RENEWAL_INVALID_STATUS_MESSAGE,
                expiredPayment.getId(), Payment.Status.EXPIRED, Booking.Status.PENDING);

        String actualConfirmedBooking = exceptionConfirmedBooking.getMessage();
        String actualExpiredPayment = exceptionExpiredPayment.getMessage();

        assertEquals(expectedConfirmedBooking, actualConfirmedBooking,
                EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(expectedPendingPayment, actualExpiredPayment,
                EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findById(pendingPayment.getId());
        verify(paymentRepository).findById(expiredPayment.getId());
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Get pending payments count by user ID should return the number "
            + "of pending payments")
    void getPendingPaymentsCountByUserId_ValidUserId_ShouldReturnCount() {
        //Given
        Long expected = 1L;

        when(paymentRepository.findPendingPaymentsCount(SAMPLE_TEST_ID_1)).thenReturn(expected);

        //When
        Long actual = paymentService.getPendingPaymentsCountByUserId(SAMPLE_TEST_ID_1);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findPendingPaymentsCount(SAMPLE_TEST_ID_1);

        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Updating status by booking ID when valid ID is provided")
    void updateStatusByBookingId_ValidId_ShouldUpdateStatusAndSave() {
        //Given
        Booking booking = paymentTestDataBuilder.getPendingBooking();
        Payment payment = paymentTestDataBuilder.getPendingPaymentPendingBooking();

        when(paymentRepository.findByBookingId(booking.getId())).thenReturn(Optional.of(payment));
        doAnswer(invocation -> {
            Payment updatedPayment = invocation.getArgument(0);
            updatedPayment.setStatus(Payment.Status.PAID);
            return null;
        }).when(paymentRepository).save(payment);

        //When
        assertDoesNotThrow(() ->
                paymentService.updateStatusByBookingId(booking.getId(), Payment.Status.PAID));

        //Then
        assertEquals(Payment.Status.PAID, payment.getStatus(),
                String.format(EXPECTED_PAYMENT_STATUS_PAID_MESSAGE, Payment.Status.PAID));

        verify(paymentRepository).findByBookingId(booking.getId());
        verify(paymentRepository).save(payment);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Updating status by booking ID when invalid ID is provided")
    void updateStatusByBookingId_InvalidId_ShouldDoNothing() {
        //Given
        when(paymentRepository.findByBookingId(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.empty());

        when(paymentRepository.findByBookingId(SAMPLE_TEST_ID_1)).thenReturn(Optional.empty());

        //When
        assertDoesNotThrow(() ->
                paymentService.updateStatusByBookingId(SAMPLE_TEST_ID_1, Payment.Status.PAID));

        // Then
        verify(paymentRepository).findByBookingId(SAMPLE_TEST_ID_1);
        verify(paymentRepository, never()).save(any());
        verifyNoMoreInteractions(paymentRepository);
    }
}
