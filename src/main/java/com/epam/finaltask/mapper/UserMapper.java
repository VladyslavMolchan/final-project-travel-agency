package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    @Mapping(target = "balance", source = "balance", qualifiedByName = "bigDecimalToDouble")
    UserDTO toUserDTO(User user);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUUID")
    @Mapping(target = "role", source = "role", qualifiedByName = "stringToRole")
    @Mapping(target = "balance", source = "balance", qualifiedByName = "doubleToBigDecimal")
    User toUser(UserDTO userDTO);

    // Конвертери

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id == null ? null : id.toString();
    }

    @Named("stringToUUID")
    default UUID stringToUUID(String id) {
        return id == null || id.isEmpty() ? null : UUID.fromString(id);
    }

    @Named("roleToString")
    default String roleToString(Role role) {
        return role == null ? null : role.name();
    }

    @Named("stringToRole")
    default Role stringToRole(String role) {
        return role == null || role.isEmpty() ? null : Role.valueOf(role);
    }

    @Named("bigDecimalToDouble")
    default Double bigDecimalToDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }

    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
}
