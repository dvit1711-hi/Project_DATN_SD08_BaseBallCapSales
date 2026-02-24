package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.GetImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutImageDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.ImageRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class imageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    public List<GetImageDto> getAllImages() {
        return imageRepository.findAll().stream()
                .map(GetImageDto::new)
                .toList();
    }

    public Image postImage(PostImageDto dto) {

        ProductColor pc = productColorRepository.findById(dto.getProductColorID())
                .orElse(null);
        if (pc == null) return null;

        Image image = new Image();
        image.setProductColorID(pc);
        image.setImageUrl(dto.getImageUrl());
        image.setIsMain(dto.getIsMain() != null ? dto.getIsMain() : false);

        return imageRepository.save(image);
    }

    public Image putImage(Integer id, PutImageDto dto) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) return null;

        if (dto.getImageUrl() != null)
            image.setImageUrl(dto.getImageUrl());

        if (dto.getIsMain() != null)
            image.setIsMain(dto.getIsMain());

        return imageRepository.save(image);
    }

    public boolean deleteImage(Integer id) {
        if (!imageRepository.existsById(id))
            return false;
        imageRepository.deleteById(id);
        return true;
    }
}
