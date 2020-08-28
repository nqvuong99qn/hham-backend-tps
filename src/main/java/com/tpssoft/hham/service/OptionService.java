package com.tpssoft.hham.service;

import com.tpssoft.hham.dto.ImageDto;
import com.tpssoft.hham.dto.OptionDto;
import com.tpssoft.hham.entity.Activity;
import com.tpssoft.hham.entity.Option;
import com.tpssoft.hham.exception.ActivityNotFoundException;
import com.tpssoft.hham.exception.OptionNotFoundException;
import com.tpssoft.hham.repository.OptionRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor
public class OptionService {
    private final ServiceHelper serviceHelper;
    private final OptionRepository optionRepository;
    private final ImageService imageService;

    /**
     * Create a new option using the information provided
     *
     * @param name        Name for the new option
     * @param price       Price for the new option
     * @param activityId  ID of the activity the new option belongs to
     * @param imageDigest Digest of the image for this option (not required)
     *
     * @return Information of the option created
     *
     * @throws ActivityNotFoundException if the activity ID provided in the DTO
     *                                   does not belong to any activity
     */
    public OptionDto create(@NonNull String name,
                            @NonNull BigDecimal price,
                            int activityId,
                            String imageDigest) {
        serviceHelper.ensureValidActivityId(activityId);
        var option = new Option();
        option.setName(name);
        option.setPrice(price);
        option.setActivity(new Activity());
        option.getActivity().setId(activityId);
        var dto = OptionDto.from(optionRepository.save(option));
        if (imageDigest != null) {
            dto.setImage(imageService.getByDigest(imageDigest));
        }
        return dto;
    }

    /**
     * Get information of a specific option
     *
     * @param id ID of the option to read
     *
     * @return Information of the specified option
     */
    public OptionDto get(int id) {
        var option = optionRepository
                .findById(id)
                .orElseThrow(OptionNotFoundException::new);
        var dto = OptionDto.from(option);
        if (option.getImage() != null) {
            dto.setImage(ImageDto.from(option.getImage()));
        }
        return dto;
    }

    private static Stream<Option> addConstraints(Stream<Option> stream,
                                                 List<SearchConstraint> constraints) {
        for (var constraint : constraints) {
            switch (constraint.getFieldName()) {
                case "activityId":
                    stream = stream.filter(option -> constraint.matches(option.getActivity().getId()));
                    break;
                default:
                    break;
            }
        }
        return stream;
    }

    public List<OptionDto> findAll(List<SearchConstraint> constraints) {
        return addConstraints(optionRepository.findAll().stream(), constraints)
                .map(option -> {
                    var dto = OptionDto.from(option);
                    if (option.getImage() != null) {
                        dto.setImage(ImageDto.from(option.getImage()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Update information (except image) of an option
     *
     * Only non-null values are used as new value. So if you want to
     * leave a field unchanged, pass null to it.
     *
     * @param id    ID of the option to edit
     * @param name  New name for the option
     * @param price New price for the option
     *
     * @return Information of the updated option
     *
     * @throws OptionNotFoundException if the ID provided does not belong to any option
     */
    public OptionDto update(int id, String name, BigDecimal price) {
        var option = optionRepository
                .findById(id)
                .orElseThrow(OptionNotFoundException::new);
        if (name != null) {
            option.setName(name);
        }
        if (price != null) {
            option.setPrice(price);
        }
        option = optionRepository.save(option);
        // Image update is in ImageService, so we don't handle it here
        var dto = OptionDto.from(option);
        if (option.getImage() != null) {
            dto.setImage(imageService.getForOption(option.getId()));
        }
        return dto;
    }

    /**
     * Delete the option with the specified ID
     *
     * @param id ID of the option to delete
     *
     * @return Information of the deleted option
     *
     * @throws OptionNotFoundException if the ID provided does not belong to any option
     */
    public OptionDto delete(int id) {
        var dto = OptionDto.from(optionRepository
                .findById(id)
                .orElseThrow(OptionNotFoundException::new));
        optionRepository.deleteById(id);
        return dto;
    }
}
