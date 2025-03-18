package vn.minhhai.springb_fskill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.minhhai.springb_fskill.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "select r from Role r inner join UserHasRole ur on r.id = ur.user.id where ur.user.id=:userId")
    List<Role> getAllByUserId(Long userId);

}
