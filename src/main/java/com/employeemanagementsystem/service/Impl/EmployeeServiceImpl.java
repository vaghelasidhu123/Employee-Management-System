package com.employeemanagementsystem.service.Impl;

import com.employeemanagementsystem.model.Employee;
import com.employeemanagementsystem.repository.EmployeeRepository;
import com.employeemanagementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public void saveEmployee(Employee employee) {
        if (employee.getIsActive() == null) {
            employee.setIsActive(true);
        }
        this.employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(long id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        return optional.orElseThrow(() ->
                new RuntimeException("Employee not found for id :: " + id));
    }

    @Override
    public void deleteEmployeeById(long id) {
        this.employeeRepository.deleteById(id);
    }

    @Override
    public Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.employeeRepository.findAll(pageable);
    }

    @Override
    public Map<String, Long> getDashboardStatistics() {
        Map<String, Long> stats = new HashMap<>();

        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByIsActive(true);
        long inactiveEmployees = employeeRepository.countByIsActive(false);

        // Employees added this month
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        long thisMonth = employeeRepository.countByCreatedAtAfter(
                startOfMonth.atStartOfDay());

        stats.put("totalEmployees", totalEmployees);
        stats.put("activeEmployees", activeEmployees);
        stats.put("inactiveEmployees", inactiveEmployees);
        stats.put("thisMonth", thisMonth);

        return stats;
    }

    @Override
    public List<Employee> getRecentEmployees(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        return employeeRepository.findAll(pageable).getContent();
    }

    @Override
    public long getActiveEmployeesCount() {
        return employeeRepository.countByIsActive(true);
    }

    @Override
    public long getEmployeesAddedThisMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        return employeeRepository.countByCreatedAtAfter(startOfMonth.atStartOfDay());
    }
}