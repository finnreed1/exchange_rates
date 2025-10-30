package project.entity;

import lombok.*;

@Data
@AllArgsConstructor
public class Currency {
    private int id;
    private String code;
    private String fullname;
    private String sign;

}
