package com.employeemanagementsystem.service;

import com.employeemanagementsystem.model.Employee;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    void saveEmployee(Employee employee);

    Employee getEmployeeById(long id);
    void deleteEmployeeById(long id);
    Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

    // New methods for dashboard statistics
    Map<String, Long> getDashboardStatistics();
    List<Employee> getRecentEmployees(int count);
    long getActiveEmployeesCount();
    long getEmployeesAddedThisMonth();
}