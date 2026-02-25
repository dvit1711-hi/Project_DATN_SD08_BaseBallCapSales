package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PostAddressDto {

    @Size(max = 20)
    @Nationalized
    private String unitNumber;

    @Size(max = 20)
    @Nationalized
    private String streetNumber;

    @Size(max = 255)
    @Nationalized
    private String addressLine1;

    @Size(max = 255)
    @Nationalized
    private String addressLine2;

    @Size(max = 100)
    @Nationalized
    private String city;

    @Size(max = 100)
    @Nationalized
    private String region;

    @Size(max = 20)
    @Nationalized
    private String postalCode;

    private Integer accountID;

}
