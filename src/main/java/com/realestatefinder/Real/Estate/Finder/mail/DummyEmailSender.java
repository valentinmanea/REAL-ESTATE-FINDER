package com.realestatefinder.Real.Estate.Finder.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(value = "local")
@Component
public class DummyEmailSender implements EmailSender {

    Logger logger = LoggerFactory.getLogger(DummyEmailSender.class);

    @Override
    public void send() {
        logger.info("Email sent local!");
    }
}
