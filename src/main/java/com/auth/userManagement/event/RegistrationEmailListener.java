package com.auth.userManagement.event;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.auth.userManagement.entity.User;
import com.auth.userManagement.service.IUserService;

@Component
@Async
public class RegistrationEmailListener implements ApplicationListener<OnRegistrationSuccessEvent> {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private MessageSource messages;
	
	@Autowired
	private MailSender mailSender;
	
	@Override
	public void onApplicationEvent(OnRegistrationSuccessEvent event) {
		this.generateActivationTokenAndSendEmail(event);
		
	}

	private void generateActivationTokenAndSendEmail(OnRegistrationSuccessEvent event) {
		
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		//remove old activation token
		userService.removeVerificationToken(user);

		userService.createVerificationToken(user,token);
		
		String recipient = user.getEmail();
		String subject = "Registration Confirmation";
        String url 
          = event.getAppUrl() + "?token=" + token + "&username="+user.getUsername();
        String message = messages.getMessage("message.registrationSuccessConfimationLink", null, event.getLocale());
         
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message + " " +url);
        System.out.println(url);
        mailSender.send(email);
		
	}
	
	
}
