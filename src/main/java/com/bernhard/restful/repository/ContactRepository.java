package com.bernhard.restful.repository;

import com.bernhard.restful.entity.Contact;
import com.bernhard.restful.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String>, JpaSpecificationExecutor {

    Optional<Contact> findFirstByUserAndId(User user, String id);
}
