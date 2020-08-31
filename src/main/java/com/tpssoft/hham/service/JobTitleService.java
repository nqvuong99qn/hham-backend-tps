package com.tpssoft.hham.service;

import com.tpssoft.hham.dto.JobTitleDto;
import com.tpssoft.hham.entity.JobTitle;
import com.tpssoft.hham.exception.DuplicatedNameException;
import com.tpssoft.hham.exception.JobTitleNotFoundException;
import com.tpssoft.hham.exception.ResourceNotFoundException;
import com.tpssoft.hham.repository.JobTitleRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class JobTitleService {
    private final JobTitleRepository jobTitleRepository;

    /**
     * Create new job title from the information provided in the data transfer object
     *
     * @param name          Name of the job title
     * @param monthlyAmount The funding amount required each month
     *
     * @return Information of the new job title
     */
    public JobTitleDto create(@NonNull String name, @NonNull BigDecimal monthlyAmount) {
        if (jobTitleRepository.existsByName(name)) {
            throw new DuplicatedNameException(
                    String.format("The job title %s cannot be used because it already exists", name)
            );
        }
        var jobTitle = new JobTitle();
        jobTitle.setName(name);
        jobTitle.setMonthlyAmount(monthlyAmount);
        return JobTitleDto.from(jobTitleRepository.save(jobTitle));
    }

    /**
     * Get information of a specific job title
     *
     * @param id ID of the job title to read
     *
     * @return Information of the specified job title
     *
     * @throws JobTitleNotFoundException if the ID provided does not belong to any job title
     */
    public JobTitleDto getOne(int id){
        return JobTitleDto.from(jobTitleRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    /**
     * Get all job titles in the system
     *
     * @return All job titles in the system, even the ones not being used anymore
     */
    public List<JobTitleDto> findAll() {
        return jobTitleRepository.findAll().stream().map(JobTitleDto::from).collect(Collectors.toList());
    }

    /**
     * Add filters corresponding to the constraints provided
     *
     * Constraints with unknown field name are ignored.
     *
     * @param stream      The stream to add filters to
     * @param constraints The constraints to use when creating filter
     *
     * @return A new stream with filters correctly appended
     */
    private Stream<JobTitle> addConstraints(Stream<JobTitle> stream,List<SearchConstraint> constraints) {
        for (var constraint : constraints) {
            switch (constraint.getFieldName()) {
                case "name":
                    stream = stream.filter(jobTitle -> constraint.matches(jobTitle.getName()));
                    break;
                case "monthlyAmount":
                    // TODO: Generalize this so < and > can be used instead of just equals
                    stream = stream.filter(jobTitle -> constraint.matches(jobTitle.getMonthlyAmount()));
                    break;
                case "archivedOn":
                    stream = stream.filter(jobTitle -> constraint.matches(jobTitle.getArchivedOn()));
                    break;
                default:
                    // Ignore unknown fields
            }
        }
        return stream;
    }

    /**
     * Find all job titles satisfying all the given constraints.
     *
     * @param constraints The constraints to filter the result set
     *
     * @return All job titles satisfying the constraints
     */
    public List<JobTitleDto> findAll(SearchConstraints constraints) {
        return addConstraints(jobTitleRepository.findAll().stream(), constraints.getConstraints())
                .map(JobTitleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Update information of a specific job title
     *
     * @param id            ID of the job title to edit
     * @param name          New name for the job title
     * @param monthlyAmount New required monthly amount for the job title
     *
     * @return Information of the job title after the update
     *
     * @throws JobTitleNotFoundException if the ID provided does not belong to any job title
     */
    public JobTitleDto update(int id, String name, BigDecimal monthlyAmount) {
        var jobTitle = jobTitleRepository.findById(id).orElseThrow(JobTitleNotFoundException::new);
        jobTitle.setName(name);
        jobTitle.setMonthlyAmount(monthlyAmount);
        return JobTitleDto.from(jobTitleRepository.save(jobTitle));
    }

    /**
     * Archive a job title not being in use.
     *
     * @param id ID of the job title to archive
     *
     * @return Information of the job title after the archival
     *
     * @throws JobTitleNotFoundException if the ID provided does not belong to any job title
     */
    public JobTitleDto archive(int id) {
        var jobTitle = jobTitleRepository.findById(id).orElseThrow(JobTitleNotFoundException::new);
        jobTitle.setArchivedOn(ZonedDateTime.now());
        return JobTitleDto.from(jobTitleRepository.save(jobTitle));
    }
}
