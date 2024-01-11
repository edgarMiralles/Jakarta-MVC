/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.controller;

import deim.urv.cat.homework2.model.Cart;
import deim.urv.cat.homework2.model.Console;
import deim.urv.cat.homework2.model.Customer;
import deim.urv.cat.homework2.model.Game;
import deim.urv.cat.homework2.model.Rental;
import deim.urv.cat.homework2.model.User;
import deim.urv.cat.homework2.service.ConsoleService;
import deim.urv.cat.homework2.service.RentalService;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jordi
 */

@Controller
@Path("history")
public class RentalController {
    @Inject ConsoleService consoleService;
    @Inject RentalService rentalService;
    @Inject Models models;

    @GET
    public String showHistory(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        ArrayList<Rental> rentals= new ArrayList();
        HttpSession session = request.getSession(false);
        if(session==null){
            response.sendRedirect(request.getContextPath() + "/Error404.jsp");
        }
        
        User user = (User) session.getAttribute("authUser");
        
        if(user== null || user.getClass() == null || user.getId()==null){
            response.sendRedirect(request.getContextPath() + "/Error404.jsp");
        }
        Collection<Console> consoles = consoleService.getAllConsoles();
        rentals = rentalService.findAllRental(String.valueOf(user.getId()),user);

        
        models.put("consoles", consoles);
        models.put("rentals", rentals);
        return "game/history.jsp";
    }
    
    
    @POST
    public void newRental(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        
        if(session==null){
            response.sendRedirect(request.getContextPath() + "/Error404.jsp");
        }
        
        List<Game> games = new ArrayList<>();
        Cart cart = (Cart) session.getAttribute("cart");
        games = cart.getGames();


        User user = (User) session.getAttribute("authUser");
        
        if(user== null || user.getClass() == null || user.getId()==null || cart==null){
            response.sendRedirect(request.getContextPath() + "/Error404.jsp");
            return;
        }
        
        Customer customer = new Customer();
        customer.setId(user.getId());
        customer.setEmail(user.getEmail());
        customer.setName(user.getUsername());

        String total = request.getParameter("total");
 
        List<Long> gamesId = new ArrayList();
        for(Game game: games){
            gamesId.add(game.getId());
        }
                
        System.out.print(user);
        System.out.print(customer);
        System.out.print(gamesId);

        Rental newRental = new Rental();
        newRental.setCustomerId(customer.getId());
        newRental.setPrice(Float.parseFloat(total));
        newRental.setGameId(gamesId);
        Date startDate = new Date();
        newRental.setStartDate(startDate);
       
        
        rentalService.postRental(newRental, user);

        Cart newCart = new Cart();
        session.setAttribute("cart", newCart);

        response.sendRedirect(request.getContextPath() + "/Web/history");
    }   
}
