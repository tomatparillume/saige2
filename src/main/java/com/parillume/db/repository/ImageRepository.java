package com.parillume.db.repository;

import com.parillume.print.display.DBImage;
import com.parillume.util.model.ImageType;
import javax.persistence.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Repository
@Cacheable(false)
public interface ImageRepository extends JpaRepository<DBImage, Long> {
    DBImage findByCompanyIdAndImageType(String companyId, ImageType imageType); 
}