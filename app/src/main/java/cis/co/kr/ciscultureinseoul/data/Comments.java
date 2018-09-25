package cis.co.kr.ciscultureinseoul.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comments {
    private Long id;
    private String comments_contents;
    private String comments_nick;
    private String comments_time;
    private Long board_id;
}
