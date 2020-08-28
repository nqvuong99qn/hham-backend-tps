package com.tpssoft.hham.service;

import com.tpssoft.hham.dto.*;
import com.tpssoft.hham.entity.Activity;
import com.tpssoft.hham.entity.Fund;
import com.tpssoft.hham.entity.Membership;
import com.tpssoft.hham.entity.MembershipId;
import com.tpssoft.hham.entity.Project;
import com.tpssoft.hham.exception.*;
import com.tpssoft.hham.repository.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor
public class ProjectService {
    private final ServiceHelper serviceHelper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FundRepository fundRepository;
    private final JobTitleRepository jobTitleRepository;
    private final MembershipRepository membershipRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Add search constraints to the operation stream.
     *
     * Constraints with unknown field name will be ignored.
     *
     * @param stream      The stream to add search constraints
     * @param constraints The constraints to add
     *
     * @return New stream with all constraints applied
     */
    private Stream<Project> addConstraints(Stream<Project> stream, List<SearchConstraint> constraints) {
        for (var constraint : constraints) {
            switch (constraint.getFieldName()) {
                case "name":
                    stream = stream.filter(project -> constraint.matches(project.getName()));
                    break;
                case "description":
                    stream = stream.filter(project -> constraint.matches(project.getDescription()));
                    break;
                case "adminId":
                    stream = stream.filter(project ->
                            project.getMemberships()
                                    .stream()
                                    .filter(Membership::isAdmin)
                                    .anyMatch(membership -> constraint.matches(membership.getUserId()))
                    );
                    break;
                case "userId":
                    stream = stream.filter(project ->
                            project.getMemberships()
                                    .stream()
                                    .map(Membership::getUserId)
                                    .anyMatch(constraint::matches)
                    );
                    break;
                case "activityId":
                    stream = stream.filter(project ->
                            project.getActivities()
                                    .stream()
                                    .map(Activity::getId)
                                    .anyMatch(constraint::matches)
                    );
                    break;
                case "archivedOn":
                    stream = stream.filter(project -> constraint.matches(project.getArchivedOn()));
                    break;
                default:
                    // Ignore unknown fields
            }
        }
        return stream;
    }

    /**
     * Get all projects in the system
     *
     * @return Information of all projects in the system
     */
    public List<ProjectDto> findAll() {
        return findAll(List.of());
    }

    /**
     * Get all projects satisfying the given constraints.
     *
     * @param constraints The constraints to filter out result set
     *
     * @return Projects satisfying the given constraints
     */
    public List<ProjectDto> findAll(List<SearchConstraint> constraints) {
        return addConstraints(projectRepository.findAll().stream(), constraints)
                .map(ProjectDto::from)
                .map(dto -> {
                    dto.getFunds().addAll(fundRepository
                            .findByProjectId(dto.getId())
                            .stream()
                            .map(FundDto::from)
                            .collect(Collectors.toList())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get information of the project with a specific ID.
     *
     * @param id ID of the project to get
     *
     * @return Information of the project
     *
     * @throws ProjectNotFoundException if the ID provided does not belong to any project
     */
    public ProjectDto get(int id) {
        var dto = ProjectDto.from(projectRepository
                .findById(id)
                .orElseThrow(ProjectNotFoundException::new));
        dto.setFunds(fundRepository
                .findByProjectId(dto.getId())
                .stream()
                .map(FundDto::from)
                .collect(Collectors.toList())
        );
        return dto;
    }

    /**
     * Create a new project from the provided information
     *
     * @param name        Name for the new project, required
     * @param description Description for the new project, required
     *
     * @return Information of the project created
     */
    public ProjectDto create(@NonNull String name,
                             @NonNull String description,
                             int initialAdminId) {
        if (projectRepository.existsByName(name)) {
            throw new DuplicatedNameException(
                    String.format("The name %s cannot be used because it already exists", name)
            );
        }
        var project = new Project();
        project.setName(name);
        project.setDescription(description);
        var projectDto = ProjectDto.from(projectRepository.save(project));
        var fund = fundRepository.save(new Fund(project.getName(), project));
        projectDto.getFunds().add(FundDto.from(fund));
        membershipRepository.save(new Membership(initialAdminId, projectDto.getId(), true));
        return projectDto;
    }

    /**
     * Update an existing project using the provided information
     *
     * To keep old value, pass null to the corresponding parameter
     *
     * @param id          ID of the project to update
     * @param name        New name for the project
     * @param description New description for the project
     *
     * @return Information of the project after the update
     *
     * @throws ProjectNotFoundException if the ID provided does not belong to any project
     */
    public ProjectDto update(int id, String name, String description) {
        var project = projectRepository
                .findById(id)
                .orElseThrow(ProjectNotFoundException::new);
        if (name != null) {
            project.setName(name);
        }
        if (description != null) {
            project.setDescription(description);
        }
        return ProjectDto.from(projectRepository.save(project));
    }

    /**
     * Archive the project so its information can only be read.
     *
     * @param id ID of the project to archive
     *
     * @return Information of the project archived
     *
     * @throws ProjectNotFoundException if the ID provided does not belong to any project
     */
    public ProjectDto archive(int id) {
        var project = projectRepository
                .findById(id)
                .orElseThrow(ProjectNotFoundException::new);
        project.setArchivedOn(ZonedDateTime.now());
        return ProjectDto.from(projectRepository.save(project));
    }

    /**
     * Get information of all administrators in the project
     *
     * @param projectId ID of the project
     *
     * @return Information of all project administrators
     *
     * @throws ProjectNotFoundException if the provided ID does not belong to any project
     */
    public List<UserDto> getAdmins(int projectId) {
        serviceHelper.ensureValidProjectId(projectId);
        return userRepository
                .getAdminsOfProject(projectId)
                .stream()
                .map(UserDto::from)
                .map(dto -> {
                    dto.setHashedPassword(null);
                    dto.setPassword(null);
                    jobTitleRepository.findByUserId(dto.getId()).ifPresent(jobTitle ->
                            dto.setJobTitle(JobTitleDto.from(jobTitle)));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get information of all members in the project
     *
     * @param projectId ID of the project
     *
     * @return Information of all project members
     *
     * @throws ProjectNotFoundException if the provided ID does not belong to any project
     */
    @Transactional
    public List<UserDto> getMembers(int projectId) {
        serviceHelper.ensureValidProjectId(projectId);
        return userRepository
                .getMembersOfProject(projectId)
                .stream()
                .map(UserDto::from)
                .map(dto -> {
                    dto.setHashedPassword(null);
                    dto.setPassword(null);
                    jobTitleRepository.findByUserId(dto.getId()).ifPresent(jobTitle ->
                            dto.setJobTitle(JobTitleDto.from(jobTitle)));
                    return dto;
                })

                .map(dto -> {
                    var membership = membershipRepository
                            .findById(new MembershipId(projectId, dto.getId()))
                            .orElseThrow(MembershipNotFoundException::new);
                    dto.setAdmin(membership.isAdmin());
                    //   dto.getTransDtos().addAll(transactionRepository.findByUserId(dto.getId()));
                    return dto;
                })

                .map(dto -> {
                    dto.getTransDtos().addAll(transactionRepository
                                .findByUserId(dto.getId(), projectId)
                                .stream()
                                .map(TransactionDto::from)
                                .collect(Collectors.toList()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Add a new member to the project
     *
     * @param projectId ID of the project
     * @param memberId  ID of the user to add to the project
     *
     * @return Information of the user added to project
     *
     * @throws ProjectNotFoundException if the provided project ID does not belong to any project
     * @throws UserNotFoundException    if the provided user ID does not belong to any user
     */
    public UserDto addMember(int projectId, int memberId) {
        serviceHelper.ensureValidProjectId(projectId);
        serviceHelper.ensureValidUserId(memberId);
        var id = new MembershipId(projectId, memberId);
        var membershipOptional = membershipRepository.findById(id);
        UserDto dto;
        if (membershipOptional.isPresent()) {
            dto = UserDto.from(membershipOptional.get().getUser());
        } else {
            dto = UserDto.from(userRepository
                    .findById(memberId)
                    .orElseThrow(UserNotFoundException::new)
            );
            membershipRepository.save(new Membership(id));
        }
        dto.setPassword(null);
        dto.setHashedPassword(null);
        return dto;
    }

    /**
     * Remove a user from the project
     *
     * @param projectId ID of the project
     * @param memberId  ID of the member to be removed
     *
     * @return Information of the user removed from project
     *
     * @throws ProjectNotFoundException    if the provided project ID does not belong to any project
     * @throws UserNotFoundException       if the provided user ID does not belong to any user
     * @throws MembershipNotFoundException if the specified user is not in the project
     */
    public UserDto removeMember(int projectId, int memberId) {
        serviceHelper.ensureValidProjectId(projectId);
        serviceHelper.ensureValidUserId(memberId);
        var membership = membershipRepository
                .findById(new MembershipId(projectId, memberId))
                .orElseThrow(MembershipNotFoundException::new);
        var userDto = UserDto.from(membership.getUser());
        userDto.setPassword(null);
        userDto.setHashedPassword(null);
        membershipRepository.delete(membership);
        return userDto;
    }

    private UserDto changeRole(int projectId, int memberId, boolean changeToAdmin) {
        serviceHelper.ensureValidProjectId(projectId);
        serviceHelper.ensureValidUserId(memberId);
        var id = new MembershipId(projectId, memberId);
        var membershipOptional = membershipRepository.findById(id);
        if (membershipOptional.isEmpty()) {
            throw new MembershipNotFoundException("The specified user is not a project member");
        }
        var membership = membershipOptional.get();
        UserDto dto;
        if (changeToAdmin && membership.isAdmin() || !changeToAdmin && !membership.isAdmin()) {
            dto = UserDto.from(membership.getUser());
        } else {
            membership.setAdmin(changeToAdmin);
            dto = UserDto.from(membershipRepository.save(membership).getUser());
        }
        dto.setPassword(null);
        dto.setHashedPassword(null);
        return dto;
    }

    /**
     * Set a project member as administrator of the project.
     *
     * A project can have multiple administrators.
     *
     * @param projectId ID of the project
     * @param memberId  ID of the member to promote to project administrator
     *
     * @return Information of the user being promoted
     *
     * @throws ProjectNotFoundException    if the provided project ID does not belong to any project
     * @throws UserNotFoundException       if the provided user ID does not belong to any user
     * @throws MembershipNotFoundException if the specified user is not a project member
     */
    public UserDto promoteToAdmin(int projectId, int memberId) {
        return changeRole(projectId, memberId, true);
    }

    /**
     * Set an existing project administrator to regular member
     *
     * @param projectId ID of the project
     * @param memberId  ID of the administrator to demote
     *
     * @return Information of the user being demoted
     *
     * @throws ProjectNotFoundException if the provided project ID does not belong to any project
     * @throws UserNotFoundException    if the provided user ID does not belong to any user
     */
    public UserDto demoteToMember(int projectId, int memberId) {
        return changeRole(projectId, memberId, false);
    }
}
