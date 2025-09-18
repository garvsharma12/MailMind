package com.draig.mailmind_sb;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;
}
