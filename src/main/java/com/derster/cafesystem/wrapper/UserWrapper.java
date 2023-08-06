package com.derster.cafesystem.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWrapper {
    private Integer id;
    private String name;
    private String contactNumber;
    private String email;
    private boolean status;
}
