package cis.co.kr.ciscultureinseoul.data;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchConcertPlace {
    private Integer list_total_count;
    private RESULT RESULT;
    private ArrayList<Row> row;
}
