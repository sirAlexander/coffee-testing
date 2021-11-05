package com.sebastian_daschner.coffee_shop.orders.boundary;

import com.sebastian_daschner.coffee_shop.orders.entity.CoffeeType;
import com.sebastian_daschner.coffee_shop.orders.entity.Order;
import com.sebastian_daschner.coffee_shop.orders.entity.Origin;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Path("order.html")
@Controller
@ApplicationScoped
public class OrderCoffeeController {

    @Inject
    CoffeeShop coffeeShop;

    @Inject
    Models models;

    @GET
    public String index() {
        Set<CoffeeType> types = coffeeShop.getCoffeeTypes();
        models.put("types", types);
        return "order.jsp";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submit(@FormParam("type") @DefaultValue("") String type, @FormParam("origin") @DefaultValue("") String originName) {
        CoffeeType coffeeType = CoffeeType.fromString(type);
        Origin origin = new Origin(originName);
        Order order = new Order(UUID.randomUUID(), coffeeType, origin);

        if (!orderIsValid(order)) {
            Set<CoffeeType> types = coffeeShop.getCoffeeTypes();

            models.put("failed", true);
            models.put("types", types);
            return Response.ok("order.jsp").build();
        }

        coffeeShop.createOrder(order);

        return Response.seeOther(URI.create("/coffee-shop/coffee/index.html")).build();
    }

    private boolean orderIsValid(Order order) {
        if (order.getType() == null || order.getOrigin() == null)
            return false;
        Origin origin = coffeeShop.getOrigin(order.getOrigin().getName());
        return origin != null;
    }

}
