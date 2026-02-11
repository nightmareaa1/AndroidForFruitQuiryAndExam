package com.example.userauth.repository;

import com.example.userauth.entity.FruitData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FruitDataRepository extends JpaRepository<FruitData, Long> {
    Optional<FruitData> findByFruitNameAndDataType(String fruitName, String dataType);
    
    List<FruitData> findByDataType(String dataType);
    
    List<FruitData> findByFruitName(String fruitName);
    
    void deleteByFruitNameAndDataType(String fruitName, String dataType);
    
    boolean existsByFruitNameAndDataType(String fruitName, String dataType);
}
