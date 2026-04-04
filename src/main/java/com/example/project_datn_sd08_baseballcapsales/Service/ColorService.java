package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import com.example.project_datn_sd08_baseballcapsales.Repository.ColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    public List<GetColorDto> getAllColors() {
        return colorRepository.findAll()
                .stream()
                .map(GetColorDto::new)
                .toList();
    }

    public List<GetColorDto> getActiveColors() {
        return colorRepository.findByStatus("ACTIVE")
                .stream()
                .map(GetColorDto::new)
                .toList();
    }

    public Color postColor(PostColorDto dto) {
        if (dto.getColorName() == null || dto.getColorName().isBlank()) {
            throw new IllegalArgumentException("Tên màu không được để trống");
        }

        Color color = new Color();
        color.setColorName(dto.getColorName().trim());
        color.setColorCode(dto.getColorCode() != null ? dto.getColorCode().trim() : null);
        color.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "ACTIVE" : dto.getStatus().trim());

        return colorRepository.save(color);
    }

    public Color putColor(Integer id, PutColorDto dto) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));

        if (dto.getColorName() == null || dto.getColorName().isBlank()) {
            throw new IllegalArgumentException("Tên màu không được để trống");
        }

        color.setColorName(dto.getColorName().trim());
        color.setColorCode(dto.getColorCode() != null ? dto.getColorCode().trim() : null);
        color.setStatus(dto.getStatus().trim());
        return colorRepository.save(color);
    }

    @Transactional
    public String deleteColor(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));

        boolean used = productColorRepository.existsByColorID_Id(id);

        if (used) {
            color.setStatus("INACTIVE");
            colorRepository.save(color);
            return "Color đang được sử dụng, đã chuyển sang INACTIVE";
        }

        colorRepository.delete(color);
        return "Đã xóa color";
    }
}