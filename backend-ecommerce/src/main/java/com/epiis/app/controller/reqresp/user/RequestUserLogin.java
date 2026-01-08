package com.epiis.app.controller.reqresp.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserLogin {
	@Getter
	@Setter
	public class Dto {
		private String email;
		private String password;
	}

	private Dto dto = new Dto();
}
