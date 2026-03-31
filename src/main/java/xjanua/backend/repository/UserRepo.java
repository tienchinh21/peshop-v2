package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

}
