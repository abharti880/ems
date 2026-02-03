package com.ems.mapper;

import com.ems.dto.DepartmentSummary;
import com.ems.dto.EmployeeResponse;
import com.ems.entity.Department;
import com.ems.entity.Employee;

public class EmployeeMapper {

    public static EmployeeResponse toResponse(Employee employee) {

        Department dept = employee.getDepartment();

        DepartmentSummary deptSummary = null;
        if (dept != null) {
            deptSummary = new DepartmentSummary();
            deptSummary.setId(dept.getId());
            deptSummary.setName(dept.getName());
        }

        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setFullName(employee.getFullName());
        response.setEmail(employee.getEmail());
        response.setDepartment(deptSummary);
        response.setSalary(employee.getSalary());
        response.setJoiningDate(employee.getJoiningDate());
        response.setCreatedAt(employee.getCreationTimestamp());
        response.setUpdatedAt(employee.getLastUpdateTimestamp());
        response.setRole(employee.getRole());

        return response;
    }
}

