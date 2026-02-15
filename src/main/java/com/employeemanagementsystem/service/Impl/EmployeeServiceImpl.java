package com.employeemanagementsystem.service.Impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.employeemanagementsystem.model.Employee;
import com.employeemanagementsystem.model.User;
import com.employeemanagementsystem.repository.EmployeeRepository;
import com.employeemanagementsystem.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getEmployeesByUser(User user) {
        return employeeRepository.findByUser(user);
    }

    @Override
    public void saveEmployee(Employee employee, User user) {
        if (employee.getIsActive() == null) {
            employee.setIsActive(true);
        }
        employee.setUser(user);
        employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(long id, User user) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found for id :: " + id));
        if (!emp.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: Employee does not belong to you");
        }
        return emp;
    }

    @Override
    public void deleteEmployeeById(long id, User user) {
        Employee emp = getEmployeeById(id, user);
        employeeRepository.delete(emp);
    }

    @Override
    public Page<Employee> findPaginatedByUser(int pageNo, int pageSize, String sortField, String sortDirection, User user) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return employeeRepository.findByUser(user, pageable);
    }

    @Override
    public Map<String, Long> getDashboardStatistics(User user) {
        Map<String, Long> stats = new HashMap<>();
        long totalEmployees = employeeRepository.findByUser(user).size();
        long activeEmployees = employeeRepository.countByUserAndIsActive(user, true);
        long inactiveEmployees = employeeRepository.countByUserAndIsActive(user, false);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        long thisMonth = employeeRepository.countByUserAndCreatedAtAfter(user, startOfMonth.atStartOfDay());

        stats.put("totalEmployees", totalEmployees);
        stats.put("activeEmployees", activeEmployees);
        stats.put("inactiveEmployees", inactiveEmployees);
        stats.put("thisMonth", thisMonth);
        return stats;
    }

    @Override
    public List<Employee> getRecentEmployees(User user, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        return employeeRepository.findByUser(user, pageable).getContent();
    }

    @Override
    public long getActiveEmployeesCount(User user) {
        return employeeRepository.countByUserAndIsActive(user, true);
    }

    @Override
    public long getEmployeesAddedThisMonth(User user) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        return employeeRepository.countByUserAndCreatedAtAfter(user, startOfMonth.atStartOfDay());
    }
}