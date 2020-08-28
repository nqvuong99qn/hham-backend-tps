package com.tpssoft.hham.service;

import com.tpssoft.hham.dto.ActivityDto;
import com.tpssoft.hham.dto.UserDto;
import com.tpssoft.hham.entity.Activity;
import com.tpssoft.hham.entity.ActivityType;
import com.tpssoft.hham.entity.EndStatus;
import com.tpssoft.hham.entity.Membership;
import com.tpssoft.hham.entity.Project;
import com.tpssoft.hham.entity.Transaction;
import com.tpssoft.hham.entity.TransactionType;
import com.tpssoft.hham.entity.User;
import com.tpssoft.hham.exception.ActivityNotFoundException;
import com.tpssoft.hham.exception.ProjectNotFoundException;
import com.tpssoft.hham.exception.UserNotFoundException;
import com.tpssoft.hham.repository.ActivityRepository;
import com.tpssoft.hham.repository.FundRepository;
import com.tpssoft.hham.repository.ProjectRepository;
import com.tpssoft.hham.repository.TransactionRepository;
import com.tpssoft.hham.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Data
@Transactional
@AllArgsConstructor
public class ActivityService {
    private final ServiceHelper serviceHelper;
    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FundRepository fundRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final ProjectService projectService;
    private final EmailService emailService;

    /**
     * Create a new activity from the information provided and add projects to the new activity
     *
     * @param name        Name for the new activity
     * @param description Description of the new activity
     * @param projectsId  ID of the projects to add
     *
     * @return Information of the activity created
     *
     * @throws ProjectNotFoundException if any of the ID provided does not belong to a project
     */
    public ActivityDto create(@NonNull String name,
                              @NonNull String description,
                              @NonNull List<Integer> projectsId) {
        for (var id : projectsId) {
            serviceHelper.ensureValidProjectId(id);
        }
        var activity = new Activity();
        activity.setName(name);
        activity.setDescription(description);
        activity.setType(new ActivityType(1, "With options"));
        activity = activityRepository.save(activity);
        var projects = projectsId.stream()
                .map(id -> projectRepository
                        .findById(id)
                        .orElseThrow(ProjectNotFoundException::new)
                )
                .collect(Collectors.toList());
        // Need final to use forEach(), but here we need to modify the entity
        for (var project : projects) {
            activity.getProjects().add(project);
            project.getActivities().add(activity);
            projectRepository.save(project);
            activity = activityRepository.save(activity);
        }
        return ActivityDto.from(activity);
    }

    public String[] notify(int activityId) {
        String[] recipients = projectRepository
                .findByActivityId(activityId)
                .stream()
                .flatMap(project -> project.getMemberships().stream())
                .distinct()
                .map(Membership::getUser)
                .filter(user -> !user.isAdmin())
                .map(User::getEmail)
                .toArray(String[]::new);
        emailService.sendActivityReadyNotification(recipients, activityId);
        return recipients;
    }

    /**
     * Get information of a specific activity
     *
     * @param id ID of the activity
     *
     * @return Information of the specified activity
     *
     * @throws ActivityNotFoundException if the ID provided does not belong to any activity
     */
    public ActivityDto get(int id) {
        return ActivityDto.from(activityRepository
                .findById(id)
                .orElseThrow(ActivityNotFoundException::new)
        );
    }

    /**
     * Add filters corresponding to provided constraints
     *
     * Constraints with unknown field name are ignored.
     *
     * @param stream      The stream to add filters
     * @param constraints The constraints to use
     *
     * @return A new stream with filters corresponding to the search constraints attached
     */
    private Stream<Activity> addConstraints(Stream<Activity> stream, List<SearchConstraint> constraints) {
        for (var constraint : constraints) {
            switch (constraint.getFieldName()) {
                case "name":
                    stream = stream.filter(activity -> constraint.matches(activity.getName()));
                    break;
                case "description":
                    stream = stream.filter(activity -> constraint.matches(activity.getDescription()));
                    break;
                // TODO: Generalize this so multiple projects can be used in the same constraint
                case "projectId":
                    stream = stream.filter(activity ->
                            activity.getProjects()
                                    .stream()
                                    .map(Project::getId)
                                    .anyMatch(constraint::matches)
                    );
                    break;
                case "userId":
                    stream = stream.filter(activity ->
                            activity.getProjects()
                                    .stream()
                                    .flatMap(project -> project.getMemberships().stream())
                                    .map(Membership::getUserId)
                                    .anyMatch(constraint::matches)
                    );
                    break;
                case "archivedOn":
                    stream = stream.filter(activity -> constraint.matches(activity.getArchivedOn()));
                    break;
                default:
                    // Ignore unknown fields
            }
        }
        return stream;
    }

    /**
     * Gets all activities in the system
     *
     * @return List of activities in the system
     */
    public List<ActivityDto> findAll() {
        return findAll(List.of());
    }

    /**
     * Get all activities satisfying the specified constraints
     *
     * @param constraints The constraints to filter the result set
     *
     * @return List of activities satisfying all the constraints provided
     */
    public List<ActivityDto> findAll(List<SearchConstraint> constraints) {
        return addConstraints(activityRepository.findAll().stream(), constraints)
                .map(ActivityDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Update information of a specific activity
     *
     * To keep the value unchanged, pass null to the corresponding paramter
     *
     * @param id          ID of the activity to update
     * @param name        New name for the activity
     * @param description New description for the activity
     *
     * @return Information of the activity after the update
     *
     * @throws ActivityNotFoundException if the ID in the DTO does not belong to any activity
     */
    public ActivityDto update(int id, String name, String description) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(ActivityNotFoundException::new);
        if (name != null) {
            activity.setName(name);
        }
        if (description != null) {
            activity.setDescription(description);
        }
        return ActivityDto.from(activityRepository.save(activity));
    }

    /**
     * Lock an activity so no other vote can be casted on the options
     *
     * @param id ID of the activity to lock voting
     *
     * @return Information of the activity after locking
     *
     * @throws ActivityNotFoundException if the provided ID does not belong to any activity
     */
    public ActivityDto lock(int id) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(ActivityNotFoundException::new);
        activity.setLockedOn(ZonedDateTime.now());
        return ActivityDto.from(activityRepository.save(activity));
    }

    /**
     * Unlock an activity to allow new vote to be casted on the options
     *
     * @param id ID of the activity
     *
     * @return Information of the activity after unlocking
     *
     * @throws ActivityNotFoundException if the provided ID does not belong to any activity
     */
    public ActivityDto unlock(int id) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(ActivityNotFoundException::new);
        activity.setLockedOn(null);
        return ActivityDto.from(activityRepository.save(activity));
    }

    /**
     * Archive an activity so no further modification can happen on that activity
     *
     * @param id        ID of the activity to archive
     * @param endStatus The reason the activity is ended
     *
     * @return Information of the activity after archival
     *
     * @throws ActivityNotFoundException if the ID specified does not belong to any activity
     */
    public ActivityDto archive(int id, @NonNull EndStatus endStatus, int userId) {
        // Update archive to reflect new state
        var activity = activityRepository
                .findById(id)
                .orElseThrow(ActivityNotFoundException::new);
        if (activity.getLockedOn() == null) {
            activity.setLockedOn(ZonedDateTime.now());
        }
        activity.setEndStatus(endStatus);
        activity.setArchivedOn(ZonedDateTime.now());
        var dto = ActivityDto.from(activityRepository.save(activity));
        // Do nothing else if the activity is canceled
        if (endStatus.equals(EndStatus.CANCELED)) {
            return dto;
        }
        // Make change to the fund if the activity actually happened
        var votes = activity.getOptions()
                .stream()
                .flatMap(option -> option.getVotes().stream())
                .collect(Collectors.toList());
        var memoBuilder = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;
        for (var vote : votes) {
            memoBuilder.append(String.format("User %s voted for %s (price: %.2f)",
                    vote.getUser().getDisplayName(),
                    vote.getOption().getName(),
                    vote.getOption().getPrice()
            ));
            memoBuilder.append("\n");
            total = total.add(vote.getOption().getPrice());
        }
        // Assume that the first fund of the first project is used to pay
        var fund = activity.getProjects()
                .stream()
                .findFirst()
                .orElseThrow(ProjectNotFoundException::new)
                .getFunds()
                .get(0);
        var user = userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new);
        var transaction = new Transaction();
        transaction.setType(new TransactionType(2, "Spending"));
        transaction.setUser(user);
        transaction.setActivity(activity);
        transaction.setFund(fund);
        transaction.setMemo(memoBuilder.toString());
        transaction.setAmount(total);
        fund.setAmount(fund.getAmount().subtract(total));
        fundRepository.save(fund);
        transactionRepository.save(transaction);
        return dto;
    }

    public boolean userCanView(int userId, int activityId) {
        return activityRepository
                .findById(activityId)
                .orElseThrow(ActivityNotFoundException::new)
                .getProjects()
                .stream()
                .flatMap(project -> project.getMemberships().stream())
                .anyMatch(membership -> membership.getUserId().equals(userId));
    }

    public boolean userCanEdit(int userId, int activityId) {
        return projectRepository
                .findByActivityId(activityId)
                .stream()
                .flatMap(project -> project.getMemberships().stream())
                .anyMatch(membership -> membership.getUserId().equals(userId) && membership.isAdmin());
    }

    public List<UserDto> getAdmins(int id) {
        return projectRepository
                .findByActivityId(id)
                .stream()
                .flatMap(project -> project.getMemberships().stream())
                .filter(Membership::isAdmin)
                .map(Membership::getUser)
                .map(UserDto::from)
                .distinct()
                .sorted(Comparator.comparing(UserDto::getId))
                .map(dto -> {
                    dto.setPassword(null);
                    dto.setHashedPassword(null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<UserDto> getMembers(int id) {
        return projectRepository
                .findByActivityId(id)
                .stream()
                .flatMap(project -> project.getMemberships().stream())
                .map(Membership::getUser)
                .map(UserDto::from)
                .distinct()
                .sorted(Comparator.comparing(UserDto::getId))
                .map(dto -> {
                    dto.setPassword(null);
                    dto.setHashedPassword(null);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
