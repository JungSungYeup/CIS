package cis.co.kr.ciscultureinseoul.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Row implements Serializable {
    private Integer CULTCODE;
    private Integer SUBJCODE;
    private String CODENAME;
    private String TITLE;
    private String STRTDATE;
    private String END_DATE;
    private String TIME;
    private String PLACE;
    private String ORG_LINK;
    private String MAIN_IMG;
    private String HOMEPAGE;
    private String USE_TRGT;
    private String USE_FEE;
    private String SPONSOR;
    private String INQUIRY;
    private String SUPPORT;
    private String ETC_DESC;
    private String AGELIMIT;
    private String IS_FREE;
    private String TICKET;
    private String PROGRAM;
    private String PLAYER;
    private String CONTENTS;
    private String GCODE;

}
