package cis.co.kr.ciscultureinseoul.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchDateEvent {
    private String startday;
    private String endday;
}
