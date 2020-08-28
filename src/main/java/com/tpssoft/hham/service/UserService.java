package com.tpssoft.hham.service;

import com.tpssoft.hham.Helper;
import com.tpssoft.hham.dto.InvitationDto;
import com.tpssoft.hham.dto.JobTitleDto;
import com.tpssoft.hham.dto.UserDto;
import com.tpssoft.hham.entity.Invitation;
import com.tpssoft.hham.entity.JobTaken;
import com.tpssoft.hham.entity.Membership;
import com.tpssoft.hham.entity.User;
import com.tpssoft.hham.exception.DuplicatedEmailException;
import com.tpssoft.hham.exception.IllegalOperationException;
import com.tpssoft.hham.exception.InvitationNotFoundException;
import com.tpssoft.hham.exception.JobTitleNotFoundException;
import com.tpssoft.hham.exception.NoPrivilegeException;
import com.tpssoft.hham.exception.PasswordRequirementsNotMeetException;
import com.tpssoft.hham.exception.UserNotFoundException;
import com.tpssoft.hham.repository.InvitationRepository;
import com.tpssoft.hham.repository.JobTakenRepository;
import com.tpssoft.hham.repository.JobTitleRepository;
import com.tpssoft.hham.repository.MembershipRepository;
import com.tpssoft.hham.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobTitleRepository jobTitleRepository;
    private final JobTakenRepository jobTakenRepository;
    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final SessionService sessionService;
    private final MembershipRepository membershipRepository;
    private final ServiceHelper serviceHelper;

    public UserDto create(@NonNull String username,
                          @NonNull String plainPassword,
                          @NonNull String email,
                          @NonNull String displayName,
                          Integer jobTitleId) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException(String.format(
                    "The email %s cannot be used because it belongs to an existing user",
                    email
            ));
        }
        var user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setEmail(email);
        user.setDisplayName(displayName);
        var userDto = UserDto.from(userRepository.save(user));
        if (jobTitleId != null) {
            var jobTitle = jobTitleRepository
                    .findById(jobTitleId)
                    .orElseThrow(JobTitleNotFoundException::new);
            jobTakenRepository.save(new JobTaken(userDto.getId(), jobTitleId));
            userDto.setJobTitle(JobTitleDto.from(jobTitle));
        }

        userDto.setPassword(null);
        userDto.setHashedPassword(null);
        invitationRepository.deleteAllByEmail(email);
        return userDto;
    }

    /**
     * Create new account using activation token
     *
     * @param activationToken The activation token
     * @param username        Username for the new user account
     * @param plainPassword   Password in plaintext for the new user account
     * @param email           Email of the new user
     * @param displayName     Display name for the new user
     * @param jobTitleId      ID of the job title for the new user
     *
     * @return Information of the user created
     *
     * @throws NoPrivilegeException if the activation token is missing or invalid
     */
    public UserDto create(String activationToken,
                          @NonNull String username,
                          @NonNull String plainPassword,
                          @NonNull String email,
                          @NonNull String displayName,
                          Integer jobTitleId) {

        if (activationToken == null) {
            throw new NoPrivilegeException("Activation token is missing");
        }
        var invitation = invitationRepository.
                findById(activationToken).
                orElseThrow(() -> {
                    throw new NoPrivilegeException("Invalid activation token");
                });
        if (!invitation.getEmail().equals(email)) {
            throw new NoPrivilegeException("This token does not correspond to the email provided");
        }
        var dto = create(username, plainPassword, email, displayName, jobTitleId);
        if (invitation.getProjectId() != null) {
            membershipRepository.save(new Membership(dto.getId(), invitation.getProjectId(), false));
        }
        return dto;
    }

    /**
     * Change password of a specific user.
     *
     * @param id            ID of the user
     * @param plainPassword The password before hashing
     *
     * @throws UserNotFoundException                if the ID provided does not belong to any user
     * @throws PasswordRequirementsNotMeetException if the password provided does not satisfy all
     *                                              requirements about password.
     */
    public void changePassword(int id, @NonNull String plainPassword) {
        var user = userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
        if (plainPassword.length() < 8) {
            throw new PasswordRequirementsNotMeetException("Password must be 8 characters or longer");
        }
        user.setPassword(passwordEncoder.encode(plainPassword));
        userRepository.save(user);
    }

    public UserDto update(int id,
                          @NonNull String username,
                          @NonNull String displayName,
                          @NonNull String email,
                          Integer jobTitleId) {
        serviceHelper.ensureValidUserId(id);
        userRepository.findByEmail(email).ifPresent(userFound -> {
            if (id != userFound.getId()) {
                throw new DuplicatedEmailException(String.format(
                        "The email %s cannot be used because it belongs to an existing user", email
                ));
            }
        });
        var user = userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        var dto = UserDto.from(userRepository.save(user));
        var jobTakenOptional = jobTakenRepository.findByUserId(id);
        // End old job
        jobTakenOptional.ifPresent(jobTaken -> {
            jobTaken.setEndOn(ZonedDateTime.now());
            jobTakenRepository.save(jobTaken);
        });

        if (jobTitleId != null) {
            var jobTitle = jobTitleRepository
                    .findById(jobTitleId)
                    .orElseThrow(JobTitleNotFoundException::new);
            jobTakenRepository.save(new JobTaken(id, jobTitleId));
            dto.setJobTitle(JobTitleDto.from(jobTitle));
        }
        dto.setHashedPassword(null);
        dto.setPassword(null);
        return dto;
    }

    /**
     * Deactivate an account so it cannot be used to login anymore
     *
     * Password will be cleared when a user account is deactivated.
     *
     * @param id ID of the user to be deactivated
     *
     * @throws UserNotFoundException     if the ID provided does not belong to any user
     * @throws IllegalOperationException if the user being deactivated is the system admin
     */
    public UserDto deactivate(int id) {
        var user = userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
        if (user.isAdmin()) {
            throw new IllegalOperationException("System admin can't be deactivated");
        }
        user.setPassword("");
        user.setDeactivatedOn(ZonedDateTime.now());
        user.setImage(null);
        return UserDto.from(userRepository.save(user));
    }

    /**
     * Get information of the user with the username specified
     *
     * @param username Username of the user to search for
     *
     * @return Information of the user with the specified username
     *
     * @throws UserNotFoundException if the username provided does not belong to any user
     */
    public UserDto getByUsername(String username) {
        return UserDto.from(userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new)
        );
    }

    /**
     * Get information of a specific user
     *
     * @param id ID of the user to read
     *
     * @return Information of the specified user
     *
     * @throws UserNotFoundException if the ID provided does not belong to any user
     */
    public UserDto get(int id) {
        return UserDto.from(userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new)
        );
    }

    /**
     * Get information of all users in the system, including the ones deactivated.
     *
     * @return All users in the system
     */
    public List<UserDto> findAll() {
        return findAll(List.of());
    }

    /**
     * Add filters corresponding to the constraints. Ignore constraints on unknown fields.
     *
     * @param stream      The stream to add filters into
     * @param constraints The constraints to use for creating filters
     *
     * @return A new stream with filters corresponding to the constraints appended
     */
    private Stream<User> addConstraints(Stream<User> stream, List<SearchConstraint> constraints) {
        for (var constraint : constraints) {
            switch (constraint.getFieldName()) {
                case "username":
                    stream = stream.filter(user -> constraint.matches(user.getUsername()));
                    break;
                case "displayName":
                    stream = stream.filter(user -> constraint.matches(user.getDisplayName()));
                    break;
                case "deactivatedOn":
                    stream = stream.filter(user -> constraint.matches(user.getDeactivatedOn()));
                    break;
                default:
                    // Ignore unknown fields
            }
        }
        return stream;
    }

    /**
     * Get all users satisfying all constraints specified
     *
     * @param constraints The constraints to use
     *
     * @return List of users satisfying all the constraints specified
     */
    public List<UserDto> findAll(@NonNull List<SearchConstraint> constraints) {
        return addConstraints(userRepository.findAll().stream(), constraints)
                .map(user -> {
                    var dto = UserDto.from(user);
                    jobTitleRepository.findByUserId(user.getId()).ifPresent(jobTitle ->
                            dto.setJobTitle(JobTitleDto.from(jobTitle))
                    );
                    dto.setHashedPassword(null);
                    dto.setPassword(null);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Send an invitation (containing activation code) to the specified email
     *
     * @param inviterName  Name of the inviter
     * @param inviterEmail Email of the inviter
     * @param recipient    Email address of the invitee
     */
    public void invite(String inviterName, String inviterEmail, String recipient)
            throws NoSuchAlgorithmException {
        invite(inviterName, inviterEmail, recipient, null);
    }

    /**
     * Send an invitation (containing activation code) to the specified email,
     * with a predefined project to add new user into when they create an account.
     *
     * The activation code valid for 24 hours.
     *
     * @param inviterName  Name of the inviter
     * @param inviterEmail Email of the inviter
     * @param recipient    Email address of the invitee
     * @param projectId    ID of the project to add new user into. If this is set to null,
     *                     new user will see an empty system because they don't belong to
     *                     any project yet.
     */
    public void invite(String inviterName, String inviterEmail, String recipient, Integer projectId)
            throws NoSuchAlgorithmException {
        var token = Helper.sha512Hash(sessionService.generateToken().getBytes());
        // Activation code lives for 1 day
        var invitation = new Invitation(
                token, recipient, projectId,
                ZonedDateTime.now().plus(Duration.ofSeconds(24 * 60 * 60L))
        );
        invitationRepository.save(invitation);
        emailService.sendInvitation(inviterName, inviterEmail, recipient, invitation.getToken());
    }

    /**
     * Get the invitation information from the activation token
     *
     * @param token The activation token
     *
     * @return Information of the invitation corresponding to the provided activation token
     *
     * @throws InvitationNotFoundException if the token provided does not belong to any invitation
     */
    public InvitationDto getInvitation(String token) {
        return InvitationDto.from(invitationRepository
                .findById(token)
                .orElseThrow(InvitationNotFoundException::new));
    }
}
