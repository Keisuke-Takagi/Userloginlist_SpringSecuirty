package com.example.demo.Repository.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.app.validate.Userform;

//Mysqlとリポジトリで接続
@Repository
public interface UserInfoRepository extends JpaRepository<Userform, Long> {
	    public Userform findByUsername(String username);
}
