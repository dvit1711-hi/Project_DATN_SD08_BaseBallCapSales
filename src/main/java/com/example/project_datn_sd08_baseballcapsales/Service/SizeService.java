package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostSizeDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetSizeDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.SizeEntity;
import com.example.project_datn_sd08_baseballcapsales.Repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    public List<GetSizeDto> getAll() {
        return sizeRepository.findAll()
                .stream()
                .map(GetSizeDto::new)
                .toList();
    }

    public SizeEntity getById(Integer id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));
    }

    public SizeEntity create(PostSizeDto dto) {
        if (dto.getSizeName() == null || dto.getSizeName().isBlank()) {
            throw new IllegalArgumentException("Tên size không được để trống");
        }

        SizeEntity size = new SizeEntity();
        size.setSizeName(dto.getSizeName());
        size.setSizeDescription(dto.getSizeDescription());

        return sizeRepository.save(size);
    }

    public SizeEntity update(Integer id, PostSizeDto dto) {
        SizeEntity size = sizeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));

        if (dto.getSizeName() == null || dto.getSizeName().isBlank()) {
            throw new IllegalArgumentException("Tên size không được để trống");
        }

        size.setSizeName(dto.getSizeName());
        size.setSizeDescription(dto.getSizeDescription());

        return sizeRepository.save(size);
    }

    public void delete(Integer id) {
        SizeEntity size = sizeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));
        sizeRepository.delete(size);
    }
}