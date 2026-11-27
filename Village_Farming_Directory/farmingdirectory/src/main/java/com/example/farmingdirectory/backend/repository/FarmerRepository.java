
package com.example.farmingdirectory.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.farmingdirectory.backend.model.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Farmer findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByMobile(String mobile);

    @Query("SELECT f FROM Farmer f WHERE " +
           "LOWER(f.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.village) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.crops) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Farmer> searchFarmers(@Param("keyword") String keyword);
}
