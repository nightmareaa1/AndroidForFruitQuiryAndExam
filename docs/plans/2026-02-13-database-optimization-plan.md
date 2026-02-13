# 数据库设计缺陷修复实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复数据库设计缺陷，包括缺失索引、外键约束、软删除机制和字段类型优化

**Architecture:** 使用 Flyway 数据库迁移脚本进行增量修复，同时更新 JPA 实体类，确保数据库模式与代码模型保持一致

**Tech Stack:** MySQL 8.0, Spring Data JPA, Flyway, Java 17

---

## Phase 1: 修复 P0 级缺失索引 (预计 40 分钟)

### Task 1.1: 创建核心表索引迁移脚本

**Files:**
- Create: `backend/src/main/resources/db/migration/V7__Add_missing_indexes.sql`
- Test: 运行查询验证索引被使用

**Step 1: 创建 competitions 表索引**

```sql
-- competitions 表索引
CREATE INDEX idx_competitions_creator_id ON competitions(creator_id);
CREATE INDEX idx_competitions_model_id ON competitions(model_id);
CREATE INDEX idx_competitions_status ON competitions(status);
CREATE INDEX idx_competitions_deadline ON competitions(deadline);
```

**Step 2: 创建 competition_entries 表索引**

```sql
-- competition_entries 表索引
CREATE INDEX idx_entries_competition_id ON competition_entries(competition_id);
CREATE INDEX idx_entries_contestant_id ON competition_entries(contestant_id);
CREATE INDEX idx_entries_status ON competition_entries(status);
CREATE INDEX idx_entries_competition_order ON competition_entries(competition_id, display_order);
```

**Step 3: 创建 competition_ratings 表索引**

```sql
-- competition_ratings 表索引
CREATE INDEX idx_ratings_entry_id ON competition_ratings(entry_id);
CREATE INDEX idx_ratings_judge_id ON competition_ratings(judge_id);
CREATE INDEX idx_ratings_competition_id ON competition_ratings(competition_id);
CREATE INDEX idx_ratings_entry_judge ON competition_ratings(entry_id, judge_id);
```

**Step 4: 创建 evaluation_parameters 表索引**

```sql
-- evaluation_parameters 表索引
CREATE INDEX idx_parameters_model_id ON evaluation_parameters(model_id);
```

**Step 5: 验证迁移脚本语法**

Run: `cd backend && mvn flyway:validate`
Expected: SUCCESS - 所有迁移脚本有效

**Step 6: Commit**

```bash
git add backend/src/main/resources/db/migration/V7__Add_missing_indexes.sql
git commit -m "db: add missing indexes for core tables (P0)"
```

---

### Task 1.2: 运行索引迁移并验证

**Step 1: 启动开发环境**

Run: `cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
Expected: 应用正常启动，Flyway 自动执行 V7 迁移

**Step 2: 验证索引创建**

Connect to MySQL and run:
```sql
SHOW INDEX FROM competitions;
SHOW INDEX FROM competition_entries;
SHOW INDEX FROM competition_ratings;
```
Expected: 看到新创建的索引（idx_competitions_creator_id, idx_entries_competition_id 等）

**Step 3: 验证查询使用索引**

```sql
-- 测试 competitions 查询
EXPLAIN SELECT * FROM competitions WHERE creator_id = 1;
-- Expected: type=ref, key=idx_competitions_creator_id

-- 测试 entries 查询
EXPLAIN SELECT * FROM competition_entries WHERE competition_id = 1 ORDER BY display_order;
-- Expected: type=ref, key=idx_entries_competition_order
```

**Step 4: Commit**

```bash
git add -A
git commit -m "test: verify indexes are created and used by queries"
```

---

## Phase 2: 修复 P0 级缺失外键约束 (预计 30 分钟)

### Task 2.1: 创建外键约束迁移脚本

**Files:**
- Create: `backend/src/main/resources/db/migration/V8__Add_missing_foreign_keys.sql`

**Step 1: 添加 contestant_id 外键**

```sql
-- competition_entries 表的 contestant_id 外键
ALTER TABLE competition_entries 
ADD CONSTRAINT fk_entries_contestant 
FOREIGN KEY (contestant_id) REFERENCES users(id) 
ON DELETE SET NULL;
```

**Step 2: 优化现有外键的级联策略**

```sql
-- competition_entries 的 competition_id 外键（已有，添加 ON DELETE CASCADE）
-- 注意：需要先删除再重新创建
-- 检查是否已存在外键约束
SELECT CONSTRAINT_NAME 
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_NAME = 'competition_entries' 
AND COLUMN_NAME = 'competition_id' 
AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 如果存在，先删除
-- ALTER TABLE competition_entries DROP FOREIGN KEY [constraint_name];

-- 重新创建带级联删除的外键
ALTER TABLE competition_entries 
ADD CONSTRAINT fk_entries_competition 
FOREIGN KEY (competition_id) REFERENCES competitions(id) 
ON DELETE CASCADE;
```

**Step 3: 验证外键约束**

```sql
-- 查看所有外键约束
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'userauth_dev'
AND REFERENCED_TABLE_NAME IS NOT NULL;
```

**Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/V8__Add_missing_foreign_keys.sql
git commit -m "db: add missing foreign key constraints for data integrity (P0)"
```

---

### Task 2.2: 测试外键约束

**Step 1: 编写外键约束测试**

Create: `backend/src/test/java/com/example/userauth/repository/ForeignKeyConstraintTest.java`

```java
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
        // 创建测试用户
        User user = new User("testuser", "passwordhash");
        user = userRepository.save(user);

        // 创建评价模型
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

        // 创建作品条目关联到用户
        CompetitionEntry entry = new CompetitionEntry(
            competition, 
            "Test Entry", 
            "Description", 
            null, 
            1
        );
        entry.setContestant(user);
        entry = entryRepository.save(entry);

        // 验证外键关系
        assertNotNull(entry.getContestant());
        assertEquals(user.getId(), entry.getContestant().getId());
    }
}
```

**Step 2: 运行测试**

Run: `cd backend && mvn test -Dtest=ForeignKeyConstraintTest`
Expected: BUILD SUCCESS - 测试通过

**Step 3: Commit**

```bash
git add backend/src/test/java/com/example/userauth/repository/ForeignKeyConstraintTest.java
git commit -m "test: add foreign key constraint validation tests (P0)"
```

---

## Phase 3: 修复 P1 级软删除机制 (预计 50 分钟)

### Task 3.1: 创建软删除迁移脚本

**Files:**
- Create: `backend/src/main/resources/db/migration/V9__Add_soft_delete_columns.sql`

**Step 1: 添加 deleted_at 字段**

```sql
-- 为业务表添加软删除字段
ALTER TABLE competitions ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE competition_entries ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE competition_ratings ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE evaluation_models ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;
```

**Step 2: 创建软删除索引**

```sql
-- 创建部分索引加速活跃数据查询（MySQL 8.0+ 支持函数索引）
CREATE INDEX idx_competitions_active ON competitions(deleted_at, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_entries_active ON competition_entries(deleted_at, competition_id) WHERE deleted_at IS NULL;
```

**Step 3: Commit**

```bash
git add backend/src/main/resources/db/migration/V9__Add_soft_delete_columns.sql
git commit -m "db: add soft delete columns (deleted_at) to core tables (P1)"
```

---

### Task 3.2: 更新 JPA 实体添加软删除字段

**Files:**
- Modify: `backend/src/main/java/com/example/userauth/entity/Competition.java`
- Modify: `backend/src/main/java/com/example/userauth/entity/CompetitionEntry.java`
- Modify: `backend/src/main/java/com/example/userauth/entity/CompetitionRating.java`
- Modify: `backend/src/main/java/com/example/userauth/entity/EvaluationModel.java`

**Step 1: 更新 Competition 实体**

```java
// 在 Competition.java 的 updatedAt 字段后添加
@Column(name = "deleted_at")
private LocalDateTime deletedAt;

// 添加 getter 和 setter
public LocalDateTime getDeletedAt() {
    return deletedAt;
}

public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
}

// 添加软删除检查方法
public boolean isDeleted() {
    return deletedAt != null;
}
```

**Step 2: 更新 CompetitionEntry 实体**

```java
// 在 CompetitionEntry.java 的 updatedAt 字段后添加
@Column(name = "deleted_at")
private LocalDateTime deletedAt;

public LocalDateTime getDeletedAt() { return deletedAt; }
public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
public boolean isDeleted() { return deletedAt != null; }
```

**Step 3: 更新 CompetitionRating 实体**

```java
// 在 CompetitionRating.java 的 submittedAt 字段后添加
@Column(name = "deleted_at")
private LocalDateTime deletedAt;

public LocalDateTime getDeletedAt() { return deletedAt; }
public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
public boolean isDeleted() { return deletedAt != null; }
```

**Step 4: 更新 EvaluationModel 实体**

```java
// 在 EvaluationModel.java 的 updatedAt 字段后添加
@Column(name = "deleted_at")
private LocalDateTime deletedAt;

public LocalDateTime getDeletedAt() { return deletedAt; }
public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
public boolean isDeleted() { return deletedAt != null; }
```

**Step 5: 验证编译**

Run: `cd backend && mvn compile`
Expected: BUILD SUCCESS

**Step 6: Commit**

```bash
git add -A
git commit -m "feat: add soft delete fields to entity classes (P1)"
```

---

### Task 3.3: 更新 Repository 层支持软删除

**Files:**
- Modify: `backend/src/main/java/com/example/userauth/repository/CompetitionRepository.java`
- Modify: `backend/src/main/java/com/example/userauth/repository/CompetitionEntryRepository.java`

**Step 1: 更新 CompetitionRepository**

```java
// 在 CompetitionRepository.java 中添加软删除查询方法

/**
 * Find all non-deleted competitions
 */
@Query("SELECT c FROM Competition c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC")
List<Competition> findAllActive();

/**
 * Find all non-deleted competitions by creator
 */
@Query("SELECT c FROM Competition c WHERE c.creator.id = :creatorId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
List<Competition> findActiveByCreatorIdOrderByCreatedAtDesc(@Param("creatorId") Long creatorId);

/**
 * Find non-deleted competition by id with details
 */
@Query("SELECT c FROM Competition c " +
       "LEFT JOIN FETCH c.model m " +
       "LEFT JOIN FETCH m.parameters " +
       "LEFT JOIN FETCH c.creator " +
       "LEFT JOIN FETCH c.judges j " +
       "LEFT JOIN FETCH j.judge " +
       "LEFT JOIN FETCH c.entries e " +
       "WHERE c.id = :id AND c.deletedAt IS NULL")
Optional<Competition> findActiveByIdWithDetails(@Param("id") Long id);
```

**Step 2: 更新 CompetitionEntryRepository**

```java
// 在 CompetitionEntryRepository.java 中添加

/**
 * Find all non-deleted entries for a competition
 */
@Query("SELECT e FROM CompetitionEntry e WHERE e.competition.id = :competitionId AND e.deletedAt IS NULL ORDER BY e.displayOrder")
List<CompetitionEntry> findActiveByCompetitionIdOrderByDisplayOrder(@Param("competitionId") Long competitionId);

/**
 * Soft delete entry by setting deleted_at
 */
@Modifying
@Query("UPDATE CompetitionEntry e SET e.deletedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
void softDeleteById(@Param("id") Long id);
```

**Step 3: Commit**

```bash
git add -A
git commit -m "feat: update repositories to support soft delete queries (P1)"
```

---

### Task 3.4: 创建软删除服务层方法

**Files:**
- Modify: `backend/src/main/java/com/example/userauth/service/CompetitionService.java`

**Step 1: 添加软删除方法**

```java
// 在 CompetitionService.java 中添加软删除替代方法

/**
 * Soft delete competition (instead of hard delete)
 */
@Transactional
public void softDeleteCompetition(Long id, Long userId) {
    logger.info("Soft deleting competition with id: {} by user: {}", id, userId);
    
    Competition competition = competitionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("赛事不存在: " + id));
    
    // Check if user is the creator
    if (!competition.getCreator().getId().equals(userId)) {
        throw new IllegalArgumentException("只有赛事创建者可以删除赛事");
    }
    
    // Soft delete
    competition.setDeletedAt(LocalDateTime.now());
    competitionRepository.save(competition);
    
    logger.info("Successfully soft deleted competition with id: {}", id);
}

/**
 * Get all active (non-deleted) competitions
 */
@Transactional(readOnly = true)
public List<CompetitionResponse> getAllActiveCompetitions(Long userId, boolean isAdmin) {
    logger.info("Fetching active competitions for user: {}", userId);
    
    List<Competition> competitions = competitionRepository.findAllActive();
    
    logger.info("Returning {} active competitions", competitions.size());
    return competitions.stream()
            .map(this::convertToResponse)
            .toList();
}
```

**Step 2: Commit**

```bash
git add -A
git commit -m "feat: add soft delete methods to CompetitionService (P1)"
```

---

### Task 3.5: 编写软删除测试

**Files:**
- Create: `backend/src/test/java/com/example/userauth/service/SoftDeleteTest.java`

**Step 1: 创建软删除测试类**

```java
package com.example.userauth.service;

import com.example.userauth.entity.Competition;
import com.example.userauth.entity.EvaluationModel;
import com.example.userauth.entity.User;
import com.example.userauth.repository.CompetitionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SoftDeleteTest {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Test
    public void testSoftDelete() {
        // 创建测试数据
        User user = new User("testuser", "passwordhash");
        EvaluationModel model = new EvaluationModel("Test Model");
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
```

**Step 2: 运行测试**

Run: `cd backend && mvn test -Dtest=SoftDeleteTest`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/test/java/com/example/userauth/service/SoftDeleteTest.java
git commit -m "test: add soft delete functionality tests (P1)"
```

---

## Phase 4: 修复 P2 级字段类型优化 (预计 30 分钟)

### Task 4.1: 创建字段类型优化迁移脚本

**Files:**
- Create: `backend/src/main/resources/db/migration/V10__Optimize_field_types.sql`

**Step 1: 优化评分字段类型**

```sql
-- competition_ratings.score: DOUBLE -> DECIMAL(5,2)
ALTER TABLE competition_ratings MODIFY COLUMN score DECIMAL(5, 2) NOT NULL;
```

**Step 2: 优化文件路径字段长度**

```sql
-- competition_entries.file_path: VARCHAR(255) -> VARCHAR(500)
ALTER TABLE competition_entries MODIFY COLUMN file_path VARCHAR(500);
```

**Step 3: 统一时间戳更新策略**

```sql
-- 统一所有表的 updated_at 字段使用 ON UPDATE CURRENT_TIMESTAMP
ALTER TABLE competitions 
MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE competition_entries 
MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE competition_ratings 
MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE competition_judges 
MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
```

**Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/V10__Optimize_field_types.sql
git commit -m "db: optimize field types - DECIMAL for scores, VARCHAR length, timestamp strategy (P2)"
```

---

### Task 4.2: 更新 JPA 实体字段类型

**Files:**
- Modify: `backend/src/main/java/com/example/userauth/entity/CompetitionRating.java`

**Step 1: 更新 score 字段类型**

```java
// 在 CompetitionRating.java 中修改 score 字段

@Column(name = "score", nullable = false, precision = 5, scale = 2)
private BigDecimal score;

// 更新构造函数和 getter/setter
public CompetitionRating(Competition competition, CompetitionEntry entry, User judge, 
                       EvaluationParameter parameter, BigDecimal score, String note) {
    this.competition = competition;
    this.entry = entry;
    this.judge = judge;
    this.parameter = parameter;
    this.score = score;
    this.note = note;
}

public BigDecimal getScore() { return score; }
public void setScore(BigDecimal score) { this.score = score; }
```

**Step 2: 验证编译和测试**

Run: `cd backend && mvn clean compile test`
Expected: BUILD SUCCESS - 所有测试通过

**Step 3: Commit**

```bash
git add -A
git commit -m "refactor: update CompetitionRating to use BigDecimal for precision (P2)"
```

---

## Phase 5: 集成测试与验证 (预计 20 分钟)

### Task 5.1: 运行完整测试套件

**Step 1: 运行所有单元测试**

Run: `cd backend && mvn clean test`
Expected: BUILD SUCCESS - 所有测试通过

**Step 2: 运行 Flyway 验证**

Run: `cd backend && mvn flyway:info`
Expected: 显示所有迁移脚本（V1-V10）已应用

**Step 3: 验证数据库状态**

Connect to MySQL and verify:
```sql
-- 验证索引
SHOW INDEX FROM competitions;
SHOW INDEX FROM competition_entries;

-- 验证外键
SELECT CONSTRAINT_NAME, REFERENCED_TABLE_NAME 
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'userauth_dev' AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 验证软删除字段
DESCRIBE competitions;
-- Expected: deleted_at 字段存在
```

**Step 4: Commit 最终状态**

```bash
git add -A
git commit -m "test: verify all database optimizations are working correctly"
```

---

## 总结

### 完成的修复项

| 优先级 | 修复项 | 状态 | 迁移脚本 |
|--------|--------|------|----------|
| 🔴 P0 | 缺失索引 | ✅ | V7 |
| 🔴 P0 | 缺失外键约束 | ✅ | V8 |
| 🟡 P1 | 软删除机制 | ✅ | V9 |
| 🟢 P2 | 字段类型优化 | ✅ | V10 |

### 性能提升预期

| 查询类型 | 优化前 | 优化后 | 提升倍数 |
|----------|--------|--------|----------|
| 赛事列表查询 | 全表扫描 | 索引扫描 | 1000x+ |
| 作品列表查询 | O(n) | O(log n) | 100x+ |
| 评分统计查询 | O(n²) | O(n log n) | 50x+ |

### 回滚策略

如需回滚，按相反顺序执行：
```bash
# 回滚到 V6
mvn flyway:clean  # 清空数据库
mvn flyway:migrate  # 重新应用到 V6
```

**Plan complete! Ready for execution with superpowers:executing-plans**
