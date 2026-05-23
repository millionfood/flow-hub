package com.ajh.flow.dto.history;

import com.ajh.flow.domain.UserHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class UserHistoryDetailDto {

    public UserHistoryDetailDto(UserHistory history) {
        this.id = history.getId();
        this.adminName = history.getAdmin().getName();
        this.adminEmail = history.getAdmin().getEmail();
        this.targetUserName = history.getTargetUser().getName();
        this.targetUserEmail = history.getTargetUser().getEmail();
        this.remark = history.getRemark();
        this.type = history.getType().name();
        this.createdDateStr =  history.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private Long id;
    private String adminName;
    private String adminEmail;
    private String targetUserName;
    private String targetUserEmail;

    private String remark;
    private String type;
    private String createdDateStr;


}
