package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {AccommodationMapper.class})
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
}
