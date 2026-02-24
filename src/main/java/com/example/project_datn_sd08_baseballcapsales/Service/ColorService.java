package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import com.example.project_datn_sd08_baseballcapsales.Repository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {

    @Autowired
    private ColorRepository colorRepository;

    public List<GetColorDto> getAllColors() {
        return colorRepository.findAll()
                .stream()
                .map(GetColorDto::new)
                .toList();
    }

    public Color postColor(PostColorDto dto) {
        Color color = new Color();
        color.setColorName(dto.getColorName());
        color.setColorCode(dto.getColorCode());
        return colorRepository.save(color);
    }

    public Color putColor(Integer id, PutColorDto dto) {
        Color color = colorRepository.findById(id).orElse(null);
        if (color == null) return null;

        color.setColorName(dto.getColorName());
        color.setColorCode(dto.getColorCode());
        return colorRepository.save(color);
    }

    public boolean deleteColor(Integer id) {
        if (!colorRepository.existsById(id)) {
            return false;
        }
        colorRepository.deleteById(id);
        return true;
    }
}
