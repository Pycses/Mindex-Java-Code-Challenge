package com.mindex.challenge;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeApplicationTests {

	private String reportingStructureUrl;
	private String compensationCreateUrl;
	private String compensationGetUrl;

	@Autowired
	private EmployeeService employeeService;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setup() {
		reportingStructureUrl = "http://localhost:" + port + "/reportingstructure/{id}";
		compensationCreateUrl = "http://localhost:" + port + "/compensation";
		compensationGetUrl = "http://localhost:" + port + "/compensation/{id}";
	}

	@Test
	public void testRetrieveReportingStructure() {

		// Employee ID for John Lennon in bootstrap data
		String testEmployeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

		// Should give us the John Lennon employee object
		Employee testEmployee = employeeService.read(testEmployeeId);

		ReportingStructure testReportingStructure = new ReportingStructure();
		testReportingStructure.setEmployee(testEmployee);
		testReportingStructure.setNumberOfReports(4); // We know from the test data that John Lennon has 4 subordinates

		// Request ReportingStructure to check
		ReportingStructure createdReportingStructure =
				restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployeeId).getBody();

		assertNotNull(createdReportingStructure);
		assertReportingStructureEquivalence(testReportingStructure, createdReportingStructure);

	}

	@Test
	public void testCompensationCreateRead() {

		String testEmployeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
		double testSalary = 10000.00;
		String testEffectiveDate = "2022-01-01";

		Employee testEmployee = employeeService.read(testEmployeeId);

		Compensation testCompensation = new Compensation();
		testCompensation.setEmployee(testEmployee);
		testCompensation.setSalary(testSalary);
		testCompensation.setEffectiveDate(testEffectiveDate);

		// Attempt Compensation creation

		Compensation createdCompensation =
				restTemplate.postForEntity(compensationCreateUrl, testCompensation, Compensation.class ).getBody();

		assertNotNull(createdCompensation);
		assertCompensationEquivalence(testCompensation, createdCompensation);

		// Attempt to read the Compensation that we created earlier

		Compensation readCompensation =
				restTemplate.getForEntity(compensationGetUrl, Compensation.class, testEmployeeId).getBody();

		assertNotNull(readCompensation);
		assertCompensationEquivalence(testCompensation, readCompensation);

	}


	private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
		assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
		assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
	}

	private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
		assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
		assertEquals(expected.getSalary(), actual.getSalary(), 0.01); // Compares salary to within the cent
		assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
	}


	private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
		assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
		assertEquals(expected.getFirstName(), actual.getFirstName());
		assertEquals(expected.getLastName(), actual.getLastName());
		assertEquals(expected.getDepartment(), actual.getDepartment());
		assertEquals(expected.getPosition(), actual.getPosition());
	}



}
