package com.skillswap.form;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 2000)
    private String bio;

    @Size(max = 500)
    private String avatarUrl;
}
