package com.employeemanagementsystem.repository;

import com.employeemanagementsystem.model.Employee;
import com.employeemanagementsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByUser(User user);

    Page<Employee> findByUser(User user, Pageable pageable);

    long countByUserAndIsActive(User user, boolean isActive);

    long countByUserAndCreatedAtAfter(User user, LocalDateTime date);

    // Optional: global queries if needed for admin
    long countByIsActive(boolean isActive);

    long countByCreatedAtAfter(LocalDateTime date);

    List<Employee> findByDepartment(String department);

    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchEmployees(@Param("keyword") String keyword);

    @Query("SELECT e FROM Employee e ORDER BY e.salary DESC")
    List<Employee> findTopEmployeesBySalary(Pageable pageable);

    @Query("SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department")
    List<Object[]> countEmployeesByDepartment();
}