package com.tfg.librairy;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

	public void run(String texto) {

		final String username = "170jorgejir@gmail.com";
		final String password = "1717jorge";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("170jorgejir@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("jorge_gameover@hotmail.com"));
			message.setSubject("Hola soy Alberto");
			String pasar = "";
			for (int i = 0; i < 40; i++) {
				pasar += "Hola soy Alberto\n";
			}
			message.setText(pasar);
			Transport.send(message);
			System.out.println("Done");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
