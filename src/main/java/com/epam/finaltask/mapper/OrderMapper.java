package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "voucherId", source = "voucher.id", qualifiedByName = "uuidToString")
    @Mapping(target = "customerName", source = "customerName")
    @Mapping(target = "customerEmail", source = "customerEmail")
    @Mapping(target = "status", source = "status")
    OrderDTO toOrderDTO(Order order);


    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUUID")
    @Mapping(target = "voucher.id", source = "voucherId", qualifiedByName = "stringToUUID")
    @Mapping(target = "customerName", source = "customerName")
    @Mapping(target = "customerEmail", source = "customerEmail")
    @Mapping(target = "status", source = "status")
    Order toOrder(OrderDTO dto);



    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id == null ? null : id.toString();
    }

    @Named("stringToUUID")
    default UUID stringToUUID(String id) {
        return id == null || id.isEmpty() ? null : UUID.fromString(id);
    }
}
