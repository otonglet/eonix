package be.eonix.demo;

import be.eonix.demo.controller.PersonController;
import be.eonix.demo.domain.Person;
import be.eonix.demo.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PersonControllerTest {
    private static final String GROSJEAN = "Grosjean";
    private static final String UUID_MATCHING_NO_ONE = "550e8400-e29b-41d4-a716-446655440000";
    @Autowired
    private PersonRepository repository;
    @Autowired
    private PersonController controller;
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();

        repository.deleteAll();
        repository.saveAll(Arrays.asList(
                new Person("Marcel", "Labille"),
                new Person("Sebastien", GROSJEAN),
                new Person("Aurelien", "Merckx")
        ));
    }

    /**
     * Récupération d'une liste de personnes, avec filtres sur le nom et le prénom. Les filtres
     * doivent être optionnels. Les filtres doivent être insensibles à la casse, et il doit être possible
     * de faire une recherche sur le début ou la fin du nom/prénom (par exemple pour
     * "Sébastien", "Séb" ou "tien" doit matcher).
     */
    @Test
    public void getPersons() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].prenom").value(hasItems("Marcel", "Sebastien", "Aurelien")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/person?nom=jean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].prenom").value(hasItem("Sebastien")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/person?prenom=tien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].prenom").value(hasItem("Sebastien")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/person?prenom=ien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].prenom").value(hasItems("Sebastien", "Aurelien")));
    }


    /**
     * Récupération d'une personne, sur base de son ID.
     */
    @Test
    public void getPersonById() throws Exception {
        Person grosjean = repository.findByNomContainingIgnoreCase(GROSJEAN).get(0);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/person/" + grosjean.getId())
                        .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value(grosjean.getPrenom()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/person/" + UUID_MATCHING_NO_ONE))
                .andExpect(status().isNotFound());
    }

    /**
     * Ajout d'une personne
     */
    @Test
    public void addPerson() throws Exception {
        Person person = new Person("Andre", "Labruyere");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/person")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(person)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prenom").value("Andre"));

    }

    /**
     * Mise à jour d'une personne
     */
    @Test
    public void updatePerson() throws Exception {
        Person grosjean = repository.findByNomContainingIgnoreCase(GROSJEAN).get(0);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/person/" + UUID_MATCHING_NO_ONE)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(grosjean)))
                .andExpect(status().isNotFound());

        grosjean.setPrenom("Philippe");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/person/" + grosjean.getId())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(grosjean)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value(grosjean.getPrenom()));

        assertThat(repository.findByNomContainingIgnoreCase(GROSJEAN).get(0).getPrenom()).isEqualTo("Philippe");
    }

    /**
     * Suppression d'une personne
     */
    @Test
    public void deletePerson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/person/" + UUID_MATCHING_NO_ONE))
                .andExpect(status().isOk());

        Person grosjean = repository.findByNomContainingIgnoreCase(GROSJEAN).get(0);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/person/" + grosjean.getId()))
                .andExpect(status().isOk());

        assertThat(repository.findByNomContainingIgnoreCase(GROSJEAN)).isEmpty();
    }
}
