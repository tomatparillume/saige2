package com.parillume.db.repository;

import com.parillume.external.chat.model.ChatMetadata;
import java.util.List;
import javax.persistence.Cacheable;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Repository
@Cacheable(false)
public interface ChatMetadataRepository extends JpaRepository<ChatMetadata, String> { 
    @NotNull
    List<ChatMetadata> findByChatterUserId(String chatterUserId);
}