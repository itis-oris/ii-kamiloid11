package com.skillswap.repository;

import com.skillswap.entity.Skill;
import com.skillswap.entity.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByCategory(SkillCategory category);
}
