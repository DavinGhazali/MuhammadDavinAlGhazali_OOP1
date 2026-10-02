package com.davin.backend.controller;

import com.davin.backend.model.Score;
import com.davin.backend.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{scoreId}")
    public ResponseEntity<?> getScoreById(@PathVariable UUID scoreId) {
        Optional<Score> score = scoreService.getScoreByID(scoreId);

        if (score.isPresent()) {
            return ResponseEntity.ok(score.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Score not found");
        }
    }

    @PostMapping
    public ResponseEntity<?> createScore(@RequestBody Score score) {
        try {
            Score newScore = scoreService.createScore(score);
            return ResponseEntity.status(HttpStatus.CREATED).body(newScore);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating score: " + e.getMessage());
        }
    }

    @GetMapping

    public ResponseEntity<List<Score>> getAllScores() {
        List<Score> scores = scoreService.getAllScores();
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Score>> getLeaderboardByPoint(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Score> scores = scoreService.getLeaderboard(limit);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/above/{minValue}")
    public ResponseEntity<List<Score>> getScoresAboveValue(
            @PathVariable Integer minValue) {
        List<Score> scores = scoreService.getScoreAboveValue(minValue);
        return ResponseEntity.ok(scores);

    }

    @GetMapping("/recent")
    public ResponseEntity<List<Score>> getRecentScores(){
        List<Score> scores = scoreService.getRecentScores();
        return ResponseEntity.ok(scores);
    }

    @DeleteMapping("/{scoreID}")
    public ResponseEntity<?> deleteScore(
            @PathVariable UUID scoreID){
        try {
            scoreService.deleteScore(scoreID);
            return ResponseEntity.ok("scores with ID" + scoreID + "was deleted");

        } catch (RuntimeException e){
            return ResponseEntity.status((HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



}