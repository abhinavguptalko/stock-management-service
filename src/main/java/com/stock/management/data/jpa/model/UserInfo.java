package com.stock.management.data.jpa.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_details")
public class UserInfo implements UserDetails {
	@Id
	@Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    private String username;
    private String password;
    public void setPassword(String password) {
		this.password = password;
	}

	private String email;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockDetails> stocks;

    public UserInfo() {}

    public UserInfo(String userId,String username, String email) {
    	this.userId=userId;
        this.username = username;
        this.email = email;
    }
    
    public UserInfo(String userId,String username, String email,String password ) {
    	this.userId=userId;
        this.username = username;
        this.email = email;
        this.password=password;
    }

	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<StockDetails> getStocks() {
		return stocks;
	}

	public void setStocks(List<StockDetails> stocks) {
		this.stocks = stocks;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPassword() {
		return password;
	}


   
}

