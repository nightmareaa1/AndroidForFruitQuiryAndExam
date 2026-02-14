package com.example.userauth.service;

import com.example.userauth.config.TestEnvironmentConfiguration;
import com.example.userauth.entity.Competition;
import com.example.userauth.entity.EvaluationModel;
import com.example.userauth.entity.User;
import com.example.userauth.repository.CompetitionRepository;
import com.example.userauth.repository.EvaluationModelRepository;
import com.example.userauth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestEnvironmentConfiguration.class)
@Transactional
public class SoftDeleteTest {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EvaluationModelRepository modelRepository;

    @Test
    public void testSoftDelete() {
        // 创建并保存关联实体
        User user = new User("testuser", "passwordhash");
        user = userRepository.save(user);

        EvaluationModel model = new EvaluationModel("Test Model");
        model = modelRepository.save(model);

        // 创建赛事
        Competition competition = new Competition(
            "Test Competition",
            "Description",
            model,
            user,
            LocalDateTime.now().plusDays(7)
        );
        competition = competitionRepository.save(competition);
        Long competitionId = competition.getId();

        // 软删除
        competition.setDeletedAt(LocalDateTime.now());
        competitionRepository.save(competition);

        // 验证 findAllActive 不包含已删除数据
        List<Competition> active = competitionRepository.findAllActive();
        assertTrue(active.stream().noneMatch(c -> c.getId().equals(competitionId)));

        // 验证 findById 仍能找到（物理存在）
        assertTrue(competitionRepository.findById(competitionId).isPresent());
    }
}
