package com.chertiavdev.bookingapp.utils.helpers;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_DAYS_UNTIL_CHECKOUT;

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
import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkDto;
import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Address;
import com.chertiavdev.bookingapp.model.Amenity;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.Role.RoleName;
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
    public static Accommodation createTestAccommodation(
            Long id, Accommodation.Type type, String size, Address location,
            Set<Amenity> amenities, BigDecimal dailyRate, Integer availability
    ) {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setType(type);
        accommodation.setSize(size);
        accommodation.setLocation(location);
        accommodation.setAmenities(amenities);
        accommodation.setDailyRate(dailyRate.setScale(2, RoundingMode.HALF_UP));
        accommodation.setAvailability(availability);
        accommodation.setDeleted(false);
        return accommodation;
    }

    public static Address createTestAddress(
            Long id, String city, String street, String houseNumber, String apartmentNumber
    ) {
        Address address = new Address();
        address.setId(id);
        address.setCity(city);
        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setApartmentNumber(apartmentNumber);
        address.setDeleted(false);
        return address;
    }

    public static CreateAccommodationRequestDto createTestAccommodationRequestDto(
            Accommodation.Type type, CreateAddressRequestDto location, String size,
            List<Long> amenities, BigDecimal dailyRate, Integer availability
    ) {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto();
        requestDto.setType(type);
        requestDto.setLocation(location);
        requestDto.setSize(size);
        requestDto.setAmenities(amenities);
        requestDto.setDailyRate(dailyRate);
        requestDto.setAvailability(availability);
        return requestDto;
    }

    public static CreateAddressRequestDto createTestAddressRequestDto(
            String city, String street, String houseNumber, String apartmentNumber
    ) {
        CreateAddressRequestDto requestDto = new CreateAddressRequestDto();
        requestDto.setCity(city);
        requestDto.setStreet(street);
        requestDto.setHouseNumber(houseNumber);
        requestDto.setApartmentNumber(apartmentNumber);
        return requestDto;
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

    public static String mapAddressToString(Address address) {
        return String.format("%s %s, %s, %s",
                address.getStreet(),
                address.getHouseNumber(),
                address.getApartmentNumber(),
                address.getCity());
    }

    public static Set<Long> getSetOfAmenitiesId(Set<Amenity> amenities) {
        return amenities.stream()
                .map(Amenity::getId)
                .collect(Collectors.toSet());
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
    public static Amenity createTestAmenity(Long id, String name, AmenityCategory amenityCategory) {
        Amenity amenity = new Amenity();
        amenity.setId(id);
        amenity.setName(name);
        amenity.setCategory(amenityCategory);
        amenity.setDeleted(false);
        return amenity;
    }

    public static CreateAmenityRequestDto createTestAmenityRequestDto(
            String name, Long categoryId
    ) {
        CreateAmenityRequestDto requestDto = new CreateAmenityRequestDto();
        requestDto.setName(name);
        requestDto.setCategoryId(categoryId);
        return requestDto;
    }

    public static AmenityDto mapAmenityToDto(Amenity amenity) {
        AmenityDto amenityDto = new AmenityDto();
        amenityDto.setId(amenity.getId());
        amenityDto.setName(amenity.getName());
        amenityDto.setCategoryId(amenity.getCategory().getId());
        return amenityDto;
    }

    //=======================AmenityCategories===========================================
    public static AmenityCategory createTestAmenityCategory(Long id, String name) {
        AmenityCategory amenityCategory = new AmenityCategory();
        amenityCategory.setId(id);
        amenityCategory.setName(name);
        amenityCategory.setDeleted(false);
        return amenityCategory;
    }

    public static CreateAmenityCategoryRequestDto createTestAmenityCategoryRequest(String name) {
        CreateAmenityCategoryRequestDto requestDto = new CreateAmenityCategoryRequestDto();
        requestDto.setName(name);
        return requestDto;
    }

    public static AmenityCategoryDto createTestAmenityCategoryDto(Long id, String name) {
        AmenityCategoryDto amenityCategoryDto = new AmenityCategoryDto();
        amenityCategoryDto.setId(id);
        amenityCategoryDto.setName(name);
        return amenityCategoryDto;
    }

    //=======================Booking===========================================
    public static Booking createTestBooking(
            Long id,
            LocalDate checkIn,
            LocalDate checkOut,
            Accommodation accommodation,
            User user,
            Booking.Status status
    ) {
        Booking booking = new Booking();

        booking.setId(id);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(status);

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

    public static CreateBookingRequestDto createTestBookingRequestDto(
            LocalDate checkInDate,
            LocalDate checkoutDate,
            long bookingId
    ) {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setCheckIn(checkInDate);
        requestDto.setCheckOut(checkoutDate);
        requestDto.setAccommodationId(bookingId);
        return requestDto;
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
            User user,
            String address) {
        BookingExpiredNotificationDto notificationDto = new BookingExpiredNotificationDto();
        notificationDto.setBookingId(booking.getId());
        notificationDto.setCheckOut(booking.getCheckOut());
        notificationDto.setLocation(address);
        notificationDto.setCustomer(createFullName(user));
        notificationDto.setCustomerEmail(user.getEmail());
        notificationDto.setStatus(booking.getStatus().name());

        return notificationDto;
    }

    public static String createFullName(User user) {
        return String.format("%s %s", user.getFirstName(), user.getLastName());
    }

    public static String createAddressString(
            String street,
            String houseNumber,
            String apartmentNumber,
            String city
    ) {
        return String.format("%s %s, %s, %s",
                street,
                houseNumber,
                apartmentNumber,
                city);
    }

    public static BigDecimal calculateTotalPriceByBooking(Booking booking) {
        return booking.getAccommodation().getDailyRate()
                .multiply(BigDecimal.valueOf(BOOKING_DAYS_UNTIL_CHECKOUT));
    }

    //=======================User===========================================
    public static User createTestUser(
            Long id, String firstName, String lastName, String password, String email, Role role
    ) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(new HashSet<>(Set.of(role)));
        user.setDeleted(false);
        return user;
    }

    public static Role createTestUserRole(RoleName roleName, Long roleId) {
        Role userRole = new Role();
        userRole.setId(roleId);
        userRole.setName(roleName);
        return userRole;
    }

    public static UserRegisterRequestDto createTestUserRegisterRequest(
            String email, String password, String repeatPassword, String firstName, String lastName
    ) {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);
        requestDto.setRepeatPassword(repeatPassword);
        requestDto.setFirstName(firstName);
        requestDto.setLastName(lastName);
        return requestDto;
    }

    public static UserUpdateRequestDto createTestUserUpdateRequestDto(
            String firstName, String lastName
    ) {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setFirstName(firstName);
        requestDto.setLastName(lastName);
        return requestDto;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto expected = new UserDto();
        expected.setId(user.getId());
        expected.setEmail(user.getEmail());
        expected.setFirstName(user.getFirstName());
        expected.setLastName(user.getLastName());
        return expected;
    }

    public static UserUpdateRoleRequestDto createTestUserUpdateRoleRequestDto(RoleName roleName) {
        UserUpdateRoleRequestDto requestDto = new UserUpdateRoleRequestDto();
        requestDto.setRoleName(roleName);
        return requestDto;
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

    public static User updateNamesUser(User user, String firstName, String lastName) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    public static Set<String> getUserRoleAuthorities(User user) {
        return user.getRoles().stream()
                .map(Role::getAuthority).collect(Collectors.toSet());
    }

    //=======================UserTelegram===================================================
    public static UserTelegram createTestUserTelegram(
            Long id, User user, long chatId, boolean isDeleted
    ) {
        UserTelegram userTelegram = new UserTelegram();
        userTelegram.setId(id);
        userTelegram.setUser(user);
        userTelegram.setChatId(chatId);
        userTelegram.setDeleted(isDeleted);
        return userTelegram;
    }

    public static UserTelegramStatusDto createTestUserTelegramStatusDto(UserTelegram userTelegram) {
        UserTelegramStatusDto userTelegramStatusDto = new UserTelegramStatusDto();
        userTelegramStatusDto.setEnabled(!userTelegram.isDeleted());
        return userTelegramStatusDto;
    }

    //=======================Payment===========================================
    public static Payment createTestPayment(
            Long id, Payment.Status status, Booking booking, String sessionId,
            String sessionUrl, BigDecimal amountToPay, boolean isDeleted
    ) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setStatus(status);
        payment.setBooking(booking);
        payment.setSessionId(sessionId);
        payment.setSessionUrl(sessionUrl);
        payment.setAmountToPay(amountToPay);
        payment.setDeleted(isDeleted);
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

    public static CreatePaymentRequestDto createTestPaymentRequestDto(Long bookingId) {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto();
        requestDto.setBookingId(bookingId);
        return requestDto;
    }

    //=====================================Session=========================================
    public static Session createTestSession(
            String sessionId, String sessionUrl, BigDecimal amountToPay
    ) {
        Session session = new Session();
        session.setId(sessionId);
        session.setUrl(sessionUrl);
        session.setAmountTotal(amountToPay.longValue());
        return session;
    }

    //================================UserTelegramLink==========================================
    public static TelegramLink createTestTelegramLink(
            Long id,
            User user,
            String token,
            Instant expiresAt,
            boolean isDeleted) {
        TelegramLink telegramLink = new TelegramLink();
        telegramLink.setId(id);
        telegramLink.setUser(user);
        telegramLink.setToken(token);
        telegramLink.setExpiresAt(expiresAt);
        telegramLink.setDeleted(isDeleted);
        return telegramLink;
    }

    public static TelegramLinkDto createTestTelegramLinkDto(TelegramLink telegramLink) {
        TelegramLinkDto telegramLinkDto = new TelegramLinkDto();
        telegramLinkDto.setLink(telegramLink.getToken());
        return telegramLinkDto;
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
}
