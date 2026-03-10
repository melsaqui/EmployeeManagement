package org.example.employeemanagement.Services;

import org.example.employeemanagement.Exceptions.EmployeeDoesNotExistException;
import org.example.employeemanagement.Repositories.EmployeesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * Test Class primarily tests the invalid inputs of deleting a user in the service level
 * */
@SpringBootTest
public class InvalidDeleteTests {
    @Autowired
    EmployeesRepository employeesRepository;
    @Autowired
    ModifyDBServices modifyDBServices;
    /**
     * Tests that deleting non-existent employee IDs will throw the correct exception and Exception message
     * */
    @Test
    @Rollback
    public void testNonExistentUserDelete(){
        int sizeBefore = employeesRepository.findAll().size();
        Exception exception = assertThrows(EmployeeDoesNotExistException.class, () -> {
            modifyDBServices.deleteEmployee(999);
        });
        assertEquals("Employee with ID 999 does not exist", exception.getMessage().toString());
        assertEquals(sizeBefore,employeesRepository.findAll().size());
    }

}
