package com.tpssoft.hham.service;

import com.tpssoft.hham.dto.VoteDto;
import com.tpssoft.hham.entity.Vote;
import com.tpssoft.hham.entity.VoteId;
import com.tpssoft.hham.exception.ActivityNotFoundException;
import com.tpssoft.hham.exception.VoteNotFoundException;
import com.tpssoft.hham.repository.ActivityRepository;
import com.tpssoft.hham.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ActivityRepository activityRepository;
    private final ServiceHelper serviceHelper;

    private Stream<Vote> addConstraints(Stream<Vote> stream, List<SearchConstraint> constraints) {
        for (var constraint : constraints) {
            switch (constraint.getFieldName()) {
                case "userId":
                    stream = stream.filter(vote -> constraint.matches(vote.getUserId()));
                    break;
                case "activityId":
                    stream = stream.filter(vote -> constraint.matches(vote.getOption().getActivity().getId()));
                    break;
                default:
                    break;
            }
        }
        return stream;
    }

    /**
     * get All votes in system
     * @param constraints
     * @return
     */
    public List<VoteDto> getAll(SearchConstraints constraints) {
        return addConstraints(voteRepository.findAll().stream(), constraints.getConstraints())
                .map(VoteDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Create a new vote, optionally with a note
     *
     * @param userId   ID of the user voted for the option
     * @param optionId ID of the option
     * @param note     The note about this vote, not required
     *
     * @return Information of the vote created
     */
    public VoteDto create(int userId, int optionId, String note) {
        var activityId = activityRepository
                .findByOptionId(optionId)
                .orElseThrow(ActivityNotFoundException::new)
                .getId();
        // Remove existing vote
        voteRepository
                .findAllByUserId(userId)
                .stream()
                .filter(vote -> vote.getOption().getActivity().getId().equals(activityId))
                .forEach(voteRepository::delete);
        var vote = new Vote();
        vote.setOptionId(optionId);
        vote.setUserId(userId);
        vote.setNote(note);
        return VoteDto.from(voteRepository.save(vote));
    }

    /**
     * Delete an existing vote
     *
     * @param userId   ID of the voted user
     * @param optionId ID of the option being voted
     *
     * @return Information of the deleted vote
     *
     * @throws UserNotFoundException   if the user ID does not belong to a real user
     * @throws OptionNotFoundException if the option ID does not belong to a real option
     * @throws VoteNotFoundException   if the user didn't vote for the option
     */
    public void delete(int userId, int optionId) {
        serviceHelper.ensureValidUserId(userId);
        serviceHelper.ensureValidOptionId(optionId);
        var vote = voteRepository
                .findById(new VoteId(userId, optionId))
                .orElseThrow(VoteNotFoundException::new);
        voteRepository.delete(vote);
    }
}
