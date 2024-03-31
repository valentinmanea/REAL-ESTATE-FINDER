package com.realestatefinder.Real.Estate.Finder.services;

import com.realestatefinder.Real.Estate.Finder.db.RealEstateItemEntity;
import com.realestatefinder.Real.Estate.Finder.db.RealEstateItemRepo;
import com.realestatefinder.Real.Estate.Finder.mail.DummyEmailSender;
import jakarta.persistence.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbProcessingService {

    private final Logger logger = LoggerFactory.getLogger(DummyEmailSender.class);

    private final RealEstateItemRepo repo;

    public DbProcessingService(RealEstateItemRepo repo) {
        this.repo = repo;
    }

    public void process(List<RealEstateItemEntity> items) {
        List<String> urls = repo.findAll()
                .stream()
                .map(RealEstateItemEntity::getUrl)
                .toList();
        items.forEach(entity -> {
            if (!urls.contains(entity.getUrl())) {
                repo.save(entity);
                logger.info("New item inserted with url {}; description: {}", entity.getUrl(), entity.getDescription());
            }
        });

    }
}
