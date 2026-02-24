package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import com.example.project_datn_sd08_baseballcapsales.Service.ColorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/color")
public class ColorController {

    @Autowired
    private ColorService colorService;

    @GetMapping
    public List<GetColorDto> getAll() {
        return colorService.getAllColors();
    }

    @PostMapping
    public ResponseEntity<?> postColor(@Valid @RequestBody PostColorDto dto) {
        Color color = colorService.postColor(dto);
        return ResponseEntity.ok(color);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putColor(@PathVariable Integer id,
                                      @Valid @RequestBody PutColorDto dto) {
        Color color = colorService.putColor(id, dto);
        if (color == null) {
            return ResponseEntity.status(404).body("Color not found");
        }
        return ResponseEntity.ok(color);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColor(@PathVariable Integer id) {
        boolean deleted = colorService.deleteColor(id);
        if (!deleted) {
            return ResponseEntity.status(404).body("Color not found");
        }
        return ResponseEntity.noContent().build();
    }
}
