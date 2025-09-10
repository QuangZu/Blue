package com.techtack.blue.repository.market;

import com.techtack.blue.model.market.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, Long> {
    
    Optional<Industry> findByIndustryCode(String industryCode);
    
    List<Industry> findByLevel(Integer level);
    
    List<Industry> findByParentId(Long parentId);
    
    @Query("SELECT i FROM Industry i WHERE i.parent IS NULL")
    List<Industry> findRootIndustries();
    
    @Query("SELECT i FROM Industry i LEFT JOIN FETCH i.symbols WHERE i.id = :id")
    Optional<Industry> findByIdWithStocks(@Param("id") Long id);
    
    @Query("SELECT i FROM Industry i LEFT JOIN FETCH i.children WHERE i.id = :id")
    Optional<Industry> findByIdWithChildren(@Param("id") Long id);
}
