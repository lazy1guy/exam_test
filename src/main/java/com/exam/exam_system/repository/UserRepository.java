package com.exam.exam_system.repository;

import com.exam.exam_system.entity.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    // 添加分页支持
    Page<User> findByRole(String role, Pageable pageable);

    // 添加缓存提示
    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "usersCache")
    })
    Optional<User> findByUsername(String userName);

    // 添加批量查询
    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(@Param("ids") Set<Long> ids);

    @Query(value = "SELECT /*+ INDEX(u idx_username) */ u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameWithIndexHint(@Param("username") String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT'")
    List<User> findAllStudents();
}