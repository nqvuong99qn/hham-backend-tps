package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.UrlWrapper;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.service.ImageService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@AllArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("images/{digest:[0-9A-Fa-f]+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public ResponseEntity<byte[]> get(@PathVariable String digest) {
        var dto = imageService.get(digest);
        byte[] content = Base64.decodeBase64(dto.getBase64Content());
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(dto.getType()))
                .contentLength(content.length)
                .body(content);
    }

    @GetMapping("/api/images/user/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getAvatar(@PathVariable int id) {
        return new SuccessResponse().put("data", imageService.getForUser(id));
    }

    @PostMapping(value = "/api/images/user/{id:\\d+}", consumes = "multipart/form-data")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse postAvatar(@RequestParam("file") MultipartFile file, @PathVariable int id)
            throws IOException, NoSuchAlgorithmException {
        return new SuccessResponse().put("data", imageService.saveForUser(file, id));
    }

    @PostMapping(value = "/api/images/user/{id:\\d+}", consumes = "application/json")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse postAvatarUrl(@RequestBody UrlWrapper wrapper, @PathVariable int id)
            throws IOException, NoSuchAlgorithmException {
        return new SuccessResponse().put("data", imageService.downloadAndSaveForUser(wrapper.getUrl(), id));
    }

    @GetMapping("/api/images/project/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getProjectLogo(@PathVariable int id) {
        return new SuccessResponse().put("data", imageService.getForProject(id));
    }

    @PostMapping(value = "/api/images/project/{id:\\d+}", consumes = "multipart/form-data")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse postProjectLogo(@RequestParam("file") MultipartFile file, @PathVariable int id)
            throws IOException, NoSuchAlgorithmException {
        return new SuccessResponse().put("data", imageService.saveForProject(file, id));
    }

    @PostMapping(value = "/api/images/project/{id:\\d+}", consumes = "application/json")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse postProjectLogoUrl(@RequestBody UrlWrapper wrapper, @PathVariable int id)
            throws IOException, NoSuchAlgorithmException {
        return new SuccessResponse().put("data", imageService.downloadAndSaveForProject(wrapper.getUrl(), id));
    }

    @GetMapping("/api/images/option/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getOptionImage(@PathVariable int id) {
        return new SuccessResponse().put("data", imageService.getForOption(id));
    }

    @PostMapping(value = "/api/images/option/{id:\\d+}", consumes = "multipart/form-data")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse postOptionImage(@RequestParam("file") MultipartFile file, @PathVariable int id)
            throws IOException, NoSuchAlgorithmException {
        return new SuccessResponse().put("data", imageService.saveForOption(file, id));
    }

    @PostMapping(value = "/api/images/option/{id:\\d+}", consumes = "application/json")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse postOptionImageUrl(@RequestBody UrlWrapper wrapper, @PathVariable int id)
            throws IOException, NoSuchAlgorithmException {
        return new SuccessResponse().put("data", imageService.downloadAndSaveForOption(wrapper.getUrl(), id));
    }
}
