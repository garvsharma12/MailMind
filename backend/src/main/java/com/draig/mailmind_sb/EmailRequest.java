package com.draig.mailmind_sb;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;
    // Optional: desired reply length — acceptable values: "short", "medium", "long"
    private String length;
}
