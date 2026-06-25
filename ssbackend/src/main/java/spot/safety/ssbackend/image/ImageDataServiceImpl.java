package spot.safety.ssbackend.image;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.user.UserPrincipal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageDataServiceImpl implements ImageDataService {

    private final ImageRepository imageRepository;

    @Value("${images.storage.path:./data/images}")
    private String storageRoot;

    private Path rootPath() {
        return Path.of(storageRoot).toAbsolutePath().normalize();
    }

    @Override
    public void store(Long id, MultipartFile file, UserPrincipal actor) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + id));

        // only uploader or admin can store/replace binary
        boolean isOwner = image.getUploadedBy().getId().equals(actor.id());
        boolean isAdmin = actor.role().name().equals("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Not image owner");
        }

        try {
            Path root = rootPath();
            Files.createDirectories(root);

            Path dest = root.resolve(String.valueOf(id));
            try (var in = file.getInputStream()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }

            // update imageUrl to point to our data endpoint
            image.setImageUrl("/api/v1/images/" + id + "/data");
            imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file for image: " + id, e);
        }
    }

    @Override
    public void storeBytes(Long id, byte[] data, String filename, UserPrincipal actor) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + id));

        // only uploader or admin can store/replace binary
        boolean isOwner = image.getUploadedBy().getId().equals(actor.id());
        boolean isAdmin = actor.role().name().equals("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Not image owner");
        }

        try {
            Path root = rootPath();
            Files.createDirectories(root);

            Path dest = root.resolve(String.valueOf(id));
            Files.write(dest, data);

            // update imageUrl to point to our data endpoint
            image.setImageUrl("/api/v1/images/" + id + "/data");
            imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file for image: " + id, e);
        }
    }

    @Override
    public Resource loadAsResource(Long id) {
        Path file = rootPath().resolve(String.valueOf(id));
        if (!Files.exists(file)) {
            throw new EntityNotFoundException("Image data not found: " + id);
        }
        try {
            return new UrlResource(file.toUri());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read image data: " + id, e);
        }
    }
}


