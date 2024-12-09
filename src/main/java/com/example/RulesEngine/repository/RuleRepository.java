package com.example.RulesEngine.repository;

import com.example.RulesEngine.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    // Custom query method to find rules by business unit
    List<Rule> findByBusinessUnit(String business_unit);

    Rule findByIdAndBusinessUnit(Long id, String businessUnit);
}

