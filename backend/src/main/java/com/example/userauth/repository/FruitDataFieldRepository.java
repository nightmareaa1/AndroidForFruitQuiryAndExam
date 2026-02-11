package com.example.userauth.repository;

import com.example.userauth.entity.FruitDataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FruitDataFieldRepository extends JpaRepository<FruitDataField, Long> {
    List<FruitDataField> findByFieldTypeAndIsActiveTrueOrderByDisplayOrder(String fieldType);

    boolean existsByFieldTypeAndFieldName(String fieldType, String fieldName);

    Optional<FruitDataField> findByFieldTypeAndFieldName(String fieldType, String fieldName);
}
