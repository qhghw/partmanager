package com.partmanager.pojo;

import com.partmanager.pojo.Role;

/**
 * Users entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class Users implements java.io.Serializable {

	// Fields

	private Integer userid;
	private Role role;
	private String logincode;
	private String password;
	private String username;
	private Integer state;
	private String dept_cod;
	private String bz;

	// Constructors

	/** default constructor */
	public Users() {
	}

	/** minimal constructor */
	public Users(Role role, String logincode, String password, String username,
			Integer state,String dept_cod) {
		this.role = role;
		this.logincode = logincode;
		this.password = password;
		this.username = username;
		this.state = state;
		this.dept_cod = dept_cod;
	}

	public String getDept_cod() {
		return dept_cod;
	}

	public void setDept_cod(String dept_cod) {
		this.dept_cod = dept_cod;
	}

	/** full constructor */
	public Users(Role role, String logincode, String password, String username,
			Integer state, String bz,String dept_cod) {
		this.role = role;
		this.logincode = logincode;
		this.password = password;
		this.username = username;
		this.state = state;
		this.bz = bz;
		this.dept_cod = dept_cod;
	}

	// Property accessors

	public Integer getUserid() {
		return this.userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getLogincode() {
		return this.logincode;
	}

	public void setLogincode(String logincode) {
		this.logincode = logincode;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getBz() {
		return this.bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

}