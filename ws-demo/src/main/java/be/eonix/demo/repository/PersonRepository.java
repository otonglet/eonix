package be.eonix.demo.repository;

import be.eonix.demo.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    List<Person> findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(String nom, String prenom);

    List<Person> findByNomContainingIgnoreCase(String nom);

    List<Person> findByPrenomContainingIgnoreCase(String prenom);
}