package s24.backend.exerciselog.controller.rest;

import java.util.*;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import s24.backend.exerciselog.domain.dto.*;
import s24.backend.exerciselog.domain.entity.User;
import s24.backend.exerciselog.service.WorkoutService;
import s24.backend.exerciselog.util.SecurityUtils;
import s24.backend.exerciselog.util.ValidationUtil;



@RestController
public class WorkoutRestController {
    @Autowired
    private WorkoutService workoutService;
    
    @GetMapping("/api/workouts")
    public ResponseEntity<List<WorkoutDto>> getAllWorkouts() {
        User user = SecurityUtils.getCurrentUser();
        List<WorkoutDto> workouts = workoutService.getUserWorkouts(user);
        return ResponseEntity.status(HttpStatus.OK).body(workouts);
    }

    // TODO Get workout by id, edit workout by id

    @GetMapping("/api/workouts/completed")
    public ResponseEntity<List<CompletedWorkoutDto>> getAllCompletedWorkouts() {
        User user = SecurityUtils.getCurrentUser();
        List<CompletedWorkoutDto> completedWorkouts = workoutService.getUserCompletedWorkouts(user);
        return ResponseEntity.status(HttpStatus.OK).body(completedWorkouts);
    }


    @PostMapping("/api/workouts")
    public ResponseEntity<?> addWorkout(@Valid @RequestBody WorkoutDto workoutDto, BindingResult result) {
        ResponseEntity<Map<String, String>> validationErrors = ValidationUtil.handleValidationErrors(result);
        if(validationErrors != null) {
            return validationErrors;
        }

        User user = SecurityUtils.getCurrentUser();
        workoutService.addWorkout(workoutDto, user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/api/workouts/start/{id}")
    public ResponseEntity<CompletedWorkoutDto> startWorkout(@PathVariable Long id) {
        CompletedWorkoutDto completedWorkoutDto = workoutService.startWorkout(id);
        return ResponseEntity.status(HttpStatus.OK).body(completedWorkoutDto);
    }

    @PostMapping("/api/workouts/complete/{id}")
    public ResponseEntity<?> completeWorkout(
        @PathVariable Long id, 
        @Valid @RequestBody CompletedWorkoutDto completedWorkoutDto, BindingResult result) throws BadRequestException {
        
        ResponseEntity<Map<String, String>> validationErrors = ValidationUtil.handleValidationErrors(result);
        if(validationErrors != null) {
            return validationErrors;
        }

        workoutService.completeWorkout(id, completedWorkoutDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @DeleteMapping("/api/workouts/delete-planned/{id}")
    public ResponseEntity<?> deletePlannedWorkout(@PathVariable Long id) {
        workoutService.deletePlannedWorkout(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @DeleteMapping("/api/workouts/delete-completed/{id}")
    public ResponseEntity<?> deleteCompletedWorkout(@PathVariable Long id) {
        workoutService.deleteCompletedWorkout(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
