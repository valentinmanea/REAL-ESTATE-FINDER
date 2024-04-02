package com.realestatefinder.Real.Estate.Finder;

import com.realestatefinder.Real.Estate.Finder.db.RealEstateItemEntity;
import com.realestatefinder.Real.Estate.Finder.mail.EmailSender;
import com.realestatefinder.Real.Estate.Finder.services.DbProcessingService;
import com.realestatefinder.Real.Estate.Finder.steps.RomimoStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RealEstateFinderApplication implements CommandLineRunner {
	private final Logger logger = LoggerFactory.getLogger(RealEstateFinderApplication.class);

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
//		runFlowTimes(100);
//		dbProcessingService.process(romimoStep.getRealEstateItems());
	}

	private void runFlowTimes(int max) {
		int correctFlows = 0;
		int failedFlows = 0;
		for(int i = 0; i < max;i++){
			try{
				List<RealEstateItemEntity> realEstateItems = romimoStep.getRealEstateItems();
				logger.info("realEstateItems.size(): {}", realEstateItems.size());
				if(realEstateItems.size() == 24){
					correctFlows++;
					logger.info("correctFlow: {}", correctFlows);
				}else{
					logger.info("failedFlows: {}", failedFlows);
					failedFlows++;
				}
				romimoStep.close();
				Thread.sleep(500);
			}catch (Exception e){
				failedFlows++;
				logger.error("Exception", e);
			}
		}
		logger.info("Workflows correct: " + correctFlows);
		logger.info("Workflows failed: " + failedFlows);
	}
}
