package com.auth.userManagement.api;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.auth.userManagement.dto.UserDTO;
import com.auth.userManagement.entity.User;
import com.auth.userManagement.entity.VerificationToken;
import com.auth.userManagement.event.OnRegistrationSuccessEvent;
import com.auth.userManagement.service.IUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/v1/account")
@Api(value="usermanagement", description="Operations related to user management")
public class AccountController {

	@Autowired
	private IUserService service;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private MessageSource messages;

	private Logger logger = Logger.getLogger(getClass().getName());
	@ApiOperation(value = "Register a user")
	@PostMapping("/registration")
	public ResponseEntity<String> registerNewUser(@RequestBody UserDTO userDto, BindingResult result,
			WebRequest request) {

		String userName = userDto.getUserName();

		User registeredUser = service.findByUsername(userName);
		if (registeredUser != null) {
			logger.info("There is already an account with this username: " + userName);
			return new ResponseEntity<String>(
					"Registration Failed - There is already an account with username - " + userName,
					HttpStatus.CONFLICT);
		}

		registeredUser = service.registerUser(User.fromDTO(userDto));

		try {
			String appUrl = (request.getContextPath() == null | request.getContextPath().isEmpty()
					? "http://localhost:8083"
					: request.getContextPath()) + "/api/v1/account/confirmRegistration";
			eventPublisher.publishEvent(new OnRegistrationSuccessEvent(registeredUser, request.getLocale(), appUrl));
		} catch (Exception re) {
			re.printStackTrace();
			logger.log(Level.SEVERE, re.getMessage(), re);
		}
		return new ResponseEntity<String>("User Registration Successful", HttpStatus.OK);
	}

	@GetMapping("/confirmRegistration")
	public ResponseEntity<String> confirmRegistration(WebRequest request, @RequestParam("token") String token) {

		Locale locale = request.getLocale();
		VerificationToken verificationToken = service.getVerificationToken(token);
		if (verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

		User user = verificationToken.getUser();
		Calendar calendar = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0) {
			String message = messages.getMessage("auth.message.expired", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

		user.setEnabled(true);
		user.setIsActive(true);
		service.enableRegisteredUser(user);
		service.removeVerificationToken(user);
		return new ResponseEntity<String>("Account Activation Successful", HttpStatus.OK);
	}

	@ApiOperation(value = "Reset password of a user")
	@PostMapping("/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody UserDTO userDto) {

		String userName = userDto.getUserName();

		User registeredUser = service.findByUsername(userName);
		if (registeredUser == null) {
			logger.info("User not found with username : " + userName);
			return new ResponseEntity<String>("Failed - User not found with username - " + userName,
					HttpStatus.CONFLICT);
		}
		registeredUser.setPassword(userDto.getPassword());
		service.changePassword(registeredUser);

		return new ResponseEntity<String>("Password Change Successful", HttpStatus.OK);
	}

	@ApiOperation(value = "Forgot password of a user")
	@GetMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(WebRequest request, @RequestParam("username") String username) {

		User registeredUser = service.findByUsername(username);
		if (registeredUser == null) {
			logger.info("User not found with username : " + username);
			return new ResponseEntity<String>("Failed - User not found with username - " + username,
					HttpStatus.CONFLICT);
		}

		try {
			String appUrl = (request.getContextPath() == null | request.getContextPath().isEmpty()
					? "http://localhost:8080"
					: request.getContextPath()) + "/account/reset-password";

			eventPublisher.publishEvent(new OnRegistrationSuccessEvent(registeredUser, request.getLocale(), appUrl));
		} catch (Exception re) {
			re.printStackTrace();
			logger.log(Level.SEVERE, re.getMessage(), re);
		}
		return new ResponseEntity<String>("User password reset link sent to registered email id", HttpStatus.OK);
	}

	@GetMapping("/verifyPasswordUpdateToken")
	public ResponseEntity<String> verifyPasswordUpdateToken(WebRequest request, @RequestParam("token") String token) {

		Locale locale = request.getLocale();
		VerificationToken verificationToken = service.getVerificationToken(token);
		if (verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

		Calendar calendar = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0) {
			String message = messages.getMessage("auth.message.expired", null, locale);
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
		service.removeVerificationToken(verificationToken.getUser());
		return new ResponseEntity<String>("Password update token verification successful", HttpStatus.OK);
	}

	
	@PostMapping("/retryAccountActivation")
	public ResponseEntity<String> retryAccountActivation(@RequestBody UserDTO userDto, WebRequest request) {

		String userName = userDto.getUserName();

		User registeredUser = service.findByUsername(userName);
		if (registeredUser == null) {
			logger.info("User not found with username : " + userName);
			return new ResponseEntity<String>("Failed - User not found with username - " + userName,
					HttpStatus.CONFLICT);
		}

		try {
			String appUrl = (request.getContextPath() == null | request.getContextPath().isEmpty()
					? "http://localhost:8083"
					: request.getContextPath()) + "/api/v1/account/confirmRegistration";

			eventPublisher.publishEvent(new OnRegistrationSuccessEvent(registeredUser, request.getLocale(), appUrl));
		} catch (Exception re) {
			re.printStackTrace();
			logger.log(Level.SEVERE, re.getMessage(), re);
		}
		return new ResponseEntity<String>("User Activation Link Resent", HttpStatus.OK);
	}
	
	@ApiOperation(value = "View a list of available users",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })

	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(WebRequest request) {
		List<User> users = service.getUsers();
		return new ResponseEntity<Object>(users, HttpStatus.OK);
	}
	@ApiOperation(value = "Search a user with an username",response = User.class)
	@GetMapping("/usersDetails")
	public ResponseEntity<Object> getUserDetails(WebRequest request, @RequestParam("username") String username) {
		User registeredUser = service.findByUsername(username);
		return new ResponseEntity<Object>(registeredUser, HttpStatus.OK);
	}

	@ApiOperation(value = "Update a user")
	@PutMapping("/update")
	public ResponseEntity<Object> updateUser(@RequestBody UserDTO userDto, BindingResult result, WebRequest request) {

		String userName = userDto.getUserName();

		User registeredUser = service.findByUsername(userName);
		if (registeredUser == null) {
			logger.info("User not found with username : " + userName);
			return new ResponseEntity<Object>("Failed - User not found with username - " + userName,
					HttpStatus.CONFLICT);
		}
		userDto.setPassword(registeredUser.getPassword());
		User user = User.fromDTO(userDto);
		user.setId(registeredUser.getId());
		user.setEnabled(true);
		service.updateUser(user);

		return new ResponseEntity<Object>(user, HttpStatus.OK);
	}

	@ApiOperation(value = "Deactivate a user")
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteUser(WebRequest request, @RequestParam("username") String userName, @RequestParam("token") String token) {

		User registeredUser = service.findByUsername(userName);
		if (registeredUser == null) {
			logger.info("User not found with username : " + userName);
			return new ResponseEntity<String>("Failed - User not found with username - " + userName,
					HttpStatus.CONFLICT);
		}
		registeredUser.setIsActive(false);
		registeredUser.setEnabled(false);
		service.deActivateRegisteredUser(registeredUser);

		return new ResponseEntity<String>("User Delete Successful", HttpStatus.OK);
	}

}
