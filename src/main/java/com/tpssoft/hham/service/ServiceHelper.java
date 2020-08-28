package com.tpssoft.hham.service;

import com.tpssoft.hham.exception.ActivityNotFoundException;
import com.tpssoft.hham.exception.OptionNotFoundException;
import com.tpssoft.hham.exception.ProjectNotFoundException;
import com.tpssoft.hham.exception.UserNotFoundException;
import com.tpssoft.hham.repository.ActivityRepository;
import com.tpssoft.hham.repository.OptionRepository;
import com.tpssoft.hham.repository.ProjectRepository;
import com.tpssoft.hham.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.loadtime.Options;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ServiceHelper {
    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;

    public void ensureValidProjectId(int id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException();
        }
    }

    public void ensureValidUserId(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
    }

    public void ensureValidActivityId(int id) {
        if (!activityRepository.existsById(id)) {
            throw new ActivityNotFoundException();
        }
    }

    public void ensureValidOptionId(int id) {
        if (!optionRepository.existsById(id)) {
            throw new OptionNotFoundException();
        }
    }
}
