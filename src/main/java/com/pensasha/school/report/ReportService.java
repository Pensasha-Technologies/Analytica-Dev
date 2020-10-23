package com.pensasha.school.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.pensasha.school.student.Student;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportService {

	@Autowired
	private UserService userService;

	public JasperPrint exportReport() throws FileNotFoundException, JRException {

		List<User> users = userService.findAllUsers();

		File file = ResourceUtils.getFile("classpath:users.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("createdBy", "Sobunge");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		return jasperPrint;

	}
	
	public JasperPrint exportClassList(List<Student> students) throws FileNotFoundException, JRException {
		
		File file = ResourceUtils.getFile("classpath:classList.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("createdBy", "Sobunge");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		
		return jasperPrint;
	}

}
