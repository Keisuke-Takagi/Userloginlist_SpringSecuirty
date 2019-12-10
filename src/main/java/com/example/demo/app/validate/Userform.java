package com.example.demo.app.validate;


import java.util.Collection;

/*JPAentity関連*/
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
//バリデーションに使用
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//Beanバリデーションをするフォームクラス
@Entity
@Table(name = "users")
public class Userform implements UserDetails {

	/*SpringSecurityの認可で必要 ※「ROLE_」がないと動かない*/
	public enum Authority {ROLE_USER, ROLE_ADMIN};

     //	フィールド
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name ="id")
	@Id
	private int id;

	@Column(name="username")
	@Email(message="メールアドレスを入力してください")
	@NotBlank(message="メールアドレスが空です")
	private String username;

	@NotBlank(message="パスワードを入力してください")
	@Pattern(regexp = "^([a-zA-Z]+(?=[0-9])|[0-9]+(?=[a-zA-Z]))[0-9a-zA-Z]+$", message="パスワードは半角英数字をそれぞれ含む5文字以上10文字以内にしてください")
	@Column(name="password")
	private String password;

	@Column(name="enabled")
	private  Integer  enabled;

    //	springsecurity自作時に使用
	@Enumerated(EnumType.STRING)
	private Authority authority;

      //  Spring securityで元から定義されているメソッド
	  @Override
	  public Collection<? extends GrantedAuthority> getAuthorities() {
		  return null;
	  }

	// Spring securityで元から定義されているメソッド
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// Spring securityで元から定義されているメソッド
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// Spring securityで元から定義されているメソッド
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// Spring securityで元から定義されているメソッド
	@Override
	public boolean isEnabled() {
		return true;
	}


    //	ゲッターセッター
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
}