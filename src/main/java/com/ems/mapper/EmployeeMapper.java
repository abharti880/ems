package com.ems.mapper;

import com.ems.dto.*;
import com.ems.entity.Employee;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "department", source = "department")
    @Mapping(target = "createdAt", source = "creationTimestamp")
    @Mapping(target = "updatedAt", source = "lastUpdateTimestamp")
    EmployeeAdminResponse toAdminResponse(Employee employee);

    EmployeeSelfResponse toSelfResponse(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    @Mapping(target = "lastUpdateTimestamp", ignore = true)
    @Mapping(target = "department", ignore = true)
    Employee toEntity(EmployeeCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    @Mapping(target = "lastUpdateTimestamp", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "joiningDate", ignore = true)
    void updateEntityFromRequest(EmployeeUpdateRequest request, @MappingTarget Employee employee);
}
