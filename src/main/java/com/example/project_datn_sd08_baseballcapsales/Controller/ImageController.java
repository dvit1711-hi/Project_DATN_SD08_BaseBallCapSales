package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.ImageRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
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
    @Autowired
    private ProductColorRepository productColorRepository;
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping
    public List<GetImageDto> getAll() {
        return imageService.getAllImages();
    }
    @GetMapping("/product-color/{id}")
    public List<Image> getByProductColor(@PathVariable Integer id){
        return imageService.getImagesByProductColor(id);
    }

    @PostMapping("/color/{productColorId}/image")
    public ResponseEntity<Image> addImage(
            @PathVariable Integer productColorId,
            @RequestBody PostImageDto dto) {

        if (productColorId == null || dto.getImageUrl() == null) {
            throw new IllegalArgumentException("productColorId and imageUrl must not be null");
        }

        ProductColor pc = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm"));

        List<Image> existingImages = imageRepository.findByProductColorID_Id(productColorId);
        if (existingImages.size() >= 5) {
            throw new RuntimeException("Chỉ được tối đa 5 ảnh");
        }

        Image image = new Image();
        image.setProductColorID(pc);
        image.setImageUrl(dto.getImageUrl());

        boolean hasMain = imageRepository.existsByProductColorIDAndIsMainTrue(pc);
        image.setIsMain(!hasMain);

        Image saved = imageRepository.save(image);
        return ResponseEntity.ok(saved);
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
    @DeleteMapping("/product-color/{productColorId}")
    public ResponseEntity<?> deleteAllImagesByProductColor(@PathVariable Integer productColorId) {
        boolean deleted = imageService.deleteAllImagesByProductColorID(productColorId);
        if (!deleted) {
            return ResponseEntity.status(404).body("No images found for this product color");
        }
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/set-main")
    public ResponseEntity<?> setMainImage(@PathVariable Integer id) {
        Image img = imageService.setMainImage(id);
        if (img == null) {
            return ResponseEntity.status(404).body("Image not found");
        }
        return ResponseEntity.ok(img);
    }
}
