package com.example.payment_system.mapper;

import com.example.payment_system.entity.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

@Mapper
public interface UserMapper {

    //@Select与#{}进行预编译防SQL注入
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO user(username, password, phone) VALUES(#{username}, #{password}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);//id自增

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);

    //高并发下的安全扣款
    @Update("UPDATE user SET balance = balance - #{amount} WHERE id = #{id} AND balance >= #{amount}")
    int deductBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

    @Update("UPDATE user SET balance = balance + #{amount} WHERE id = #{id}")
    int addBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
