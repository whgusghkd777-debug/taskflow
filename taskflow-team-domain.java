// ==========================================
// 🏢 Team.java
// パス: backend/src/main/java/com/taskflow/domain/team/entity/Team.java
// ==========================================
package com.taskflow.domain.team.entity;

import com.taskflow.domain.user.entity.User;
import com.taskflow.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public void update(String name, String description) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
    }
}

// ==========================================
// 👥 TeamMember.java
// パス: backend/src/main/java/com/taskflow/domain/team/entity/TeamMember.java
// ==========================================
package com.taskflow.domain.team.entity;

import com.taskflow.domain.user.entity.User;
import com.taskflow.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    public void changeRole(TeamRole newRole) {
        this.role = newRole;
    }
}

// ==========================================
// 🔑 TeamRole.java
// パス: backend/src/main/java/com/taskflow/domain/team/entity/TeamRole.java
// ==========================================
package com.taskflow.domain.team.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamRole {
    LEADER("リーダー", "チームを管理する権限"),
    MEMBER("メンバー", "一般メンバー");

    private final String title;
    private final String description;
}

// ==========================================
// 📝 TeamRequest.java
// パス: backend/src/main/java/com/taskflow/domain/team/dto/request/TeamRequest.java
// ==========================================
package com.taskflow.domain.team.dto.request;

import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequest {

    @NotBlank(message = "チーム名は必須です")
    @Size(min = 2, max = 100, message = "チーム名は2文字以上100文字以下です")
    private String name;

    @Size(max = 500, message = "説明は500文字以下です")
    private String description;

    public Team toEntity(User createdBy) {
        return Team.builder()
                .name(name)
                .description(description)
                .createdBy(createdBy)
                .build();
    }
}

// ==========================================
// 📤 TeamResponse.java
// パス: backend/src/main/java/com/taskflow/domain/team/dto/response/TeamResponse.java
// ==========================================
package com.taskflow.domain.team.dto.response;

import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.user.dto.response.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponse {

    private Long id;
    private String name;
    private String description;
    private UserResponse createdBy;
    private LocalDateTime createdAt;

    public static TeamResponse from(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdBy(UserResponse.from(team.getCreatedBy()))
                .createdAt(team.getCreatedAt())
                .build();
    }
}

// ==========================================
// 📦 TeamRepository.java
// パス: backend/src/main/java/com/taskflow/domain/team/repository/TeamRepository.java
// ==========================================
package com.taskflow.domain.team.repository;

import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByCreatedBy(User createdBy);

    @Query("SELECT DISTINCT t FROM Team t " +
           "LEFT JOIN TeamMember tm ON tm.team = t " +
           "WHERE t.createdBy.id = :userId OR tm.user.id = :userId")
    List<Team> findTeamsByUserId(@Param("userId") Long userId);
}

// ==========================================
// 📦 TeamMemberRepository.java
// パス: backend/src/main/java/com/taskflow/domain/team/repository/TeamMemberRepository.java
// ==========================================
package com.taskflow.domain.team.repository;

import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.team.entity.TeamMember;
import com.taskflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeam(Team team);
    
    List<TeamMember> findByUser(User user);
    
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    
    boolean existsByTeamAndUser(Team team, User user);
}

// ==========================================
// 📦 TeamService.java
// パス: backend/src/main/java/com/taskflow/domain/team/service/TeamService.java
// ==========================================
package com.taskflow.domain.team.service;

import com.taskflow.domain.team.dto.request.TeamRequest;
import com.taskflow.domain.team.dto.response.TeamResponse;
import com.taskflow.domain.team.entity.Team;
import com.taskflow.domain.team.entity.TeamMember;
import com.taskflow.domain.team.entity.TeamRole;
import com.taskflow.domain.team.repository.TeamMemberRepository;
import com.taskflow.domain.team.repository.TeamRepository;
import com.taskflow.domain.user.entity.User;
import com.taskflow.domain.user.service.UserService;
import com.taskflow.global.exception.BusinessException;
import com.taskflow.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;

    @Transactional
    public TeamResponse createTeam(Long userId, TeamRequest request) {
        log.info("チーム作成試行: userId={}, teamName={}", userId, request.getName());

        User user = userService.findUserById(userId);
        Team team = request.toEntity(user);
        Team savedTeam = teamRepository.save(team);

        // 作成者を自動的にリーダーとして追加
        TeamMember leader = TeamMember.builder()
                .team(savedTeam)
                .user(user)
                .role(TeamRole.LEADER)
                .build();
        teamMemberRepository.save(leader);

        log.info("チーム作成成功: teamId={}", savedTeam.getId());
        return TeamResponse.from(savedTeam);
    }

    public List<TeamResponse> getMyTeams(Long userId) {
        log.info("自分のチームリスト照会: userId={}", userId);
        
        List<Team> teams = teamRepository.findTeamsByUserId(userId);
        
        return teams.stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }

    public TeamResponse getTeamById(Long teamId) {
        Team team = findTeamById(teamId);
        return TeamResponse.from(team);
    }

    @Transactional
    public TeamResponse updateTeam(Long userId, Long teamId, TeamRequest request) {
        log.info("チーム修正試行: userId={}, teamId={}", userId, teamId);

        Team team = findTeamById(teamId);
        
        // リーダー権限確認
        if (!team.getCreatedBy().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_TEAM_LEADER);
        }

        team.update(request.getName(), request.getDescription());
        
        log.info("チーム修正成功: teamId={}", teamId);
        return TeamResponse.from(team);
    }

    @Transactional
    public void deleteTeam(Long userId, Long teamId) {
        log.info("チーム削除試行: userId={}, teamId={}", userId, teamId);

        Team team = findTeamById(teamId);
        
        if (!team.getCreatedBy().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_TEAM_LEADER);
        }

        teamRepository.delete(team);
        log.info("チーム削除成功: teamId={}", teamId);
    }

    @Transactional
    public void inviteMember(Long userId, Long teamId, Long inviteeId) {
        log.info("メンバー招待試行: userId={}, teamId={}, inviteeId={}", userId, teamId, inviteeId);

        Team team = findTeamById(teamId);
        User invitee = userService.findUserById(inviteeId);

        // リーダー権限確認
        if (!team.getCreatedBy().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_TEAM_LEADER);
        }

        // 既にメンバーか確認
        if (teamMemberRepository.existsByTeamAndUser(team, invitee)) {
            throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER);
        }

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(invitee)
                .role(TeamRole.MEMBER)
                .build();
        
        teamMemberRepository.save(member);
        log.info("メンバー招待成功: teamId={}, userId={}", teamId, inviteeId);
    }

    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
    }
}

// ==========================================
// 📦 TeamController.java
// パス: backend/src/main/java/com/taskflow/domain/team/controller/TeamController.java
// ==========================================
package com.taskflow.domain.team.controller;

import com.taskflow.domain.team.dto.request.TeamRequest;
import com.taskflow.domain.team.dto.response.TeamResponse;
import com.taskflow.domain.team.service.TeamService;
import com.taskflow.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team", description = "チームAPI")
@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "チーム作成", description = "新しいチームを作成します")
    @PostMapping
    public ApiResponse<TeamResponse> createTeam(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TeamRequest request) {
        
        TeamResponse response = teamService.createTeam(userId, request);
        return ApiResponse.success("チームが作成されました", response);
    }

    @Operation(summary = "自分のチームリスト", description = "自分が所属するチームリストを照会します")
    @GetMapping
    public ApiResponse<List<TeamResponse>> getMyTeams(@AuthenticationPrincipal Long userId) {
        List<TeamResponse> response = teamService.getMyTeams(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "チーム詳細", description = "特定チームの詳細情報を照会します")
    @GetMapping("/{teamId}")
    public ApiResponse<TeamResponse> getTeamById(@PathVariable Long teamId) {
        TeamResponse response = teamService.getTeamById(teamId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "チーム修正", description = "チーム情報を修正します（リーダーのみ）")
    @PutMapping("/{teamId}")
    public ApiResponse<TeamResponse> updateTeam(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long teamId,
            @Valid @RequestBody TeamRequest request) {
        
        TeamResponse response = teamService.updateTeam(userId, teamId, request);
        return ApiResponse.success("チーム情報が修正されました", response);
    }

    @Operation(summary = "チーム削除", description = "チームを削除します（リーダーのみ）")
    @DeleteMapping("/{teamId}")
    public ApiResponse<Void> deleteTeam(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long teamId) {
        
        teamService.deleteTeam(userId, teamId);
        return ApiResponse.success("チームが削除されました", null);
    }

    @Operation(summary = "メンバー招待", description = "チームに新しいメンバーを招待します")
    @PostMapping("/{teamId}/members/{userId}")
    public ApiResponse<Void> inviteMember(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        
        teamService.inviteMember(currentUserId, teamId, userId);
        return ApiResponse.success("メンバーが招待されました", null);
    }
}
