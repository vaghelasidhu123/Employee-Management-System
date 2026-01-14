package com.employeemanagementsystem.repository;

import com.employeemanagementsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Custom queries for statistics
    long countByIsActive(boolean isActive);

    long countByCreatedAtAfter(LocalDateTime date);

    // Find employees by department
    List<Employee> findByDepartment(String department);

    // Search employees by name or email
    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchEmployees(@Param("keyword") String keyword);

    // Get employees with highest salary
    @Query("SELECT e FROM Employee e ORDER BY e.salary DESC")
    List<Employee> findTopEmployeesBySalary(org.springframework.data.domain.Pageable pageable);

    // Count employees by department
    @Query("SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department")
    List<Object[]> countEmployeesByDepartment();
}