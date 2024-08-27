package br.com.fullcycle.hexagonal.infrastructure.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;
import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.CreatePartnerDTO;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class PartnerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PartnerRepository partnerRepository;

    @AfterEach
    void tearDown() {
        partnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreate() throws Exception {
        var partner = new CreatePartnerDTO("12.345.678/0001-00", "john.doe@gmail.com", "John Doe");

        final var result = this.mvc.perform(
                MockMvcRequestBuilders.post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partner)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
                .andReturn().getResponse().getContentAsByteArray();

        var actualResponse = mapper.readValue(result, CreatePartnerUseCase.Output.class);
        Assertions.assertEquals(partner.name(), actualResponse.name());
        Assertions.assertEquals(partner.cnpj(), actualResponse.cnpj());
        Assertions.assertEquals(partner.email(), actualResponse.email());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com CNPJ duplicado")
    public void testCreateWithDuplicatedCPFShouldFail() throws Exception {
        var partner = new CreatePartnerDTO("12.345.678/0001-00", "john.doe@gmail.com", "John Doe");

        // Cria o primeiro parceiro
        this.mvc.perform(
                MockMvcRequestBuilders.post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partner)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
                .andReturn().getResponse().getContentAsByteArray();

        partner = new CreatePartnerDTO("12.345.678/0001-00", "john.doe@hotmail.com", "John Doe");

        // Tenta criar o segundo parceiro com o mesmo CNPJ
        this.mvc.perform(
                MockMvcRequestBuilders.post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partner)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string("Partner already exists"));
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() throws Exception {
        var partner = new CreatePartnerDTO("12.345.678/0001-00", "john.doe@gmail.com", "John Doe");

        // Cria o primeiro parceiro
        this.mvc.perform(
                MockMvcRequestBuilders.post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partner)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
                .andReturn().getResponse().getContentAsByteArray();

        partner = new CreatePartnerDTO("12.345.678/0001-01", "john.doe@gmail.com", "John Doe");

        // Tenta criar o segundo parceiro com o mesmo e-mail
        this.mvc.perform(
                MockMvcRequestBuilders.post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partner)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string("Partner already exists"));
    }

    @Test
    @DisplayName("Deve obter um parceiro por id")
    public void testGet() throws Exception {
        var partner = new CreatePartnerDTO("12.345.678/0001-00", "john.doe@gmail.com", "John Doe");

        final var createResult = this.mvc.perform(
                MockMvcRequestBuilders.post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partner)))
                .andReturn().getResponse().getContentAsByteArray();

        var partnerId = mapper.readValue(createResult, CreatePartnerUseCase.Output.class).id();

        final var result = this.mvc.perform(
                MockMvcRequestBuilders.get("/partners/{id}", partnerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        var actualResponse = mapper.readValue(result, GetPartnerByIdUseCase.Output.class);
        Assertions.assertEquals(partnerId, actualResponse.id());
        Assertions.assertEquals(partner.name(), actualResponse.name());
        Assertions.assertEquals(partner.cnpj(), actualResponse.cnpj());
        Assertions.assertEquals(partner.email(), actualResponse.email());
    }
}
