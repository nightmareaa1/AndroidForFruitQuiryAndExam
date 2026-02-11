package com.example.userauth.repository;

import com.example.userauth.entity.FruitFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FruitFileRepository extends JpaRepository<FruitFile, Long> {

    List<FruitFile> findByFruitId(Long fruitId);

    List<FruitFile> findByFruitIdAndFileType(Long fruitId, String fileType);
}
