package org.example.employeemanagement.Services;
import org.aspectj.lang.annotation.Before;
import org.example.employeemanagement.Entities.Accounting;
import org.example.employeemanagement.Entities.Employee;
import org.example.employeemanagement.Entities.IT;
import org.example.employeemanagement.Repositories.AccountingRepository;
import org.example.employeemanagement.Repositories.EmployeesRepository;
import org.example.employeemanagement.Repositories.ITsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseTests {
    @Mock
    EmployeesRepository employeesRepository;
    @Mock
    AccountingRepository accountingRepository;
    @Mock
    ITsRepository iTsRepository;
    @InjectMocks
    ModifyDBServices modifyDBServices;

    Employee employeeTest;
    @BeforeEach
    public void setup(){
        employeeTest=new Employee("A name",LocalDate.now().minusYears(30));
    }
    /**
     * Tests Positive outcomes of adding user
     * */
    @Test
    public void testEverythingIsOkay() throws Exception{
        Accounting acctEmployee =new Accounting(employeeTest);
        when(accountingRepository.save(any())).thenReturn(acctEmployee);

        Employee newEmployee= modifyDBServices.registerUser(100,"A name", employeeTest.getDateOfBirth(),"ACCOUNTING",30000);
        assertNotNull(newEmployee);
        assertEquals(100, newEmployee.getEmployeeId());
        assertEquals("ACCOUNTING",newEmployee.getDepartment());
        assertEquals("A name", newEmployee.getName());
        assertEquals(30000,newEmployee.getSalary());
        assertEquals(employeeTest.getDateOfBirth(),newEmployee.getDateOfBirth());
        verify(accountingRepository,times(1)).save(any());

    }
    @Test
    public void testEditingIsOkay() throws Exception{
        employeeTest.setEmployeeId(100);
        IT itEmployee =new IT(employeeTest);
        LocalDate newBday = LocalDate.now().minusYears(25);
        when(employeesRepository.findByEmployeeId(100)).thenReturn(employeeTest);
        when(iTsRepository.save(any())).thenReturn(itEmployee);
        Employee newEmployee= modifyDBServices.updateEmployees(100,"Juan Cruz", newBday,"IT",25000);
        assertNotNull(newEmployee);

        assertEquals(100, newEmployee.getEmployeeId());
        assertEquals("IT",newEmployee.getDepartment());
        assertEquals("Juan Cruz", newEmployee.getName());
        assertEquals(25000,newEmployee.getSalary());
        assertEquals(newBday,newEmployee.getDateOfBirth());
        verify(iTsRepository,times(1)).save(any());
    }
    @Test
    public void testDeleteIsOkay() throws Exception{
        employeeTest.setEmployeeId(100);
        when(employeesRepository.findByEmployeeId(100)).thenReturn(employeeTest);
        Employee newEmployee= modifyDBServices.deleteEmployee(100);

        assertNotNull(newEmployee);
        verify(employeesRepository,times(1)).deleteByEmployeeId(100);

    }




}
