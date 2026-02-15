-- V7__Add_missing_indexes.sql
-- 创建核心表索引以优化查询性能
-- 创建日期: 2026-02-13

-- competitions 表索引
CREATE INDEX idx_competitions_creator_id ON competitions(creator_id);
CREATE INDEX idx_competitions_model_id ON competitions(model_id);
CREATE INDEX idx_competitions_status ON competitions(status);
CREATE INDEX idx_competitions_deadline ON competitions(deadline);

-- competition_entries 表索引
CREATE INDEX idx_entries_competition_id ON competition_entries(competition_id);
CREATE INDEX idx_entries_status ON competition_entries(status);
CREATE INDEX idx_entries_competition_order ON competition_entries(competition_id, display_order);

-- competition_ratings 表索引
CREATE INDEX idx_ratings_entry_id ON competition_ratings(entry_id);
CREATE INDEX idx_ratings_judge_id ON competition_ratings(judge_id);
CREATE INDEX idx_ratings_competition_id ON competition_ratings(competition_id);
CREATE INDEX idx_ratings_entry_judge ON competition_ratings(entry_id, judge_id);

-- evaluation_parameters 表索引
CREATE INDEX idx_parameters_model_id ON evaluation_parameters(model_id);
