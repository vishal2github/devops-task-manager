package com.example.devops;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    public record TaskRequest(
        @NotBlank @Size(max = 150) String title,
        @Size(max = 2000) String description,
        boolean completed
    ) {}

    @GetMapping
    public List<Task> all() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    public Task get(@PathVariable Long id) {
        return findTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(
            request.description() == null ? "" : request.description().trim()
        );
        task.setCompleted(request.completed());
        return repository.save(task);
    }

    @PutMapping("/{id}")
    public Task update(
        @PathVariable Long id,
        @Valid @RequestBody TaskRequest request
    ) {
        Task task = findTask(id);
        task.setTitle(request.title().trim());
        task.setDescription(
            request.description() == null ? "" : request.description().trim()
        );
        task.setCompleted(request.completed());
        return repository.save(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(findTask(id));
    }

    private Task findTask(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Task not found"
            )
        );
    }
}
