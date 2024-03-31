package com.realestatefinder.Real.Estate.Finder;

import com.realestatefinder.Real.Estate.Finder.mail.EmailSender;
import com.realestatefinder.Real.Estate.Finder.services.DbProcessingService;
import com.realestatefinder.Real.Estate.Finder.steps.RomimoStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RealEstateFinderApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RealEstateFinderApplication.class, args);
	}

	@Autowired
	private RomimoStep romimoStep;

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private DbProcessingService dbProcessingService;

	@Override
	public void run(String... args) {
		emailSender.send();
		dbProcessingService.process(romimoStep.getRealEstateItems());
	}
}
