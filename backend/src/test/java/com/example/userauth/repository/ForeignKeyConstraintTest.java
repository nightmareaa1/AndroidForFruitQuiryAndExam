package com.example.userauth.repository;

import com.example.userauth.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ForeignKeyConstraintTest {

    @Autowired
    private CompetitionEntryRepository entryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private EvaluationModelRepository modelRepository;

    @Test
    public void testContestantForeignKeyConstraint() {
        User user = new User("testuser", "passwordhash");
        user = userRepository.save(user);

        EvaluationModel model = new EvaluationModel("Test Model");
        model = modelRepository.save(model);

        Competition competition = new Competition(
            "Test Competition",
            "Description",
            model,
            user,
            LocalDateTime.now().plusDays(7)
        );
        competition = competitionRepository.save(competition);

        CompetitionEntry entry = new CompetitionEntry(
            competition,
            "Test Entry",
            "Description",
            null,
            1
        );
        entry.setContestant(user);
        entry = entryRepository.save(entry);

        assertNotNull(entry.getContestant());
        assertEquals(user.getId(), entry.getContestant().getId());
    }
}
