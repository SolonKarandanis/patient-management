package com.pm.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
public class AuthControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

}

//@Test
//void shouldReturnAdminView() throws Exception {
//    this.mockMvc
//            .perform(get("/admin"))
//            .andExpect(status().is(200))
//            .andExpect(view().name("admin"))
//            .andExpect(model().attributeExists("secretMessage"));
//}


//Map<String, String> map = new HashMap<>();
//        map.put("key", key);
//        map.put("secret", secret);
//HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAll(map);
//MvcResult result = mvc.perform(MockMvcRequestBuilders
//                .post("/scheduler/enable")
//                .headers(httpHeaders)
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(this.getRequstAsJSON()))
//        .andDo(print())
//        .andReturn();