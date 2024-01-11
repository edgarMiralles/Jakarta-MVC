package deim.urv.cat.homework2.service;

import deim.urv.cat.homework2.model.User;
import deim.urv.cat.homework2.controller.UserForm;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.client.Entity;
import utilities.SecurityUtil;

public class UserServiceImpl implements UserService {
    private final WebTarget webTarget;
    private final jakarta.ws.rs.client.Client client;
    private static final String BASE_URI = "http://localhost:8080/projecte/webresources";
    
    public UserServiceImpl() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("customer");
    }
    
    @Override
    public User findUserByEmail(String email){
        Response response = webTarget.path("email/" + email)
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatus() == 200) {
            return response.readEntity(User.class);
        }
        return null;
    }
    
    @Override
    public User validateUser(UserForm user){
        System.out.println("Contraseña al validar"+user.getPassword());
        Response response = webTarget.path("validate").request(MediaType.APPLICATION_JSON).post(
        Entity.entity(
            user, 
            MediaType.APPLICATION_JSON
        ), 
        Response.class);
        if (response.getStatus() == 200) {
            return response.readEntity(User.class);
        }
        return null;
    }

    @Override
    public boolean addUser(UserForm user) {
        user.setPassword(SecurityUtil.hashPassword(user.getPassword()));
        System.out.println("Contraseña al añadir"+user.getPassword());
        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(
        Entity.entity(
            user, 
            MediaType.APPLICATION_JSON
        ), 
        Response.class);
     return response.getStatus() == 201;
    }

}
