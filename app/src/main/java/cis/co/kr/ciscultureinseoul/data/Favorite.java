package cis.co.kr.ciscultureinseoul.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Favorite {
    private Integer code;
    private Long member_id;
}
