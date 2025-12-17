// ==========================================
// ✅ Task.java
// パス: backend/src/main/java/com/taskflow/domain/task/entity/Task.java
// ==========================================
package com.taskflow.domain.task.entity;

import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.user.entity.User;
import com.taskflow.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Version
    private Long version; // 楽観的ロック

    public void changeStatus(TaskStatus newStatus) {
        this.status = newStatus;
    }

    public void update(String title, String description, Priority priority, LocalDate dueDate, User assignee) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (priority != null) this.priority = priority;
        if (dueDate != null) this.dueDate = dueDate;
        if (assignee != null) this.assignee = assignee;
    }

    public void assignTo(User user) {
        this.assignee = user;
    }
}

// ==========================================
// 🎯 TaskStatus.java
// パス: backend/src/main/java/com/taskflow/domain/task/entity/TaskStatus.java
// ==========================================
package com.taskflow.domain.task.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    TODO("未着手", "まだ開始していない"),
    IN_PROGRESS("進行中", "現在作業中"),
    DONE("完了", "作業完了");

    private final String title;
    private final String description;
}

// ==========================================
// 🔥 Priority.java
// パス: backend/src/main/java/com/taskflow/domain/task/entity/Priority.java
// ==========================================
package com.taskflow.domain.task.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {
    HIGH("高", 3),
    MEDIUM("中", 2),
    LOW("低", 1);

    private final String title;
    private final int value;
}

// ==========================================
// 📝 TaskRequest.java
// パス: backend/src/main/java/com/taskflow/domain/task/dto/request/TaskRequest.java
// ==========================================
package com.taskflow.domain.task.dto.request;

import com.taskflow.domain.task.entity.Priority;
import com.taskflow.domain.task.entity.Task;
import com.taskflow.domain.task.entity.TaskStatus;
import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "タイトルは必須です")
    @Size(min = 2, max = 200, message = "タイトルは2文字以上200文字以下です")
    private String title;

    @Size(max = 2000, message = "説明は2000文字以下です")
    private String description;

    private Priority priority;

    private LocalDate dueDate;

    @NotNull(message = "チームIDは必須です")
    private Long teamId;

    private Long assigneeId;

    public Task toEntity(Team team, User createdBy, User assignee) {
        return Task.builder()
                .title(title)
                .description(description)
                .status(TaskStatus.TODO)
                .priority(priority != null ? priority : Priority.MEDIUM)
                .dueDate(dueDate)
                .team(team)
                .createdBy(createdBy)
                .assignee(assignee)
                .build();
    }
}

// ==========================================
// 📤 TaskResponse.java
// パス: backend/src/main/java/com/taskflow/domain/task/dto/response/TaskResponse.java
// ==========================================
package com.taskflow.domain.task.dto.response;

import com.taskflow.domain.task.entity.Priority;
import com.taskflow.domain.task.entity.Task;
import com.taskflow.domain.task.entity.TaskStatus;
import com.taskflow.domain.user.dto.response.UserResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private UserResponse assignee;
    private UserResponse createdBy;
    private Long teamId;
    private String teamName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assignee(task.getAssignee() != null ? UserResponse.from(task.getAssignee()) : null)
                .createdBy(UserResponse.from(task.getCreatedBy()))
                .teamId(task.getTeam().getId())
                .teamName(task.getTeam().getName())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

// ==========================================
// 📦 TaskRepository.java
// パス: backend/src/main/java/com/taskflow/domain/task/repository/TaskRepository.java
// ==========================================
package com.taskflow.domain.task.repository;

import com.taskflow.domain.task.entity.Task;
import com.taskflow.domain.task.entity.TaskStatus;
import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.team " +
           "WHERE t.team = :team")
    Page<Task> findByTeamWithAssignee(@Param("team") Team team, Pageable pageable);

    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.team " +
           "WHERE t.assignee = :user OR t.createdBy = :user")
    List<Task> findByAssigneeOrCreatedBy(@Param("user") User user);

    @Query("SELECT t FROM Task t " +
           "WHERE t.dueDate BETWEEN :startDate AND :endDate " +
           "AND t.status != 'DONE'")
    List<Task> findUpcomingTasks(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Task t " +
           "WHERE t.dueDate < :today " +
           "AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    long countByTeamAndStatus(Team team, TaskStatus status);
}

// ==========================================
// 📦 TaskService.java
// パス: backend/src/main/java/com/taskflow/domain/task/service/TaskService.java
// ==========================================
package com.taskflow.domain.task.service;

import com.taskflow.domain.task.dto.request.TaskRequest;
import com.taskflow.domain.task.dto.response.TaskResponse;
import com.taskflow.domain.task.entity.Task;
import com.taskflow.domain.task.entity.TaskStatus;
import com.taskflow.domain.task.repository.TaskRepository;
import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.team.service.TeamService;
import com.taskflow.domain.user.entity.User;
import com.taskflow.domain.user.service.UserService;
import com.taskflow.global.exception.BusinessException;
import com.taskflow.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final TeamService teamService;
    private final UserService userService;

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest request) {
        log.info("タスク作成試行: userId={}, title={}", userId, request.getTitle());

        User createdBy = userService.findUserById(userId);
        Team team = teamService.findTeamById(request.getTeamId());
        User assignee = request.getAssigneeId() != null 
                ? userService.findUserById(request.getAssigneeId()) 
                : null;

        Task task = request.toEntity(team, createdBy, assignee);
        Task savedTask = taskRepository.save(task);

        log.info("タスク作成成功: taskId={}", savedTask.getId());
        return TaskResponse.from(savedTask);
    }

    public Page<TaskResponse> getTasksByTeam(Long teamId, Pageable pageable) {
        log.info("チーム別タスクリスト照会: teamId={}", teamId);

        Team team = teamService.findTeamById(teamId);
        Page<Task> tasks = taskRepository.findByTeamWithAssignee(team, pageable);

        return tasks.map(TaskResponse::from);
    }

    public TaskResponse getTaskById(Long taskId) {
        Task task = findTaskById(taskId);
        return TaskResponse.from(task);
    }

    public List<TaskResponse> getMyTasks(Long userId) {
        log.info("自分のタスクリスト照会: userId={}", userId);

        User user = userService.findUserById(userId);
        List<Task> tasks = taskRepository.findByAssigneeOrCreatedBy(user);

        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        log.info("タスク修正試行: taskId={}", taskId);

        Task task = findTaskById(taskId);
        User assignee = request.getAssigneeId() != null 
                ? userService.findUserById(request.getAssigneeId()) 
                : null;

        task.update(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate(),
                assignee
        );

        log.info("タスク修正成功: taskId={}", taskId);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse changeStatus(Long taskId, TaskStatus newStatus) {
        log.info("タスク状態変更試行: taskId={}, newStatus={}", taskId, newStatus);

        Task task = findTaskById(taskId);
        task.changeStatus(newStatus);

        log.info("タスク状態変更成功: taskId={}", taskId);
        return TaskResponse.from(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        log.info("タスク削除試行: taskId={}", taskId);

        Task task = findTaskById(taskId);
        taskRepository.delete(task);

        log.info("タスク削除成功: taskId={}", taskId);
    }

    public List<TaskResponse> getUpcomingTasks(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<Task> tasks = taskRepository.findUpcomingTasks(today, endDate);
        
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        List<Task> tasks = taskRepository.findOverdueTasks(today);
        
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));
    }
}

// ==========================================
// 📦 TaskController.java
// パス: backend/src/main/java/com/taskflow/domain/task/controller/TaskController.java
// ==========================================
package com.taskflow.domain.task.controller;

import com.taskflow.domain.task.dto.request.TaskRequest;
import com.taskflow.domain.task.dto.response.TaskResponse;
import com.taskflow.domain.task.entity.TaskStatus;
import com.taskflow.domain.task.service.TaskService;
import com.taskflow.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task", description = "タスクAPI")
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "タスク作成", description = "新しいタスクを作成します")
    @PostMapping
    public ApiResponse<TaskResponse> createTask(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TaskRequest request) {
        
        TaskResponse response = taskService.createTask(userId, request);
        return ApiResponse.success("タスクが作成されました", response);
    }

    @Operation(summary = "タスクリスト", description = "チーム別タスクリストを照会します（ページング）")
    @GetMapping
    public ApiResponse<Page<TaskResponse>> getTasks(
            @RequestParam Long teamId,
            Pageable pageable) {
        
        Page<TaskResponse> response = taskService.getTasksByTeam(teamId, pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "自分のタスク", description = "自分が作成または割り当てられたタスクを照会します")
    @GetMapping("/my")
    public ApiResponse<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal Long userId) {
        List<TaskResponse> response = taskService.getMyTasks(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "タスク詳細", description = "特定タスクの詳細情報を照会します")
    @GetMapping("/{taskId}")
    public ApiResponse<TaskResponse> getTaskById(@PathVariable Long taskId) {
        TaskResponse response = taskService.getTaskById(taskId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "タスク修正", description = "タスク情報を修正します")
    @PutMapping("/{taskId}")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {
        
        TaskResponse response = taskService.updateTask(taskId, request);
        return ApiResponse.success("タスク情報が修正されました", response);
    }

    @Operation(summary = "状態変更", description = "タスクの状態を変更します")
    @PatchMapping("/{taskId}/status")
    public ApiResponse<TaskResponse> changeStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {
        
        TaskResponse response = taskService.changeStatus(taskId, status);
        return ApiResponse.success("タスク状態が変更されました", response);
    }

    @Operation(summary = "タスク削除", description = "タスクを削除します")
    @DeleteMapping("/{taskId}")
    public ApiResponse<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ApiResponse.success("タスクが削除されました", null);
    }

    @Operation(summary = "期限迫るタスク", description = "指定日数以内に期限が迫るタスクを照会します")
    @GetMapping("/upcoming")
    public ApiResponse<List<TaskResponse>> getUpcomingTasks(@RequestParam(defaultValue = "7") int days) {
        List<TaskResponse> response = taskService.getUpcomingTasks(days);
        return ApiResponse.success(response);
    }

    @Operation(summary = "期限切れタスク", description = "期限が過ぎた未完了タスクを照会します")
    @GetMapping("/overdue")
    public ApiResponse<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> response = taskService.getOverdueTasks();
        return ApiResponse.success(response);
    }
}
