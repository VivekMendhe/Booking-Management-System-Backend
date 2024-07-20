package com.pack.book.dto;

import lombok.Data;

@Data
public class UserDTO {

	private Long id;
	private String name;
	private String date;
	private String startTime;
	private String endTime;
	private String status;
	private String conflictStatus;

}
