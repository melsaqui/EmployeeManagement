package org.example.employeemanagement.Services;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import jakarta.transaction.Transactional;
import org.example.employeemanagement.DTOs.EmployeeDTO;
import org.example.employeemanagement.Entities.Accounting;
import org.example.employeemanagement.Entities.Employee;
import org.example.employeemanagement.Entities.HR;
import org.example.employeemanagement.Entities.IT;
import org.example.employeemanagement.Exceptions.*;
import org.example.employeemanagement.Repositories.AccountingRepository;
import org.example.employeemanagement.Repositories.EmployeesRepository;
import org.example.employeemanagement.Repositories.HRsRepository;
import org.example.employeemanagement.Repositories.ITsRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Service class that primarily handles the modification of the database
 * */
@Service
public class ModifyDBServices {
    private final EmployeesRepository employeesRepository;
    private final HRsRepository hrRepository;
    private final ITsRepository itRepository;
    private final AccountingRepository accountingRepository;

    private final ResourceBundle bundle = ResourceBundle.getBundle("messages-en");

    public ModifyDBServices(EmployeesRepository employeesRepository, HRsRepository hrRepository,
                            ITsRepository itRepository, AccountingRepository accountingRepository){

        this.employeesRepository=employeesRepository;
        this.hrRepository=hrRepository;
        this.itRepository=itRepository;
        this.accountingRepository =accountingRepository;
    }

    /**
     * Method used to register or create new employees
     * @param id the Employee ID of the new employee
     * @param name the name of the new employee
     * @param dateOfBirth the date of birth of the new employee
     * @param department the department of the new employee
     * @param salary the salary of the new employee
     * */
    public Employee registerUser(int id, String name, LocalDate dateOfBirth, String department, double salary) throws Exception{
        department= department.toUpperCase();
        if (name.isEmpty() || department.isEmpty() || dateOfBirth ==null){
            String message = bundle.getString("invalid.emptyInput");
            throw new Exception(message);
        }
        if(employeesRepository.findByEmployeeId(id)==null){
            if(isValidBirthday(dateOfBirth)){
                if (salary>0){
                    Employee employee=new Employee();
                    employee.setName(name);
                    employee.setDateOfBirth(dateOfBirth);
                    employee.setSalary(salary);
                    employee.setEmployeeId(id);
                    if (department.equalsIgnoreCase("IT")){
                        employee.setDepartment(department);
                        this.itRepository.save(new IT(employee));
                        return employee;
                    }else if (department.equalsIgnoreCase("HR")){
                        employee.setDepartment(department);
                        this.hrRepository.save(new HR(employee));
                        return employee;
                    }else if (department.equalsIgnoreCase("accounting")){
                        employee.setDepartment(department);
                        this.accountingRepository.save(new Accounting(employee));
                        return employee;
                    }
                    else{
                        String message = bundle.getString("invalid.department");
                        throw new InvalidDepartmentException(message);
                    }

                }
                else throw new InvalidSalaryException(bundle.getString("invalid.salary.negative"));
            }
            else{
                String message = bundle.getString("invalid.bday");
                throw new InvalidBirthDateException(message);
            }
        }
        else{
            throw new UserExistsException(bundle.getString("invalid.user.exist.id"),id);
        }
    }
    /**
     * Method used to edit the details of an employee
     * @param id the ID of the employee that will be edited
     * @param dateOfBirth the new date of birth the employee will have
     * @param name the name of the employee will be edited to
     * @param department the department the user will have after editing
     * @param salary the new salary of the employee being edited
     * */
    @Modifying
    public Employee updateEmployees(int id, String name, LocalDate dateOfBirth, String department, double salary) throws Exception {
        department= department.toUpperCase();
        if (name.isEmpty() || department.isEmpty() || dateOfBirth ==null)
            throw new Exception(bundle.getString("invalid.emptyInput"));
        Employee employee = employeesRepository.findByEmployeeId(id);
        if (employee != null){
            if(isValidBirthday(dateOfBirth)){
                if (salary > 0){
                    employee.setName(name);
                    employee.setDateOfBirth(dateOfBirth);
                    employee.setSalary(salary);
                    if(department.equalsIgnoreCase(employee.getDepartment())){
                        if (department.equalsIgnoreCase("IT")){
                            this.itRepository.save((IT)employee);
                        }else if (department.equalsIgnoreCase("HR")){
                            this.hrRepository.save((HR)employee);
                        }else if (department.equalsIgnoreCase("accounting")){
                            this.accountingRepository.save((Accounting)employee);
                        }
                        return employee;
                    }else {
                        if(employee instanceof IT) {
                            this.itRepository.delete((IT) employee);
                        }
                        if(employee instanceof HR){
                            this.hrRepository.delete((HR)employee);
                        }
                        if(employee instanceof Accounting) {
                            this.accountingRepository.delete((Accounting) employee);
                        }
                        if (department.equalsIgnoreCase("IT")) {
                            employee.setDepartment(department);
                            this.itRepository.save(new IT(employee));
                            return employee;
                        } else if (department.equalsIgnoreCase("HR")) {
                            employee.setDepartment(department);
                            this.hrRepository.save(new HR(employee));
                            return employee;
                        } else if (department.equalsIgnoreCase("ACCOUNTING")) {
                            employee.setDepartment(department);
                            this.accountingRepository.save(new Accounting(employee));
                            return employee;
                        } else{
                            String message = bundle.getString("invalid.department");
                            throw new InvalidDepartmentException(message);
                        }

                    }
                }else {
                    String message = bundle.getString("invalid.salary.negative");
                    throw new InvalidSalaryException(message);
                }
            }
            else {

                String message = bundle.getString("invalid.bday");
                throw new InvalidBirthDateException(message);
            }

        }else
            throw new EmployeeDoesNotExistException(bundle.getString("invalid.employee.notExist"),id);
    }
    /**
     * Method used to delete an employee from the database
     * @param employeeId the ID of the employee to be deleted
     * */
    @Modifying
    @Transactional
    public Employee deleteEmployee(int employeeId){
        Employee employee=findByEmployeeId(employeeId);
        if(employee!=null) {
            employeesRepository.deleteByEmployeeId(employeeId);
            return employee;
        }
        else throw new EmployeeDoesNotExistException(bundle.getString("invalid.employee.notExist"),employeeId);

    }
    /**
     * Method used to retrieve Employee from repositorybusing their employee ID
     * @param id the employee ID of the employee being retrieved
     * */
    public Employee findByEmployeeId(int id){
        Employee employee =employeesRepository.findByEmployeeId(id);
        return employee;

    }

    /**
     * Method used to check if date of birth is valid
     * */
    private boolean isValidBirthday(LocalDate date){
        LocalDate currentDate =LocalDate.now();
        LocalDate maxAgeDate = currentDate.minusYears(130); //limit to age


        if (date.isAfter(currentDate)) {
            return false; // Cannot be born in the future
        }else if(date.isBefore(maxAgeDate)){
            return false;
        }
        return true;
    }
}
