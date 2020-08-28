package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private String digest;
    private String type;
    private String base64Content;
    private String url;

    public static ImageDto from(Image image) {
        var dto = new ImageDto();
        dto.setDigest(image.getDigest());
        dto.setType(image.getType());
        dto.setBase64Content(image.getContent());
        dto.setUrl("/images/" + dto.getDigest());
        return dto;
    }
}
