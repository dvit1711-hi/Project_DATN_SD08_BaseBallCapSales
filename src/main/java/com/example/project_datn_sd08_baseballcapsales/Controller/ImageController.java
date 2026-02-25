package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Service.imageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private imageService imageService;

    @GetMapping
    public List<GetImageDto> getAll() {
        return imageService.getAllImages();
    }

    @PostMapping
    public ResponseEntity<?> postImage(@Valid @RequestBody PostImageDto dto) {
        Image img = imageService.postImage(dto);
        if (img == null) {
            return ResponseEntity.badRequest().body("Invalid productColorID");
        }
        return ResponseEntity.ok(img);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putImage(
            @PathVariable Integer id,
            @RequestBody PutImageDto dto
    ) {
        Image img = imageService.putImage(id, dto);
        if (img == null) {
            return ResponseEntity.status(404).body("Image not found");
        }
        return ResponseEntity.ok(img);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer id) {
        boolean deleted = imageService.deleteImage(id);
        if (!deleted) {
            return ResponseEntity.status(404).body("Image not found");
        }
        return ResponseEntity.noContent().build();
    }
}
