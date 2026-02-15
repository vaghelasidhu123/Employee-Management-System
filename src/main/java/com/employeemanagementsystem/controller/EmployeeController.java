package com.employeemanagementsystem.controller;

import com.employeemanagementsystem.model.Employee;
import com.employeemanagementsystem.model.User;
import com.employeemanagementsystem.service.EmployeeService;
import com.employeemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal User currentUser, Model model) {
        Map<String, Long> stats = employeeService.getDashboardStatistics(currentUser);
        model.addAttribute("totalEmployees", stats.get("totalEmployees"));
        model.addAttribute("activeEmployees", stats.get("activeEmployees"));
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("username", currentUser.getUsername());
        return "dashboard";
    }

    @GetMapping("/employees")
    public String viewEmployeeList(@AuthenticationPrincipal User currentUser, Model model) {
        return findPaginated(1, "firstName", "asc", model, currentUser);
    }

    @GetMapping("/showNewEmployeeForm")
    public String showNewEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add New Employee");
        return "new_employee";
    }

    @PostMapping("/saveEmployee")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult result,
                               @AuthenticationPrincipal User currentUser,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", employee.getId() != null ? "Update Employee" : "Add New Employee");
            return employee.getId() != null ? "update_employee" : "new_employee";
        }
        employeeService.saveEmployee(employee, currentUser);
        return "redirect:/employees";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable long id,
                                    @AuthenticationPrincipal User currentUser,
                                    Model model) {
        Employee employee = employeeService.getEmployeeById(id, currentUser);
        model.addAttribute("employee", employee);
        model.addAttribute("pageTitle", "Update Employee");
        return "update_employee";
    }

    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable long id,
                                 @AuthenticationPrincipal User currentUser,
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployeeById(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting employee: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    @GetMapping("/viewEmployee/{id}")
    public String viewEmployeeProfile(@PathVariable long id,
                                      @AuthenticationPrincipal User currentUser,
                                      Model model) {
        Employee employee = employeeService.getEmployeeById(id, currentUser);
        model.addAttribute("employee", employee);
        model.addAttribute("pageTitle", employee.getFirstName() + " " + employee.getLastName() + " - Profile");
        return "employee_profile";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model,
                                @AuthenticationPrincipal User currentUser) {
        int pageSize = 10;
        Page<Employee> page = employeeService.findPaginatedByUser(pageNo, pageSize, sortField, sortDir, currentUser);
        List<Employee> listEmployees = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listEmployees", listEmployees);
        model.addAttribute("pageTitle", "Employee Directory");
        return "index";
    }
}