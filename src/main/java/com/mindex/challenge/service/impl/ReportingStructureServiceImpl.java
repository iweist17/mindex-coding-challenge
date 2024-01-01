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

    private int globalCount = 0;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Override
    public ReportingStructure read(String id) {
        LOG.info("Creating employee with id [{}]", id);

        ReportingStructure toReturn = new ReportingStructure();

        //Get employee object
        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        //Get intial list of direct reports
        List<Employee> directReport = employee.getDirectReports();

        //Use recursive message to count all direct reports, otherwise number is 0
        if(directReport != null)
            toReturn.setNumberOfReports(checkAndCountDirectReports(directReport));
        else
            toReturn.setNumberOfReports(0);

        //Set Employee field
        toReturn.setEmployee(employee);

        //Reset counter
        globalCount = 0;
        LOG.info("Check Result [{}]", toReturn.getNumberOfReports());

        return toReturn;
    }

    public int checkAndCountDirectReports(List<Employee> emp){

        //Logic to check if there are any direct reports in list
        int directReportLen = emp.size();
        if(directReportLen > 0) {
            //For each direct report add to globalCount and check if that employee has a direct report list, if so re-call method
            for(int i=0; i<directReportLen; i++){
                globalCount++;
                Employee directReport = emp.get(i);
                Employee directReportFullObject = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
                List<Employee> directReportList = directReportFullObject.getDirectReports();

                if(directReportList != null)
                    checkAndCountDirectReports(directReportFullObject.getDirectReports());
            }
            return globalCount;
        } else {
            return globalCount;
        }
    }
}
