package com.auth.userManagement.api;

import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.auth.userManagement.dto.UserDTO;
import com.auth.userManagement.entity.User;
import com.auth.userManagement.entity.VerificationToken;
import com.auth.userManagement.event.OnRegistrationSuccessEvent;
import com.auth.userManagement.service.IUserService;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

	@Autowired
	private IUserService service;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Autowired
	private MessageSource messages;
	
	private Logger logger = Logger.getLogger(getClass().getName());

	@PostMapping("/registration")
	public ResponseEntity<String> registerNewUser(@ModelAttribute UserDTO userDto, WebRequest request) {
		
		String userName = userDto.getUserName();

		User registeredUser = service.findByUsername(userName);
		if(registeredUser!=null) {
			logger.info("There is already an account with this username: " + userName);
			return new ResponseEntity<String>("Registration Failed - There is already an account with username - " + userName, HttpStatus.CONFLICT);
		}

		registeredUser = service.registerUser(User.fromDTO(userDto));
		
		try {
			String appUrl = (request.getContextPath() == null | request.getContextPath().isEmpty() ? "http://localhost:8083" : request.getContextPath())+"/api/v1/account/confirmRegistration";
			eventPublisher.publishEvent(new OnRegistrationSuccessEvent(registeredUser, request.getLocale(),appUrl));
		} catch(Exception re) {
			re.printStackTrace();
			logger.log(Level.SEVERE, re.getMessage(), re);
		}
		return new ResponseEntity<String>("User Registration Successful", HttpStatus.OK);
	}
	
	@GetMapping("/confirmRegistration")
	public ResponseEntity<String> confirmRegistration(WebRequest request, @RequestParam("token") String token) {
		
		Locale locale=request.getLocale();
		VerificationToken verificationToken = service.getVerificationToken(token);
		if(verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
		
		User user = verificationToken.getUser();
		Calendar calendar = Calendar.getInstance();
		if((verificationToken.getExpiryDate().getTime()-calendar.getTime().getTime())<=0) {
			String message = messages.getMessage("auth.message.expired", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
		
		user.setEnabled(true);
		service.enableRegisteredUser(user);
		service.removeVerificationToken(user);
		return new ResponseEntity<String>("Account Activation Successful", HttpStatus.OK);
	}
	
	@PostMapping("/changePassword")
	public ResponseEntity<String> changePassword(@ModelAttribute UserDTO userDto) {
		
		String userName = userDto.getUserName();

		User registeredUser = service.findByUsername(userName);
		if(registeredUser==null) {
			logger.info("User not found with username : " + userName);
			return new ResponseEntity<String>("Failed - User not found with username - " + userName, HttpStatus.CONFLICT);
		}
		registeredUser.setPassword(userDto.getPassword());
		service.changePassword(registeredUser);
		
		return new ResponseEntity<String>("Password Change Successful", HttpStatus.OK);
	}
	
	@GetMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(WebRequest request, @RequestParam("username") String username) {
		

		User registeredUser = service.findByUsername(username);
		if(registeredUser==null) {
			logger.info("User not found with username : " + username);
			return new ResponseEntity<String>("Failed - User not found with username - " + username, HttpStatus.CONFLICT);
		}

		try {
			String appUrl = (request.getContextPath() == null | request.getContextPath().isEmpty() ? "http://localhost:8083" : request.getContextPath())+"/api/v1/account/verifyPasswordUpdateToken";
			
			eventPublisher.publishEvent(new OnRegistrationSuccessEvent(registeredUser, request.getLocale(),appUrl));
		} catch(Exception re) {
			re.printStackTrace();
			logger.log(Level.SEVERE, re.getMessage(), re);
		}
		return new ResponseEntity<String>("User password reset link sent to registered email id", HttpStatus.OK);
	}
	
	@GetMapping("/verifyPasswordUpdateToken")
	public ResponseEntity<String> verifyPasswordUpdateToken(WebRequest request, @RequestParam("token") String token) {
		
		Locale locale=request.getLocale();
		VerificationToken verificationToken = service.getVerificationToken(token);
		if(verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
		
		Calendar calendar = Calendar.getInstance();
		if((verificationToken.getExpiryDate().getTime()-calendar.getTime().getTime())<=0) {
			String message = messages.getMessage("auth.message.expired", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
		service.removeVerificationToken(verificationToken.getUser());
		return new ResponseEntity<String>("Password update token verification successful", HttpStatus.OK);
	}
	
	
	@GetMapping("/retryAccountActivation")
	public ResponseEntity<String> retryAccountActivation(@ModelAttribute UserDTO userDto, WebRequest request) {
		
		String userName = userDto.getUserName();

		User registeredUser = service.findByUsername(userName);
		if(registeredUser==null) {
			logger.info("User not found with username : " + userName);
			return new ResponseEntity<String>("Failed - User not found with username - " + userName, HttpStatus.CONFLICT);
		}

		try {
			String appUrl = (request.getContextPath() == null | request.getContextPath().isEmpty() ? "http://localhost:8083" : request.getContextPath())+"/api/v1/account/confirmRegistration";
			
			eventPublisher.publishEvent(new OnRegistrationSuccessEvent(registeredUser, request.getLocale(), appUrl));
		} catch(Exception re) {
			re.printStackTrace();
			logger.log(Level.SEVERE, re.getMessage(), re);
		}
		return new ResponseEntity<String>("User Activation Link Resent", HttpStatus.OK);
	}


}
