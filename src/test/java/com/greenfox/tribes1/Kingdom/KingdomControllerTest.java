package com.greenfox.tribes1.Kingdom;

import com.greenfox.tribes1.ApplicationUser.ApplicationUser;
import com.greenfox.tribes1.Building.Building;
import com.greenfox.tribes1.Building.BuildingFactory;
import com.greenfox.tribes1.Building.BuildingType;
import com.greenfox.tribes1.Kingdom.DTO.KingdomDTO;
import com.greenfox.tribes1.Security.Model.JwtTokenFactory;
import com.greenfox.tribes1.TestTokenProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class KingdomControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtTokenFactory tokenFactory;

  @MockBean
  private KingdomService kingdomService;

  private String token;
  private TestTokenProvider testTokenProvider;
  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
  private String username = "username";
  private ApplicationUser testApplicationUser;
  private String userEmail = "user@user.com";
  private String kingdomName = "kingdomName";
  private Long id = 1L;
  private Kingdom testKingdom;
  private KingdomDTO testKingdomDTO;
  private String kingdomJson;
  private String mineJson;
  private String failedAuth;

  @Before
  public void init() throws JSONException {
    testTokenProvider = new TestTokenProvider(tokenFactory);

    testApplicationUser = ApplicationUser.builder()
            .id(id)
            .username(username)
            .userEmail(userEmail)
            .kingdom(testKingdom)
            .build();

    testKingdom = Kingdom.builder()
            .id(id)
            .name(kingdomName)
            .applicationUser(testApplicationUser)
            .buildings(new ArrayList<>())
            .resources(new ArrayList<>())
            .troops(new ArrayList<>())
            .build();

    testKingdomDTO = new ModelMapper().map(testKingdom, KingdomDTO.class);

    kingdomJson = new JSONObject()
            .put("id", 1)
            .put("kingdomName", kingdomName)
            .put("applicationUserName", username)
            .toString();

    mineJson = new JSONArray()
            .put(0, (new JSONObject()
                    .put("id", null)
                    .put("level", null)
                    .put("started_at", null)
                    .put("finished", null)
                    .put("kingdom", null)
                    .put("hp", null))).toString();

    failedAuth = new JSONObject()
            .put("status", "error")
            .put("message", "Auth failure")
            .toString();
  }

  @Test
  public void getKingdom_userNotFound_throwsException() throws Exception {
    token = testTokenProvider.createMockToken(username);
    when(kingdomService.getKindomFromAuth(Mockito.any(Authentication.class))).thenThrow(UsernameNotFoundException.class);
    mockMvc.perform(
            MockMvcRequestBuilders.get("/kingdom")
                    .header("Authorization", token))
            .andExpect(status().isUnauthorized());
  }

  @Test
  public void getKingdom_userFound_returnsOK() throws Exception {
    token = testTokenProvider.createMockToken(username);
   // when(kingdomService.getKindomFromAuth(Mockito.any(Authentication.class))).thenReturn(testKingdom);

    mockMvc.perform(
            MockMvcRequestBuilders.get("/kingdom")
                    .header("Authorization", token))
            .andExpect(status().isOk());
  }


  //refactor method names -> CLEAN CODE
  @Test
  public void getKingdom_returnsError_ifTokenNotProvided() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/kingdom")
    )
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(failedAuth));
  }

  @Test
  public void getKingdom_returnsError_ifTokenNotValid() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/kingdom")
                    .header("Authorization", "Bearer not.Valid.Token"))
            .andExpect(status().isUnauthorized());
  }

  @Test
  public void getKingdom_ReturnsKingdomDTO_StatusOK_HasCorrectMediaType() throws Exception {
    token = testTokenProvider.createMockToken(username);
    when(kingdomService.createKingdomDTOFromKingdom(testKingdom)).thenReturn(testKingdomDTO);
    when(kingdomService.getKindomFromAuth(Mockito.any(Authentication.class))).thenReturn(testKingdom);

    mockMvc.perform(
            MockMvcRequestBuilders.get("/kingdom")
                    .header("Authorization", token)
    )
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType))
            .andExpect(content().json(kingdomJson));
    verify(kingdomService, times(2)).getKindomFromAuth(Mockito.any(Authentication.class));
    verify(kingdomService, times(1)).createKingdomDTOFromKingdom(testKingdom);
    //verifyNoMoreInteractions(kingdomService);
  }

  @Test
  public void getKingdomBuilding_StatusOk_ReturnsMine() throws Exception {
    token = testTokenProvider.createMockToken(username);
    Building mine = BuildingFactory.createBuilding(BuildingType.Mine);
    List<Building> buildingList = new ArrayList<>();
    buildingList.add(mine);
    testKingdom.setBuildings(buildingList);
    when(kingdomService.findKingdomByApplicationUserName(Mockito.any(String.class))).thenReturn(testKingdom);
    when(kingdomService.getKindomFromAuth(Mockito.any(Authentication.class))).thenReturn(testKingdom);
    mockMvc.perform(
            MockMvcRequestBuilders.get("/kingdom/buildings")
                    .header("Authorization", token)
    )
            .andExpect(content().contentType(contentType))
            .andExpect(content().json(mineJson))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void getKingdomBuildings_returnsEmptyList() throws Exception {
    String empty = "[]";

    token = testTokenProvider.createMockToken(username);
    when(kingdomService.findKingdomByApplicationUserName(Mockito.any(String.class))).thenReturn(testKingdom);
    when(kingdomService.getKindomFromAuth(Mockito.any(Authentication.class))).thenReturn(testKingdom);

    mockMvc.perform(
            MockMvcRequestBuilders.get("/kingdom/buildings")
                    .header("Authorization", token)
    )
            .andDo(print())
            .andExpect(content().contentType(contentType))
            .andExpect(content().json(empty))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }
}

