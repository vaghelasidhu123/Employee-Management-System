package com.employeemanagementsystem.service;

import com.employeemanagementsystem.model.Employee;
import com.employeemanagementsystem.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    List<Employee> getEmployeesByUser(User user);
    void saveEmployee(Employee employee, User user);
    Employee getEmployeeById(long id, User user);
    void deleteEmployeeById(long id, User user);
    Page<Employee> findPaginatedByUser(int pageNo, int pageSize, String sortField, String sortDirection, User user);

    Map<String, Long> getDashboardStatistics(User user);
    List<Employee> getRecentEmployees(User user, int count);
    long getActiveEmployeesCount(User user);
    long getEmployeesAddedThisMonth(User user);
}