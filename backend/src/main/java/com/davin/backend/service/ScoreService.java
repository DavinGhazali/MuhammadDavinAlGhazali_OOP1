package com.davin.backend.service;

import com.davin.backend.model.Score;
import com.davin.backend.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public Score createScore(Score score) {
        return scoreRepository.save(score);
    }

    public Optional<Score> getScoreByID(UUID scoreId) {
        return scoreRepository.findById(scoreId);
    }

    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }
    public List<Score> getAllScores() {
        return scoreRepository.findAllByOrderByCreatedAtDesc();
    }
    public List<Score> getScoreAboveValue(Integer minValue) {
        return scoreRepository.findPointGreaterThan(minValue);
    }

    public List<Score> getLeaderboard(Integer limit) {
        return scoreRepository.findTopScores(limit);
    }

    public void deleteScore(UUID scoreId) {
        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new RuntimeException("Score with ID" + scoreId + "Was not found"));
        scoreRepository.delete(score);
    }



}
