
package com.ems.dto;

import lombok.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO implements Serializable {
    private String type;
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;
    private String content;
    private String status;
}
