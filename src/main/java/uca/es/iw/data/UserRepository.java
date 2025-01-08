package uca.es.iw.data;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);


    @Query(value = """
    SELECT au.name
    FROM application_user au
    JOIN user_roles ur_promotor ON au.id = ur_promotor.user_id
    LEFT JOIN user_roles ur_admin ON au.id = ur_admin.user_id AND ur_admin.roles = 'ADMIN'
    WHERE ur_promotor.roles = 'PROMOTOR' AND ur_admin.user_id IS NULL
    """, nativeQuery = true)
    List<String> findPromotor();

}
