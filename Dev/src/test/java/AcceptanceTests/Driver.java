package AcceptanceTests;

import Exceptions.*;
import OutResources.*;
import Security.UserValidation;
import Service.MarketService;
import Store.Store;
import System.System;
import User.User;
import User.Subscriber;

import java.util.HashMap;
import java.util.Map;

public class Driver {

    private static String adminId;

    public static MarketService getRealService(String userName, String password) throws UserExistsException, PaymentException, DeliveryException, AdminNotFoundException, LostConnectionException {
        DeliveryAdapter deliveryAdapter = new DeliveryAdapterImpl();
        PaymentAdapter paymentAdapter = new PaymentAdapterImpl();
        Map<String, String> usersForValidator = new HashMap<>();
        Map<String, Subscriber> subscribers = new HashMap<>();
        UserValidation userValidation = new UserValidation(usersForValidator);
        Map<String, User> users = new HashMap<>();
        Map<Integer, Store> stores = new HashMap<>();
        Map<String, User> systemAdmins= new HashMap<String, User>();
        systemAdmins.put(userName, new Subscriber());
        System system = new System(deliveryAdapter, paymentAdapter, userValidation, users, subscribers, stores, systemAdmins);
        adminId = system.entrance();
        system.registerAdmin(userName, password);
        MarketService marketService = new MarketService(system);
        return  marketService;
    }

    public static MarketService getMockService(String userName, String password) throws UserExistsException, ConnectionNotFoundException, PaymentException, DeliveryException, AdminNotFoundException, LostConnectionException {
        DeliveryAdapter deliveryAdapter = new DeliveryAdapterTimerMock();
        PaymentAdapter paymentAdapter = new PaymentAdapterTimerMock();
        Map<String, String> usersForValidator = new HashMap<>();
        Map<String, Subscriber> subscribers = new HashMap<>();
        UserValidation userValidation = new UserValidation(usersForValidator);
        Map<String, User> users = new HashMap<>();
        Map<Integer, Store> stores = new HashMap<>();
        Map<String, User> systemAdmins= new HashMap<String, User>();
        systemAdmins.put(userName, new Subscriber());
        System system = new System(deliveryAdapter, paymentAdapter, userValidation, users, subscribers, stores, systemAdmins);
        adminId = system.entrance();
        system.registerAdmin(userName, password);
        MarketService marketService = new MarketService(system);
        return  marketService;
    }

    public static String getAdminId()
    {
        return adminId;
    }
}
