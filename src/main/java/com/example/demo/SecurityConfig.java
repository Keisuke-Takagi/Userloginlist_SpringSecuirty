package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.auth.UserServiceimpl;



@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

	@Autowired
	private UserDetailsService userDetailsService;

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
//	}

	/*    @Autowired
	UserDetailsService userDetailsService;*/
//    @Autowired
//    UserAuthenticationProvider authenticationProvider;
	/*
	@Autowired
	AuthenticationFailureHandler authenticationFailureHandler;*/

//    独自のtable認証でのクラス
    @Autowired
    UserServiceimpl userServiceimpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/css/**", "/js/**" );
    }


    /**
     * UserDetailsServiceインターフェースを実装した独自の認証レルムを使用する設定
     * @param auth
     * @throws Exception
     */
	/*    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.userDetailsService(userDetailsService)
	            .passwordEncoder(passwordEncoder());
	}*/

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userServiceimpl);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

          http.authorizeRequests()
                .antMatchers("/login*").permitAll()//ログインフォームは許可
                .antMatchers("/login-fail").permitAll()
                .antMatchers("/registration").permitAll()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/login_fail/**").permitAll()
                .antMatchers("/success").permitAll()
                .antMatchers("/login_error").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers("/test/**").fullyAuthenticated()
				.anyRequest().authenticated();
		          // それ以外はログイン制限なしにする場合使う*/

		  http.formLogin()
		        .loginProcessingUrl("/login")//このURLでログイン処理
		        .loginPage("/login")//ログイン画面のURL
		        .failureUrl("/login_error")
				.failureForwardUrl("/login")//認証失敗時にforwardするurl
		        .defaultSuccessUrl("/list", true)//ログイン成功時のPath
		        .usernameParameter("username")//ユーザのパラメータ名
		        .passwordParameter("password");//パスワードのパラメータ名


		  http.logout()
		        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))//ログアウト処理のURL
		        .logoutSuccessUrl("/registration")//ログアウト成功時のURL
		        .invalidateHttpSession(true)//ログアウト時のセッション放棄
		        .permitAll();

		    }

    //  自作のログイン認証DB接続で使用
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    	auth.userDetailsService(userServiceimpl);
    }

}
