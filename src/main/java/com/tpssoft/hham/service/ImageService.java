package com.tpssoft.hham.service;

import com.tpssoft.hham.Helper;
import com.tpssoft.hham.dto.ImageDto;
import com.tpssoft.hham.entity.Image;
import com.tpssoft.hham.exception.ImageNotFoundException;
import com.tpssoft.hham.exception.OptionNotFoundException;
import com.tpssoft.hham.exception.ProjectNotFoundException;
import com.tpssoft.hham.exception.UserNotFoundException;
import com.tpssoft.hham.repository.ImageRepository;
import com.tpssoft.hham.repository.OptionRepository;
import com.tpssoft.hham.repository.ProjectRepository;
import com.tpssoft.hham.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final OptionRepository optionRepository;

    private static Image prepareImage(@NonNull String urlString)
            throws IOException, NoSuchAlgorithmException {
        var url = new URL(urlString);
        var connection = (HttpURLConnection) url.openConnection();
        // Set timeout so it does not wait forever
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed downloading image at '" + urlString + "'");
        }
        var image = new Image();
        image.setType(connection.getContentType());
        try (var outStream = new ByteArrayOutputStream()) {
            var chunk = new byte[4096]; // Read 4KiB each time
            int bytesRead;
            var inStream = connection.getInputStream();
            while ((bytesRead = inStream.read(chunk)) > 0) {
                outStream.write(chunk, 0, bytesRead);
            }
            var content = outStream.toByteArray();
            image.setDigest(Helper.sha512Hash(content));
            image.setContent(Base64.encodeBase64String(content));
            return image;
        }
    }

    private static Image prepareImage(@NonNull MultipartFile file)
            throws NoSuchAlgorithmException, IOException {
        var content = file.getBytes();
        var image = new Image();
        image.setDigest(Helper.sha512Hash(content));
        image.setType(file.getContentType());
        image.setContent(Base64.encodeBase64String(content));
        return image;
    }

    public ImageDto saveForUser(MultipartFile file, int id) throws NoSuchAlgorithmException, IOException {
        var image = imageRepository.save(prepareImage(file));
        var user = userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
        user.setImage(image);
        userRepository.save(user);
        return ImageDto.from(imageRepository.save(image));
    }

    public ImageDto saveForProject(MultipartFile file, int id) throws IOException, NoSuchAlgorithmException {
        var image = imageRepository.save(prepareImage(file));
        var project = projectRepository
                .findById(id)
                .orElseThrow(ProjectNotFoundException::new);
        project.setImage(image);
        projectRepository.save(project);
        return ImageDto.from(imageRepository.save(image));
    }

    public ImageDto saveForOption(MultipartFile file, int id) throws IOException, NoSuchAlgorithmException {
        var image = imageRepository.save(prepareImage(file));
        var option = optionRepository
                .findById(id)
                .orElseThrow(OptionNotFoundException::new);
        option.setImage(image);
        optionRepository.save(option);
        return ImageDto.from(imageRepository.save(image));
    }

    public ImageDto getForUser(int id) {
        return ImageDto.from(imageRepository
                .findByUserId(id)
                .orElseThrow(ImageNotFoundException::new));
    }

    public ImageDto getForProject(int id) {
        return ImageDto.from(imageRepository
                .findByProjectId(id)
                .orElseThrow(ImageNotFoundException::new));
    }

    public ImageDto getForOption(int id) {
        return ImageDto.from(imageRepository
                .findByOptionId(id)
                .orElseThrow(ImageNotFoundException::new));
    }

    public ImageDto downloadAndSaveForUser(String url, int id) throws IOException, NoSuchAlgorithmException {
        var image = imageRepository.save(prepareImage(url));
        var user = userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
        user.setImage(image);
        userRepository.save(user);
        return ImageDto.from(imageRepository.save(image));
    }

    public ImageDto downloadAndSaveForProject(String url, int id) throws IOException, NoSuchAlgorithmException {
        var image = imageRepository.save(prepareImage(url));
        var project = projectRepository
                .findById(id)
                .orElseThrow(OptionNotFoundException::new);
        project.setImage(image);
        projectRepository.save(project);
        return ImageDto.from(imageRepository.save(image));
    }

    public ImageDto downloadAndSaveForOption(String url, int id) throws IOException, NoSuchAlgorithmException {
        var image = imageRepository.save(prepareImage(url));
        var option = optionRepository
                .findById(id)
                .orElseThrow(OptionNotFoundException::new);
        option.setImage(image);
        optionRepository.save(option);
        return ImageDto.from(imageRepository.save(image));
    }

    public ImageDto getByDigest(String digest) {
        return ImageDto.from(imageRepository
                .findById(digest)
                .orElseThrow(ImageNotFoundException::new));
    }

    public ImageDto get(String digest) {
        return ImageDto.from(imageRepository
                .findById(digest)
                .orElseThrow(ImageNotFoundException::new));
    }
}
