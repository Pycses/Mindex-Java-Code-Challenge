package com.mindex.challenge.service.impl;


import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public ReportingStructure read(String employeeId) {

        LOG.debug("Creating reporting structure from employee id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);

        if (employee == null){
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        ReportingStructure toReturn = new ReportingStructure();
        toReturn.setEmployee(employee);
        toReturn.setNumberOfReports(getNumberReporting(employeeId));

        return toReturn;
    }

    /**
     * Recursive helper method that takes an initial employee and returns the total number of employees below them
     * @param employeeId initial employee
     * @return total number of employees below the initial
     */
    private int getNumberReporting(String employeeId) {

        Employee currentEmployee = employeeRepository.findByEmployeeId(employeeId);
        List<Employee> directReports = currentEmployee.getDirectReports();

        if (directReports == null) { return 0; }

        int toReturn = 0;

        for (Employee e : directReports){
            // Adds each reporting employee + their subordinates to the running count
            toReturn = toReturn + 1 + getNumberReporting(e.getEmployeeId());
        }

        return toReturn;

    }



}
