package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VoucherMapper {


    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "userId", source = "user.id", qualifiedByName = "uuidToString")
    @Mapping(target = "tourType", source = "tourType", qualifiedByName = "enumToString")
    @Mapping(target = "transferType", source = "transferType", qualifiedByName = "enumToString")
    @Mapping(target = "hotelType", source = "hotelType", qualifiedByName = "enumToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "enumToString")
    VoucherDTO toVoucherDTO(Voucher voucher);


    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUuid")
    @Mapping(target = "user.id", source = "userId", qualifiedByName = "stringToUuid")
    @Mapping(target = "tourType", source = "tourType", qualifiedByName = "stringToEnumTourType")
    @Mapping(target = "transferType", source = "transferType", qualifiedByName = "stringToEnumTransferType")
    @Mapping(target = "hotelType", source = "hotelType", qualifiedByName = "stringToEnumHotelType")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToEnumVoucherStatus")
    Voucher toVoucher(VoucherDTO dto);



    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id == null ? null : id.toString();
    }

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        try {
            return (id == null || id.isBlank()) ? null : UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("enumToString")
    default String enumToString(Enum<?> e) {
        return e == null ? null : e.name();
    }

    @Named("stringToEnumTourType")
    default TourType stringToEnumTourType(String s) {
        return s == null ? null : TourType.valueOf(s);
    }

    @Named("stringToEnumTransferType")
    default TransferType stringToEnumTransferType(String s) {
        return s == null ? null : TransferType.valueOf(s);
    }

    @Named("stringToEnumHotelType")
    default HotelType stringToEnumHotelType(String s) {
        return s == null ? null : HotelType.valueOf(s);
    }

    @Named("stringToEnumVoucherStatus")
    default VoucherStatus stringToEnumVoucherStatus(String s) {
        return s == null ? null : VoucherStatus.valueOf(s);
    }
}
