package cis.co.kr.ciscultureinseoul.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardImage {
    private Long id;
    private String ori_name;
    private String save_name;
    private Long board_id;
}
