package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostSizeDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetSizeDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.SizeEntity;
import com.example.project_datn_sd08_baseballcapsales.Service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/size")
public class SizeController {

    @Autowired
    private SizeService sizeService;

    @GetMapping
    public List<GetSizeDto> getAll() {
        return sizeService.getAll();
    }

    @GetMapping("/active")
    public List<GetSizeDto> getAllActive() {
        return sizeService.getAllActive();
    }

    @GetMapping("/{id}")
    public SizeEntity getById(@PathVariable Integer id) {
        return sizeService.getById(id);
    }

    @PostMapping
    public ResponseEntity<SizeEntity> create(@RequestBody PostSizeDto dto) {
        return ResponseEntity.ok(sizeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SizeEntity> update(@PathVariable Integer id,
                                             @RequestBody PostSizeDto dto) {
        return ResponseEntity.ok(sizeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(sizeService.delete(id));
    }
}