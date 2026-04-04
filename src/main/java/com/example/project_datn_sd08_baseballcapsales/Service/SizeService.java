package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostSizeDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetSizeDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.SizeEntity;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.SizeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    public List<GetSizeDto> getAll() {
        return sizeRepository.findAll()
                .stream()
                .map(GetSizeDto::new)
                .toList();
    }

    public List<GetSizeDto> getAllActive() {
        return sizeRepository.findByStatus("ACTIVE")
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

        if (sizeRepository.existsBySizeNameIgnoreCase(dto.getSizeName().trim())) {
            throw new IllegalArgumentException("Size đã tồn tại");
        }

        SizeEntity size = new SizeEntity();
        size.setSizeName(dto.getSizeName().trim());
        size.setSizeDescription(dto.getSizeDescription());
        size.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "ACTIVE" : dto.getStatus().trim());

        return sizeRepository.save(size);
    }

    public SizeEntity update(Integer id, PostSizeDto dto) {
        SizeEntity size = sizeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));

        if (dto.getSizeName() == null || dto.getSizeName().isBlank()) {
            throw new IllegalArgumentException("Tên size không được để trống");
        }

        size.setSizeName(dto.getSizeName().trim());
        size.setSizeDescription(dto.getSizeDescription());
        size.setStatus(dto.getStatus());
        return sizeRepository.save(size);
    }

    @Transactional
    public String delete(Integer id) {
        SizeEntity size = sizeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));

        boolean used = productColorRepository.existsBySizeID_SizeID(id);

        if (used) {
            size.setStatus("INACTIVE");
            sizeRepository.save(size);
            return "Size đang được sử dụng, đã chuyển sang INACTIVE";
        }

        sizeRepository.delete(size);
        return "Đã xóa size";
    }
}