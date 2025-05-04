package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingExpiredNotificationDto;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class,
        uses = {AccommodationMapper.class, AddressMapper.class, UserMapper.class}
)
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "status", expression = "java(Booking.Status.PENDING)")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "accommodation",
            source = "requestDto.accommodationId",
            qualifiedByName = "accommodationById")
    Booking toModel(CreateBookingRequestDto requestDto, User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "accommodationId", source = "accommodation.id")
    BookingDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation",
            source = "requestDto.accommodationId",
            qualifiedByName = "accommodationById")
    void updateBookingFromDto(
            CreateBookingRequestDto requestDto,
            @MappingTarget Booking booking);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "location", source = "accommodation.location",
            qualifiedByName = "mapAddressToString")
    @Mapping(target = "customer", source = "user", qualifiedByName = "mapUserToString")
    @Mapping(target = "customerEmail", source = "user.email")
    @Mapping(target = "status", source = "status")
    BookingExpiredNotificationDto toBookingExpiredNotificationDto(Booking booking);
}
