package com.project.inno_online_store.jpa.repository;

import com.project.inno_online_store.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {


    @Query(value = """
        select * 
        from users
        where active = true 
""", nativeQuery = true)
    Set<User> findActiveUsers();

}
