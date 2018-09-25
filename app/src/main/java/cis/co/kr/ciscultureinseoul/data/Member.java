package cis.co.kr.ciscultureinseoul.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {
    private Long id;
    private String member_id;
    private String member_pw;
    private String member_nickname;
}