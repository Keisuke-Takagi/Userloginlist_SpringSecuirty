package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.room.UserInfoRepository;
import com.example.demo.app.validate.Userform;

//自作リポジトリとの接続で必要
@Service
public class UserServiceimpl  implements UserDetailsService {


	@Autowired
	private UserInfoRepository userInfoRepo;

    //	Securityに元々あるloadUserByUsernameを書き換えて接続
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			if (username == "" ) {
				throw new UsernameNotFoundException("Username is empty");
			}
			Userform userInfo = userInfoRepo.findByUsername(username);

			//Secuirtyが認識できる型で送り返す
			return  userInfo;
	}
}