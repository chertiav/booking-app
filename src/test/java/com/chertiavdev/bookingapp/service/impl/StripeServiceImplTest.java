package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACCOMMODATION_DAILY_RATE_7550;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.BOOKING_SESSION_PREFIX;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CAN_T_RETRIEVE_SESSION_BY_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DEFAULT_SESSION_ITEM_QUANTITY;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_PENDING_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_STATUS_PAID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_STATUS_UNPAID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SESSION_ID_QUERY_STRING;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SESSION_ID_RETRIEVAL_ERROR_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SESSION_PAYMENT_CANCEL_URL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SESSION_PAYMENT_SUCCESS_URL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SESSION_STATUS_EXPIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SESSION_STATUS_OPEN;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.STRIPE_API_ERROR_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.STRIPE_APP_BASE_URL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.STRIPE_APP_BASE_URL_NAME;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.STRIPE_CURRENCY_NAME;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.STRIPE_CURRENCY_USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.data.builders.StripleTestDataBuilder;
import com.chertiavdev.bookingapp.exception.StripeServiceException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stripe Service Implementation Test")
class StripeServiceImplTest {
    private static final String COLON_SPACE = ": ";
    private StripleTestDataBuilder stripleTestDataBuilder;
    @InjectMocks
    private StripeServiceImpl stripeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils
                .setField(stripeService, STRIPE_APP_BASE_URL_NAME, STRIPE_APP_BASE_URL);
        ReflectionTestUtils.setField(stripeService, STRIPE_CURRENCY_NAME, STRIPE_CURRENCY_USD);
        stripleTestDataBuilder = new StripleTestDataBuilder();
    }

    @Test
    @DisplayName("CreateSession should create a new Stripe Session when valid data is provided")
    void createSession_ValidData_ShouldCreateNewStripeSession() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            Session expected = stripleTestDataBuilder.getSessionPendingBooking();

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(expected);

            //When
            Session actual = stripeService
                    .createSession(SAMPLE_TEST_ID_1, ACCOMMODATION_DAILY_RATE_7550);

            //Then
            assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
            assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

            mockedSession.verify(() -> Session
                    .create(argThat((SessionCreateParams params) -> isValidSessionParams(params))));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("CreateSession should throw StripeServiceException when an error occurred "
            + "with StripeSession")
    void createSession_StripeExceptionOccurs_ShouldThrowStripeServiceException() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new StripeException(
                            STRIPE_API_ERROR_MESSAGE,
                            null, null, 0, null
                    ) {
                    });

            //When
            Exception exception = assertThrows(StripeServiceException.class,
                    () -> stripeService.createSession(
                            SAMPLE_TEST_ID_1, ACCOMMODATION_DAILY_RATE_7550)
            );

            //Then
            String actual = exception.getMessage();

            assertEquals(STRIPE_API_ERROR_MESSAGE, actual,
                    EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("IsSessionPaid should return true when payment status is paid")
    void isSessionPaid_SessionPaid_ShouldReturnTrue() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            Session session = mock(Session.class);

            mockedSession.when(() -> Session.retrieve(anyString())).thenReturn(session);
            when(session.getPaymentStatus()).thenReturn(PAYMENT_STATUS_PAID);

            //When
            boolean actual = stripeService.isSessionPaid(anyString());

            //Then
            assertTrue(actual);

            mockedSession.verify(() -> Session.retrieve(anyString()));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("IsSessionPaid should return false when payment status is not paid")
    void isSessionPaid_SessionIsNotPaid_ShouldReturnFalse() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            Session session = mock(Session.class);

            mockedSession.when(() -> Session.retrieve(anyString())).thenReturn(session);
            when(session.getPaymentStatus()).thenReturn(PAYMENT_STATUS_UNPAID);

            //When
            boolean actual = stripeService.isSessionPaid(anyString());

            //Then
            assertFalse(actual);

            mockedSession.verify(() -> Session.retrieve(anyString()));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("IsSessionPaid should throw StripeServiceException when an error occurred "
            + "with StripeSession")
    void isSessionPaid_StripeExceptionOccurs_ShouldThrowStripeServiceException() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            String expected = String.format(SESSION_ID_RETRIEVAL_ERROR_TEMPLATE,
                    CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID,
                    CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID);

            mockedSession.when(() -> Session.retrieve(PAYMENT_SESSION_PENDING_ID))
                    .thenThrow(new StripeException(
                            CAN_T_RETRIEVE_SESSION_BY_ID + COLON_SPACE + PAYMENT_SESSION_PENDING_ID,
                            null, null, 0, null
                    ) {
                    });

            //When
            Exception exception = assertThrows(StripeServiceException.class,
                    () -> stripeService.isSessionPaid(PAYMENT_SESSION_PENDING_ID));

            //Then
            String actual = exception.getMessage();

            assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

            mockedSession.verify(() -> Session.retrieve(PAYMENT_SESSION_PENDING_ID));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("isSessionExpired should return true when the payment session has expired")
    void isSessionExpired_SessionExpired_ShouldReturnTrue() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            Session session = mock(Session.class);

            mockedSession.when(() -> Session.retrieve(anyString())).thenReturn(session);
            when(session.getStatus()).thenReturn(SESSION_STATUS_EXPIRED);

            //When
            boolean actual = stripeService.isSessionExpired(anyString());

            //Then
            assertTrue(actual);

            mockedSession.verify(() -> Session.retrieve(anyString()));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("isSessionExpired should return false when the payment session is not expired")
    void isSessionExpired_SessionIsNotExpired_ShouldReturnFalse() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            Session session = mock(Session.class);

            mockedSession.when(() -> Session.retrieve(anyString())).thenReturn(session);
            when(session.getStatus()).thenReturn(SESSION_STATUS_OPEN);

            //When
            boolean actual = stripeService.isSessionExpired(anyString());

            //Then
            assertFalse(actual);

            mockedSession.verify(() -> Session.retrieve(anyString()));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("isSessionExpired should throw StripeServiceException when an error occurred "
            + "with StripeSession")
    void isSessionExpired_StripeExceptionOccurs_ShouldThrowStripeServiceException() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            //Given
            String expected = String.format(SESSION_ID_RETRIEVAL_ERROR_TEMPLATE,
                    CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID,
                    CAN_T_RETRIEVE_SESSION_BY_ID, PAYMENT_SESSION_PENDING_ID);

            mockedSession.when(() -> Session.retrieve(PAYMENT_SESSION_PENDING_ID))
                    .thenThrow(new StripeException(
                            CAN_T_RETRIEVE_SESSION_BY_ID + COLON_SPACE + PAYMENT_SESSION_PENDING_ID,
                            null, null, 0, null
                    ) {
                    });

            //When
            Exception exception = assertThrows(StripeServiceException.class,
                    () -> stripeService.isSessionExpired(PAYMENT_SESSION_PENDING_ID));

            //Then
            String actual = exception.getMessage();

            assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

            mockedSession.verify(() -> Session.retrieve(PAYMENT_SESSION_PENDING_ID));
            mockedSession.verifyNoMoreInteractions();
        }
    }

    private static boolean isValidSessionParams(SessionCreateParams params) {
        boolean modeIsPayment = params.getMode().equals(SessionCreateParams.Mode.PAYMENT);
        boolean successUrlOk = params.getSuccessUrl().equals(STRIPE_APP_BASE_URL
                + SESSION_PAYMENT_SUCCESS_URL + SESSION_ID_QUERY_STRING);
        boolean cancelUrlOk = params.getCancelUrl().equals(STRIPE_APP_BASE_URL
                + SESSION_PAYMENT_CANCEL_URL + SESSION_ID_QUERY_STRING);

        if (params.getLineItems() == null || params.getLineItems().size() != 1) {
            return false;
        }

        SessionCreateParams.LineItem lineItem = params.getLineItems().get(0);
        boolean quantityCorrect = Long.valueOf(DEFAULT_SESSION_ITEM_QUANTITY)
                .equals(lineItem.getQuantity());

        SessionCreateParams.LineItem.PriceData priceData = lineItem.getPriceData();
        boolean currencyCorrect = STRIPE_CURRENCY_USD.equals(priceData.getCurrency());
        boolean unitAmountCorrect = ACCOMMODATION_DAILY_RATE_7550
                .multiply(BigDecimal.valueOf(100))
                .longValue() == priceData.getUnitAmount();

        String productName = priceData.getProductData().getName();
        boolean productOk = productName.contains(BOOKING_SESSION_PREFIX + SAMPLE_TEST_ID_1);

        return modeIsPayment
                && successUrlOk
                && cancelUrlOk
                && quantityCorrect
                && currencyCorrect
                && unitAmountCorrect
                && productOk;
    }
}
