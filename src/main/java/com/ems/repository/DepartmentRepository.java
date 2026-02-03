
package com.ems.repository;

import com.ems.dto.DepartmentSummary;
import com.ems.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("select new com.ems.dto.DepartmentSummary(d.id, d.name) from Department d")
    List<DepartmentSummary> findAllSummaries();

    boolean existsByNameIgnoreCase(String name);
}
