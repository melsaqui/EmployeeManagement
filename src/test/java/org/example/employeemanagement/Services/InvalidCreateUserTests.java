package org.example.employeemanagement.Services;

import org.example.employeemanagement.Entities.Employee;
import org.example.employeemanagement.Exceptions.InvalidBirthDateException;
import org.example.employeemanagement.Exceptions.InvalidSalaryException;
import org.example.employeemanagement.Exceptions.UserExistsException;
import org.example.employeemanagement.Repositories.EmployeesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * Class that primarily test the invalid outputs of adding Employees
 * */
@SpringBootTest
public class InvalidCreateUserTests {
    @Autowired
    EmployeesRepository employeesRepository;
    @Autowired
    ModifyDBServices modifyDBServices;
    private final ResourceBundle bundle = ResourceBundle.getBundle("messages-en");

    /**
     * Method tests invalid birthday input in adding an employees and checks that the correct exception and exception message is thrown
     * */
    @Test
    @Rollback
    public void testInvalidBdayAdd(){

        Exception exception = assertThrows(InvalidBirthDateException.class, () -> {
            modifyDBServices.registerUser(400,"Maria Santo", LocalDate.parse("1100-01-09"),"Accounting",34000);

        });
        assertEquals(bundle.getString("invalid.bday"), exception.getMessage());

    }
    /**
     * Method tests invalid salary input in adding an employees and checks that the correct exception and exception message is thrown
     * */
    @Test
    @Rollback
    public void testInvalidSalaryAdd(){

        Exception exception = assertThrows(InvalidSalaryException.class, () -> {
            modifyDBServices.registerUser(300,"Maria Cruz", LocalDate.parse("1990-01-09"),"Accounting",-34000);
        });
        assertEquals(bundle.getString("invalid.salary.negative"), exception.getMessage());


    }
    /**
     * Method tests that when an employee with an existing ID is added the correct exception is thrown with the correct message
     * */
    @Test
    @Rollback
    public void testEmployeeExistAdd(){
        Employee beforeEmployee = employeesRepository.findByEmployeeId(104);

        Exception exception = assertThrows(UserExistsException.class, () -> {
            modifyDBServices.registerUser(100,"Maria Santo", LocalDate.parse("1990-01-09"),"Accounting",34000);

        });
        assertEquals("User with ID 100 already exists!", exception.getMessage());

    }

    @Test
    @Rollback
    public void testCreateEmployeeBlanks(){

        Exception exception = assertThrows(Exception.class, () -> {
            modifyDBServices.registerUser(999,"Maria Santo",null,"Accounting",34000);

        });
        assertEquals(bundle.getString("invalid.emptyInput"), exception.getMessage());

    }
}
