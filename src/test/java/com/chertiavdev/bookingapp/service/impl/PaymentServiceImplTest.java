package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Role.RoleName.ADMIN;
import static com.chertiavdev.bookingapp.model.Role.RoleName.USER;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_CANCELLED_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOT_COMPLETED_NOTIFICATION;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.CAN_T_RETRIEVE_SESSION_BY_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.EXPECTED_BOOKING_STATUS_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.EXPECTED_PAYMENT_STATUS_PAID_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_RENEWAL_INVALID_STATUS_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_RENEWAL_INVALID_USER_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_NOT_FOUND_BY_ID_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_NOT_FOUND_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SESSION_ID_RETRIEVAL_ERROR_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_FIRST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_LAST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_EXAMPLE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.bookingFromRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createPage;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSampleBookingRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSamplePayment;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSamplePaymentRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSampleSession;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUser;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapPaymentToDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.paymentFromRequestDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Implementation Test")
class PaymentServiceImplTest {
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

    @Test
    @DisplayName("Find by user ID should return page of PaymentDto when a valid user ID "
            + "is provided")
    void getPayments_ValidUserId_ShouldReturnPagePaymentDto() {
        //Given
        Payment payment = createSamplePayment();
        PaymentDto paymentDto = mapPaymentToDto(payment);
        Pageable pageable = PageRequest.of(0, 20);

        Page<Payment> paymentPage = createPage(List.of(payment), pageable);

        when(paymentRepository.findAllByUserId(SAMPLE_TEST_ID_1, pageable)).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //When
        Page<PaymentDto> actual = paymentService.getPayments(SAMPLE_TEST_ID_1, pageable);

        //Then
        Page<PaymentDto> expected = createPage(List.of(paymentDto), pageable);

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

        verify(paymentRepository).findAllByUserId(SAMPLE_TEST_ID_1, pageable);
        verify(paymentMapper).toDto(payment);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Searching for all payments should return a PaymentDto page"
            + "if the user ID is not provided.")
    void getPayments_UserIdIsNull_ShouldReturnPagePaymentDto() {
        //Given
        Payment payment = createSamplePayment();
        PaymentDto paymentDto = mapPaymentToDto(payment);
        Pageable pageable = PageRequest.of(0, 20);

        Page<Payment> paymentPage = createPage(List.of(payment), pageable);

        when(paymentRepository.findAll(pageable)).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //When
        Page<PaymentDto> actual = paymentService.getPayments(null, pageable);

        //Then
        Page<PaymentDto> expected = createPage(List.of(paymentDto), pageable);

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
        verify(paymentMapper).toDto(payment);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("When valid data has been provided, the payment is successfully made.")
    void initiatePayment_ValidData_ShouldReturnPaymentDto() {
        //Given
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);
        CreatePaymentRequestDto requestDto = createSamplePaymentRequest();
        BigDecimal amountToPay = BigDecimal.TEN;
        Session session = createSampleSession();
        session.setAmountTotal(amountToPay.longValue());
        Payment payment = paymentFromRequestDto(requestDto);
        payment.setId(SAMPLE_TEST_ID_1);
        PaymentDto expected = mapPaymentToDto(payment);
        Payment paymentModel = paymentFromRequestDto(requestDto);

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
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);
        CreatePaymentRequestDto requestDto = createSamplePaymentRequest();
        BigDecimal amountToPay = BigDecimal.TEN;
        String expected = String.format(SESSION_ID_RETRIEVAL_ERROR_TEMPLATE,
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_ID,
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_ID);

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
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);

        Booking booking = bookingFromRequestDto(createSampleBookingRequest());
        booking.setId(SAMPLE_TEST_ID_1);
        booking.setUser(user);

        Payment payment = createSamplePayment();
        payment.setBooking(booking);

        PaymentDto expected = mapPaymentToDto(payment);

        when(paymentRepository.findAllBySessionId(PAYMENT_SESSION_ID))
                .thenReturn(Optional.of(payment));
        when(stripeService.isSessionPaid(PAYMENT_SESSION_ID)).thenReturn(true);
        doAnswer(invocation -> {
            Payment updatedPayment = invocation.getArgument(0);
            updatedPayment.setStatus(Payment.Status.PAID);
            return null;
        }).when(paymentRepository).save(payment);
        doAnswer(invocation -> {
            Booking updatedBooking = invocation.getArgument(0);
            updatedBooking.setStatus(Booking.Status.CONFIRMED);
            return null;
        }).when(bookingRepository).save(booking);
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.handleSuccess(PAYMENT_SESSION_ID);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(Payment.Status.PAID, payment.getStatus(),
                String.format(EXPECTED_PAYMENT_STATUS_PAID_MESSAGE, Payment.Status.PAID));
        assertEquals(Booking.Status.CONFIRMED, booking.getStatus(),
                String.format(EXPECTED_BOOKING_STATUS_MESSAGE, Booking.Status.CONFIRMED));

        String paymentNotificationMessage = String.format(PAYMENT_NOTIFICATION,
                payment.getId(), payment.getAmountToPay());

        verify(paymentRepository).findAllBySessionId(PAYMENT_SESSION_ID);
        verify(stripeService).isSessionPaid(PAYMENT_SESSION_ID);
        verify(paymentRepository).save(payment);
        verify(bookingRepository).save(booking);
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
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);
        Booking booking = bookingFromRequestDto(createSampleBookingRequest());
        booking.setId(SAMPLE_TEST_ID_1);
        booking.setUser(user);

        Payment payment = createSamplePayment();
        payment.setBooking(booking);

        PaymentDto expected = mapPaymentToDto(payment);

        when(paymentRepository.findAllBySessionId(PAYMENT_SESSION_ID))
                .thenReturn(Optional.of(payment));
        when(stripeService.isSessionPaid(PAYMENT_SESSION_ID)).thenReturn(false);
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.handleSuccess(PAYMENT_SESSION_ID);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(Payment.Status.PENDING, payment.getStatus(),
                String.format(EXPECTED_PAYMENT_STATUS_PAID_MESSAGE, Payment.Status.PENDING));
        assertEquals(Booking.Status.PENDING, booking.getStatus(),
                String.format(EXPECTED_BOOKING_STATUS_MESSAGE, Booking.Status.PENDING));

        String paymentNotificationMessage = String.format(
                PAYMENT_NOT_COMPLETED_NOTIFICATION, payment.getId());

        verify(paymentRepository).findAllBySessionId(PAYMENT_SESSION_ID);
        verify(stripeService).isSessionPaid(PAYMENT_SESSION_ID);
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
        when(paymentRepository.findAllBySessionId(PAYMENT_SESSION_ID))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> paymentService.handleSuccess(PAYMENT_SESSION_ID));

        //Then
        String expected = PAYMENT_SESSION_NOT_FOUND_MESSAGE + PAYMENT_SESSION_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findAllBySessionId(PAYMENT_SESSION_ID);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Handle cancel should return PaymentDto and send notifications")
    void handleCancel_ValidSessionId_ShouldReturnPaymentDtoAndSendNotifications() {
        //Given
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);

        Booking booking = bookingFromRequestDto(createSampleBookingRequest());
        booking.setId(SAMPLE_TEST_ID_1);
        booking.setUser(user);

        Payment payment = createSamplePayment();
        payment.setBooking(booking);

        PaymentDto expected = mapPaymentToDto(payment);

        when(paymentRepository.findAllBySessionId(PAYMENT_SESSION_ID))
                .thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.handleCancel(PAYMENT_SESSION_ID);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findAllBySessionId(PAYMENT_SESSION_ID);
        verify(notificationService)
                .sendNotificationByUserId(PAYMENT_CANCELLED_NOTIFICATION, user.getId());
        verify(paymentMapper).toDto(payment);
        verifyNoMoreInteractions(paymentRepository, paymentMapper, notificationService);
    }

    @Test
    @DisplayName("Handle cancel should throw an exception when the session ID is invalid")
    void handleCancel_InValidSessionId_ShouldReturnException() {
        //Given
        when(paymentRepository.findAllBySessionId(PAYMENT_SESSION_ID))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> paymentService.handleCancel(PAYMENT_SESSION_ID));

        //Then
        String expected = PAYMENT_SESSION_NOT_FOUND_MESSAGE + PAYMENT_SESSION_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findAllBySessionId(PAYMENT_SESSION_ID);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Expire payment when at least one session is expired")
    void expirePendingPayments_OneSessionExpired_ShouldUpdateStatusToExpiredAndSave() {
        //Given
        Payment payment1 = createSamplePayment();

        Payment payment2 = createSamplePayment();
        payment2.setSessionId(String.valueOf(SAMPLE_TEST_ID_2));
        payment2.setStatus(Payment.Status.EXPIRED);

        when(paymentRepository.findAllByStatus(Payment.Status.PENDING))
                .thenReturn(List.of(payment1, payment2));
        when(stripeService.isSessionExpired(payment1.getSessionId())).thenReturn(true);
        when(stripeService.isSessionExpired(payment2.getSessionId())).thenReturn(false);
        when(paymentRepository.save(payment1)).thenReturn(payment1);

        //When
        assertDoesNotThrow(() -> paymentService.expirePendingPayments());

        //Then
        verify(paymentRepository).findAllByStatus(Payment.Status.PENDING);
        verify(stripeService).isSessionExpired(payment1.getSessionId());
        verify(stripeService).isSessionExpired(payment2.getSessionId());
        verify(paymentRepository).save(payment1);
        verify(paymentRepository, never()).save(payment2);

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
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_ID,
                CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_ID);

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
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);

        Booking booking = bookingFromRequestDto(createSampleBookingRequest());
        booking.setId(SAMPLE_TEST_ID_1);
        booking.setUser(user);

        Payment payment = createSamplePayment();
        payment.setBooking(booking);
        payment.setStatus(Payment.Status.EXPIRED);

        Session session = createSampleSession();
        session.setAmountTotal(payment.getAmountToPay().longValue());

        PaymentDto expected = mapPaymentToDto(payment);

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
        when(paymentRepository.findById(SAMPLE_TEST_ID_1)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> paymentService.renewPayment(SAMPLE_TEST_ID_1, SAMPLE_TEST_ID_2));

        //Then
        String expected = PAYMENT_SESSION_NOT_FOUND_BY_ID_MESSAGE + SAMPLE_TEST_ID_1;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(paymentRepository).findById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("RenewPayment should throw an exception if the payment doesn't belong to the user")
    void renewPayment_PaymentDoesNotBelongUser_ShouldReturnPaymentDto() {
        //Given
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);

        Booking booking = bookingFromRequestDto(createSampleBookingRequest());
        booking.setId(SAMPLE_TEST_ID_1);
        booking.setUser(user);

        Payment payment = createSamplePayment();
        payment.setBooking(booking);

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
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);

        // Case 1: Payment is not EXPIRED, but booking status is PENDING
        Booking pendingBooking = bookingFromRequestDto(createSampleBookingRequest());
        pendingBooking.setId(SAMPLE_TEST_ID_1);
        pendingBooking.setUser(user);
        Payment pendingPayment = createSamplePayment();
        pendingPayment.setBooking(pendingBooking);

        // Case 2: Payment is EXPIRED, but booking is not PENDING
        Booking confirmedBooking = bookingFromRequestDto(createSampleBookingRequest());
        confirmedBooking.setId(SAMPLE_TEST_ID_1);
        confirmedBooking.setUser(user);
        confirmedBooking.setStatus(Booking.Status.CONFIRMED);
        Payment expiredPayment = createSamplePayment();
        expiredPayment.setId(SAMPLE_TEST_ID_2);
        expiredPayment.setBooking(confirmedBooking);

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
        User user = createTestUser(
                SAMPLE_TEST_ID_2, USERNAME_FIRST, USERNAME_LAST, USER_EMAIL_EXAMPLE, USER);

        Booking booking = bookingFromRequestDto(createSampleBookingRequest());
        booking.setId(SAMPLE_TEST_ID_1);
        booking.setUser(user);

        Payment payment = createSamplePayment();
        payment.setBooking(booking);

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
