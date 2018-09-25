package cis.co.kr.ciscultureinseoul.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {
    private Long id;
    private String board_title;
    private String board_contents;
    private String board_nick;
    private Integer comments_count;
}
