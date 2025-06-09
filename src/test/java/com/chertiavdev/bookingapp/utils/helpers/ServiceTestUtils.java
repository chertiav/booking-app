package com.chertiavdev.bookingapp.utils.helpers;

import static com.chertiavdev.bookingapp.model.Accommodation.Type.HOUSE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_AVAILABILITY;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_DAILY_RATE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_DEFAULT_AMENITIES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_SIZE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_APARTMENT_NUMBER_25;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_CITY_KYIV;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_HOUSE_NUMBER_15B;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_STREET_KHRESHCHATYK;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_DAYS_UNTIL_CHECKOUT;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.CATEGORY_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_URL;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_FIRST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_LAST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_EXAMPLE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.VALID_USER_PASSWORD;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAddressRequestDto;
import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingExpiredNotificationDto;
import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkRequestDto;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Address;
import com.chertiavdev.bookingapp.model.Amenity;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.TelegramLink;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.model.UserTelegram;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class ServiceTestUtils {

    private ServiceTestUtils() {
    }

    //=======================Accommodation===========================================
    public static CreateAccommodationRequestDto createSampleAccommodationRequest() {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto();
        requestDto.setType(HOUSE);
        requestDto.setLocation(createSampleAddressRequest());
        requestDto.setSize(ACCOMMODATION_SIZE);
        requestDto.setAmenities(ACCOMMODATION_DEFAULT_AMENITIES);
        requestDto.setDailyRate(ACCOMMODATION_DAILY_RATE);
        requestDto.setAvailability(ACCOMMODATION_AVAILABILITY);

        return requestDto;
    }

    public static Accommodation accommodationFromRequestDto(
            CreateAccommodationRequestDto requestDto
    ) {
        Accommodation accommodation = new Accommodation();
        accommodation.setType(requestDto.getType());
        accommodation.setLocation(addressFromRequestDto(requestDto.getLocation()));
        accommodation.setSize(requestDto.getSize());
        accommodation.setAmenities(getAmenitiesById(requestDto.getAmenities()));
        accommodation.setDailyRate(requestDto.getDailyRate()
                .setScale(2, RoundingMode.HALF_UP));
        accommodation.setAvailability(requestDto.getAvailability());

        return accommodation;
    }

    public static Set<Amenity> getAmenitiesById(List<Long> amenitiesIds) {
        return loadAllAmenity().stream()
                .filter(amenity -> amenitiesIds.contains(amenity.getId()))
                .collect(Collectors.toSet());
    }

    public static AccommodationDto mapAccommodationToDto(Accommodation accommodation) {
        AccommodationDto accommodationDto = new AccommodationDto();
        accommodationDto.setId(accommodation.getId());
        accommodationDto.setType(accommodation.getType());
        accommodationDto.setLocation(mapAddressToString(accommodation.getLocation()));
        accommodationDto.setSize(accommodation.getSize());
        accommodationDto.setAmenitiesIds(getSetOfAmenitiesId(accommodation.getAmenities()));
        accommodationDto.setDailyRate(accommodation.getDailyRate());
        accommodationDto.setAvailability(accommodation.getAvailability());

        return accommodationDto;
    }

    public static String generateAccommodationExistsMessage(
            CreateAccommodationRequestDto requestDto
    ) {
        return String.format("Accommodation with the same city: %s, street: %s, house number: %s, "
                        + "apartment number: %s, type: %s, and size: %s already exists.",
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize()
        );
    }

    //=======================Amenities===========================================
    public static Set<Amenity> loadAllAmenity() {
        return Set.of(
                createAmenity(1L, "Free Wi-Fi", 1L),
                createAmenity(2L, "Air Conditioning/Heating", 1L),
                createAmenity(3L, "Television", 2L)
        );
    }

    public static Amenity createAmenity(Long id, String name, Long categoryId) {
        Amenity amenity = new Amenity();
        amenity.setId(id);
        amenity.setName(name);
        amenity.setCategory(new AmenityCategory(categoryId));
        amenity.setDeleted(false);
        return amenity;
    }

    public static CreateAmenityRequestDto createSampleAmenityRequest() {
        CreateAmenityRequestDto requestDto = new CreateAmenityRequestDto();
        requestDto.setName(CATEGORY_NAME);
        requestDto.setCategoryId(SAMPLE_TEST_ID_1);
        return requestDto;
    }

    public static Amenity amenityFromRequestDto(
            CreateAmenityRequestDto requestDto
    ) {
        Amenity amenity = new Amenity();
        amenity.setName(requestDto.getName());
        amenity.setCategory(new AmenityCategory(requestDto.getCategoryId()));
        amenity.setDeleted(false);

        return amenity;
    }

    public static AmenityDto mapAmenityToDto(Amenity amenity) {
        AmenityDto amenityDto = new AmenityDto();
        amenityDto.setId(amenity.getId());
        amenityDto.setName(amenity.getName());
        amenityDto.setCategoryId(amenity.getCategory().getId());

        return amenityDto;
    }

    //=======================AmenityCategories===========================================
    public static CreateAmenityCategoryRequestDto createSampleAmenityCategoryRequest() {
        CreateAmenityCategoryRequestDto requestDto = new CreateAmenityCategoryRequestDto();
        requestDto.setName(AMENITY_CATEGORY_NAME);
        return requestDto;
    }

    public static AmenityCategory amenityCategoryFromRequestDto(
            CreateAmenityCategoryRequestDto requestDto
    ) {
        AmenityCategory amenityCategory = new AmenityCategory();
        amenityCategory.setName(requestDto.getName());
        amenityCategory.setDeleted(false);

        return amenityCategory;
    }

    public static AmenityCategoryDto mapAmenityCategorToDto(AmenityCategory amenityCategory) {
        AmenityCategoryDto amenityCategoryDto = new AmenityCategoryDto();
        amenityCategoryDto.setId(amenityCategory.getId());
        amenityCategoryDto.setName(amenityCategory.getName());

        return amenityCategoryDto;
    }

    //=======================Booking===========================================
    public static CreateBookingRequestDto createSampleBookingRequest() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setCheckIn(LocalDate.now());
        requestDto.setCheckOut(LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT));
        requestDto.setAccommodationId(SAMPLE_TEST_ID_1);

        return requestDto;
    }

    public static Booking bookingFromRequestDto(
            CreateBookingRequestDto requestDto
    ) {
        Booking booking = new Booking();
        booking.setCheckIn(requestDto.getCheckIn());
        booking.setCheckOut(requestDto.getCheckOut());
        booking.setAccommodation(new Accommodation(requestDto.getAccommodationId()));
        booking.setStatus(Booking.Status.PENDING);
        booking.setDeleted(false);

        return booking;
    }

    public static BookingDto mapBookingToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setCheckIn(booking.getCheckIn());
        bookingDto.setCheckOut(booking.getCheckOut());
        bookingDto.setAccommodationId(booking.getAccommodation().getId());
        bookingDto.setUserId(booking.getUser().getId());
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static BookingSearchParameters createBookingSearchParameters(
            String userId, String status
    ) {
        String[] users = new String[]{userId};
        String[] statuses = new String[]{status};
        return new BookingSearchParameters(users, statuses);
    }

    public static Specification<Booking> getBookingSpecification(
            String firstValueKey,
            String firstValue,
            String secondValueKey,
            String secondValue) {
        return Specification
                .where(createEqualSpecification(firstValueKey, firstValue))
                .and(createEqualSpecification(secondValueKey, secondValue));
    }

    public static Specification<Booking> createEqualSpecification(String key, String value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(key), value);
    }

    public static BookingExpiredNotificationDto createBookingExpiredNotificationDto(
            Booking booking,
            User user) {
        BookingExpiredNotificationDto notificationDto = new BookingExpiredNotificationDto();
        notificationDto.setBookingId(booking.getId());
        notificationDto.setCheckOut(booking.getCheckOut());
        notificationDto.setLocation(createAddressString());
        notificationDto.setCustomer(createFullName(user));
        notificationDto.setCustomerEmail(user.getEmail());
        notificationDto.setStatus(booking.getStatus().name());

        return notificationDto;
    }

    //=======================User===========================================
    public static User createTestUser() {
        User user = new User();
        user.setId(SAMPLE_TEST_ID_1);
        user.setFirstName(USERNAME_FIRST);
        user.setLastName(USERNAME_LAST);
        user.setEmail(USER_EMAIL_EXAMPLE);
        user.setPassword(VALID_USER_PASSWORD);
        user.setDeleted(false);
        return user;
    }

    public static UserRegisterRequestDto createUserRegisterRequest() {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto();
        requestDto.setEmail(USER_EMAIL_EXAMPLE);
        requestDto.setPassword(VALID_USER_PASSWORD);
        requestDto.setRepeatPassword(VALID_USER_PASSWORD);
        requestDto.setFirstName(USERNAME_FIRST);
        requestDto.setLastName(USERNAME_LAST);
        return requestDto;
    }

    public static User createUserFromDto(UserRegisterRequestDto requestDto) {
        User userToModel = new User();
        userToModel.setEmail(requestDto.getEmail());
        userToModel.setPassword(requestDto.getPassword());
        userToModel.setFirstName(requestDto.getFirstName());
        userToModel.setLastName(requestDto.getLastName());
        return userToModel;
    }

    public static Role createUserRole(Role.RoleName roleName, Long id) {
        Role userRole = new Role();
        userRole.setId(id);
        userRole.setName(roleName);
        return userRole;
    }

    public static User initializeUser(UserRegisterRequestDto requestDto, Role userRole, Long id) {
        User savedUser = new User();
        savedUser.setId(id);
        savedUser.setEmail(requestDto.getEmail());
        savedUser.setFirstName(requestDto.getFirstName());
        savedUser.setLastName(requestDto.getLastName());
        savedUser.setPassword(requestDto.getPassword());
        savedUser.setDeleted(false);
        savedUser.setRoles(new HashSet<>(Set.of(userRole)));
        return savedUser;
    }

    public static UserDto mapToUserDto(User savedUser) {
        UserDto expected = new UserDto();
        expected.setId(savedUser.getId());
        expected.setEmail(savedUser.getEmail());
        expected.setFirstName(savedUser.getFirstName());
        expected.setLastName(savedUser.getLastName());
        return expected;
    }

    public static UserUpdateRequestDto createUserUpdateRequestDto(
            String firstName, String lastName
    ) {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setFirstName(firstName);
        requestDto.setLastName(lastName);
        return requestDto;
    }

    public static UserDto updateNamesUserDto(UserDto userDto, String firstName, String lastName) {
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        return userDto;
    }

    public static User updateNamesUser(User user, String firstName, String lastName) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    public static UserWithRoleDto mapToUserWithRoleDto(User user) {
        UserWithRoleDto userWithRoleDto = new UserWithRoleDto();
        userWithRoleDto.setId(user.getId());
        userWithRoleDto.setEmail(user.getEmail());
        userWithRoleDto.setFirstName(user.getFirstName());
        userWithRoleDto.setLastName(user.getLastName());
        userWithRoleDto.setRoles(getUserRoleAuthorities(user));
        return userWithRoleDto;
    }

    public static Set<String> getUserRoleAuthorities(User user) {
        return user.getRoles().stream()
                .map(Role::getAuthority).collect(Collectors.toSet());
    }

    public static UserUpdateRoleRequestDto createUserUpdateRoleRequestDto(Role.RoleName roleName) {
        UserUpdateRoleRequestDto requestDto = new UserUpdateRoleRequestDto();
        requestDto.setRoleName(roleName);
        return requestDto;
    }

    //=======================UserTelegram===================================================
    public static UserTelegram createTestUserTelegram(User user, long chatId) {
        UserTelegram userTelegram = new UserTelegram();
        userTelegram.setUser(user);
        userTelegram.setChatId(chatId);
        userTelegram.setDeleted(false);
        return userTelegram;
    }

    //=======================Payment===========================================
    public static Payment createSamplePayment() {
        Payment payment = new Payment();
        payment.setId(SAMPLE_TEST_ID_1);
        payment.setStatus(Payment.Status.PENDING);
        payment.setBooking(new Booking(SAMPLE_TEST_ID_1));
        payment.setSessionId(PAYMENT_SESSION_ID);
        payment.setSessionId(PAYMENT_SESSION_URL);
        payment.setAmountToPay(BigDecimal.TEN);
        payment.setDeleted(false);

        return payment;
    }

    public static PaymentDto mapPaymentToDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setBookingId(payment.getBooking().getId());
        paymentDto.setSessionId(payment.getSessionId());
        paymentDto.setSessionUrl(payment.getSessionUrl());
        paymentDto.setAmountToPay(payment.getAmountToPay());
        paymentDto.setStatus(payment.getStatus().name());

        return paymentDto;
    }

    public static CreatePaymentRequestDto createSamplePaymentRequest() {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto();
        requestDto.setBookingId(SAMPLE_TEST_ID_1);

        return requestDto;
    }

    public static Payment paymentFromRequestDto(
            CreatePaymentRequestDto requestDto
    ) {
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setBooking(new Booking(requestDto.getBookingId()));
        payment.setSessionId(PAYMENT_SESSION_ID);
        payment.setSessionId(PAYMENT_SESSION_URL);
        payment.setAmountToPay(BigDecimal.TEN);
        payment.setDeleted(false);

        return payment;
    }

    //=====================================Session=========================================
    public static Session createSampleSession() {
        Session session = new Session();
        session.setId(PAYMENT_SESSION_ID);
        session.setUrl(PAYMENT_SESSION_URL);

        return session;
    }

    //================================TelegramLink==========================================
    public static TelegramLink createTelegramLink(
            User user,
            String token,
            Instant expiresAt,
            boolean isDeleted) {
        TelegramLink telegramLink = new TelegramLink();
        telegramLink.setUser(user);
        telegramLink.setToken(token);
        telegramLink.setExpiresAt(expiresAt);
        telegramLink.setDeleted(isDeleted);
        return telegramLink;
    }

    public static TelegramLinkRequestDto createTelegramLinkRequestDto(
            TelegramLink savedTelegramLink
    ) {
        TelegramLinkRequestDto dto = new TelegramLinkRequestDto();
        dto.setLink(savedTelegramLink.getToken());
        return dto;
    }

    public static Instant calculateExpirationInstant(int minutes, boolean isFuture) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        return isFuture
                ? now.plusMinutes(minutes).toInstant()
                : now.minusMinutes(minutes).toInstant();
    }

    //========================methods for all services======================================
    public static <T> Page<T> createPage(List<T> listOfObjects, Pageable pageable) {
        return new PageImpl<>(listOfObjects, pageable, listOfObjects.size());
    }

    private static CreateAddressRequestDto createSampleAddressRequest() {
        CreateAddressRequestDto requestDto = new CreateAddressRequestDto();
        requestDto.setStreet(ADDRESS_STREET_KHRESHCHATYK);
        requestDto.setCity(ADDRESS_CITY_KYIV);
        requestDto.setHouseNumber(ADDRESS_HOUSE_NUMBER_15B);
        requestDto.setApartmentNumber(ADDRESS_APARTMENT_NUMBER_25);

        return requestDto;
    }

    private static Address addressFromRequestDto(CreateAddressRequestDto requestDto) {
        Address address = new Address();
        address.setStreet(requestDto.getStreet());
        address.setCity(requestDto.getCity());
        address.setHouseNumber(requestDto.getHouseNumber());
        address.setApartmentNumber(requestDto.getApartmentNumber());

        return address;
    }

    private static String mapAddressToString(Address address) {
        return String.format("%s %s, %s, %s",
                address.getStreet(),
                address.getHouseNumber(),
                address.getApartmentNumber(),
                address.getCity());
    }

    private static Set<Long> getSetOfAmenitiesId(Set<Amenity> amenities) {
        return amenities.stream()
                .map(Amenity::getId)
                .collect(Collectors.toSet());
    }

    private static String createAddressString() {
        return String.format("%s %s, %s, %s",
                ADDRESS_STREET_KHRESHCHATYK,
                ADDRESS_HOUSE_NUMBER_15B,
                ADDRESS_APARTMENT_NUMBER_25,
                ADDRESS_CITY_KYIV);
    }

    private static String createFullName(User user) {
        return String.format("%s %s", user.getFirstName(), user.getLastName());
    }
}
