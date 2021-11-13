package com.rovo.mypathalerts.controllers;

import com.rovo.mypathalerts.exceptions.CompanyExceptions;
import com.rovo.mypathalerts.exceptions.JobSeekerExceptions;
import com.rovo.mypathalerts.services.CommonService;
import com.rovo.mypathalerts.services.CompanyService;
import com.rovo.mypathalerts.services.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 30)
@RestController
public class CommonController {

	@Autowired
	CommonService commonService;

	@Autowired
	JobSeekerService jobSeekerService;

	@Autowired
	CompanyService companyService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity login(HttpSession session, @RequestBody Map<String, Object> parameterMap) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		try {
			HashMap<String, Object> response = commonService.checkCredential((String) parameterMap.get("email"),
					(String) parameterMap.get("password"));
			if((boolean) response.get("verified") == false){
				return new ResponseEntity(getErrorResponse("401", "User not verified"), responseHeaders,
						HttpStatus.UNAUTHORIZED);
			}
			if (response != null) {
				session.setAttribute("id", response.get("id"));
				session.setAttribute("type", response.get("type"));
				session.setAttribute("verified", response.get("verified"));
				return new ResponseEntity(response, responseHeaders, HttpStatus.OK);
			}
			return new ResponseEntity(getErrorResponse("401", "Bad username and password"), responseHeaders,
					HttpStatus.UNAUTHORIZED);
		} catch (Exception ex) {
			return new ResponseEntity(getErrorResponse("500", "Internal Server error"), responseHeaders,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ResponseEntity logout(HttpSession session) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		try {
			session.invalidate();
			HashMap<String, Object> result = new HashMap<>();
			result.put("result", true);
			return new ResponseEntity(result, responseHeaders, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity(getErrorResponse("500", "Failed to logout"), responseHeaders,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public ResponseEntity verifyAccount(HttpSession session ,@RequestBody Map<String, Object> parameterMap) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		try {
			if (parameterMap.get("type").toString().equals("jobseeker")) {
				jobSeekerService.verifyJobSeeker((String) parameterMap.get("id"),
						(String) parameterMap.get("verificationCode"));
				HashMap<String, Object> result = new HashMap<>();
				result.put("result", true);
				return new ResponseEntity(result, responseHeaders, HttpStatus.OK);
			} else {
				companyService.verifyCompany((String) parameterMap.get("id"),
						(String) parameterMap.get("verificationCode"));
				HashMap<String, Object> result = new HashMap<>();
				result.put("result", true);
				if(session != null){
					session.setAttribute("verified", true);
				}
				return new ResponseEntity(result, responseHeaders, HttpStatus.OK);
			}
		} catch (JobSeekerExceptions ex) {
			if (ex.getMessage().contains("Already Verified")) {
				return new ResponseEntity(getErrorResponse("200", ex.getMessage()), responseHeaders, HttpStatus.OK);
			}
			return new ResponseEntity(getErrorResponse("401", ex.getMessage()), responseHeaders,
					HttpStatus.UNAUTHORIZED);
		} catch (CompanyExceptions ex) {
			if (ex.getMessage().contains("Already Verified")) {
				return new ResponseEntity(getErrorResponse("200", ex.getMessage()), responseHeaders, HttpStatus.OK);
			}
			return new ResponseEntity(getErrorResponse("401", ex.getMessage()), responseHeaders,
					HttpStatus.UNAUTHORIZED);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return new ResponseEntity(getErrorResponse("500", "Internal server error"), responseHeaders,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public HashMap<String, String> getErrorResponse(String errorcode, String error) {
		HashMap<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("code", errorcode);
		errorMap.put("msg", error);
		return errorMap;
	}
}
