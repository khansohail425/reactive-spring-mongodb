//package com.reactivespring.testcontainers;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.reactivespring.domain.Review;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoWeb
//public class ProductIntegrationLiveTest extends AbstractBaseIntegrationTest {
//
//    @Autowired
//    private MockMvc mvc;
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    public void givenProduct_whenSave_thenGetProduct() throws Exception {
//        MvcResult mvcResult = mvc.perform(post("/products").contentType("application/json")
//                        .content(objectMapper.writeValueAsString(new Review())))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String productId = mvcResult.getResponse()
//                .getContentAsString();
//
//        mvc.perform(get("/products/" + productId))
//                .andExpect(status().isOk());
//    }
//}
