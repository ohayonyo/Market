package GUI;

import DAL.*;
import Exceptions.*;

import Notifications.Notification;
import OutResources.DeliveryAdapterImpl;
import OutResources.PaymentAdapterImpl;

import Security.UserValidation;
import Service.*;
import Store.Store;
import System.System;
import User.User;
import User.Subscriber;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.io.IOException;
import java.util.*;

public class Market {

    private Service marketService;
    private String version = UUID.randomUUID().toString();

    private Map<String, Object> returnModel(Context context) {
        return WebView.initModel(context);
    }

    public Service getMarketService() {
        return marketService;
    }

    public Handler getHomePage = context -> {
        Map<String, Object> model = returnModel(context);
        model.put("first", true);
        context.render(ModelPaths.HomePage, model);
    };

    public Handler postHomePage = context -> {
        Map<String, Object> model = returnModel(context);
        String connectionID = Request.getStringFormParam(context, "connectionID");
        String input_version = Request.getStringFormParam(context, "version");
        if(!input_version.equals(version)){
            input_version = version;
            connectionID = marketService.entrance();
            model.put("save", true);
        }
        model.put("version", input_version);
        model.put("connectID", connectionID);
        context.render(ModelPaths.HomePage, model);
    };

    public Handler getRegister = context -> {
        context.render(ModelPaths.Register, returnModel(context));
    };

    public Handler postRegister = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            //String id = Request.getStringFromParam(context, "connectionID");
            String name = Request.getStringFormParam(context, "user_name");
            String password = Request.getStringFormParam(context, "password");
            marketService.register(name, password);
            model.put("Successful_Register", true);
            if (Request.getStringQueryParam(context, "redirect_login") != null) {
                context.redirect(Request.getStringQueryParam(context, "redirect_login"));
            }
            context.render(ModelPaths.Register, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.Register, model);
        } catch (UserExistsException e) {
            model.put("User_Exists_Exception", e);
            context.render(ModelPaths.Register, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.Register, model);
        }
    };

    public Handler getLogin = context -> {
        Map<String, Object> model = returnModel(context);
        boolean removed = Request.removeSessionAttribute(context, "logout");
        model.put("logout", removed);
        context.render(ModelPaths.Login, model);
    };

    public Handler postLogin = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            String name = Request.getStringFormParam(context, "user_name");
            String password = Request.getStringFormParam(context, "password");
            marketService.login(connectionID, name, password);
            //set current_user
            Collection<String> noti = new LinkedList<>();
            Collection<Notification> notifications = ((Subscriber)marketService.getSystemUser(Request.getStringFormParam(context, "connectionID"))).checkPendingNotifications();
            for (Notification n : notifications)
                noti.add(n.toString());
            model.put("notifications", noti);
            context.sessionAttribute("notifications", noti);
            context.sessionAttribute("current_user", name);
            model.put("Successful_Login", true);
            model.put("current_user", name);
            if (Request.getStringQueryParam(context, "redirect_login") != null) {
                context.redirect(Request.getStringQueryParam(context, "redirect_login"));
            }
            context.render(ModelPaths.HomePage, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found", e);
            context.render(ModelPaths.Login, model);
        } catch (UserNotExistException e) {
            model.put("User_Not_Exist", e);
            context.render(ModelPaths.Login, model);
        } catch (WrongPasswordException e) {
            model.put("Wrong_Password", e);
            context.render(ModelPaths.Login, model);
        }
        catch (AlreadyLoggedInException e) {
            model.put("Already_Logged_In_Exception", e);
            context.render(ModelPaths.Login, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.Login, model);
        }
    };

    public Handler getUpdateCart = context -> {
        context.render(ModelPaths.UpdateCart, returnModel(context));
    };

    public Handler postUpdateCart = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            int storeID = Request.getIntegerFormParam(context, "store_id");
            int productID = Request.getIntegerFormParam(context, "product_id");
            int finalAmount = Request.getIntegerFormParam(context, "final_amount");
            marketService.updateShoppingBag(connectionID, storeID, productID, finalAmount);
            model.put("Successful_Update", true);
            context.render(ModelPaths.UpdateCart, model);

        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.UpdateCart, model);
        } catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.UpdateCart, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.UpdateCart, model);
        }
        catch (OutOfInventoryException e) {
            model.put("Out_Of_Inventory_Exception", e);
            context.render(ModelPaths.UpdateCart, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.UpdateCart, model);
        }
    };

    public Handler getShowCart = context -> {
        context.render(ModelPaths.ShowCart, returnModel(context));
    };

    public Handler postShowCart = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionID = Request.getStringFormParam(context, "connectionID");
            Collection<String> cart = marketService.getCart(connectionID);
            model.put("cart_col", cart);
            double price=marketService.getCartPrice(connectionID);
            model.put("cart_price", price);
            context.render(ModelPaths.ShowCart, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.ShowCart, model);
        }
    };

    public Handler getOpenStore = context -> {
        context.render(ModelPaths.OpenStore, returnModel(context));
    };

    public Handler postOpenStore = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String id = Request.getStringFormParam(context, "connectionID");
            String name = Request.getStringFormParam(context, "store_name");
            int storeID = marketService.openStore(id, name);
            model.put("storeID", storeID);
            context.render(ModelPaths.OpenStore, model);
        } catch (ConnectionNotFoundException e) {
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.OpenStore, model);
        } catch (IllegalNameException e) {
            model.put("Illegal_Name_Exception", e);
            context.render(ModelPaths.OpenStore, model);
        } catch (IllegalDiscountException e) {
            model.put("Illegal_Discount_Exception", e);
            context.render(ModelPaths.OpenStore, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.OpenStore, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.OpenStore, model);
        }
    };

    public Handler getAddProduct = context -> {
        context.render(ModelPaths.AddProduct, returnModel(context));
    };

    public Handler postAddProduct = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            int storeID = Request.getIntegerFormParam(context, "store_id");
            String productName = Request.getStringFormParam(context, "product_name");
            String category = Request.getStringFormParam(context, "category");
            String description = Request.getStringFormParam(context, "description");
            int finalAmount = Request.getIntegerFormParam(context, "final_amount");
            double price = Request.getDoubleFormParam(context, "price");
            int productID = marketService.addProductToStore(connectionID, storeID, productName, category, description, finalAmount, price);
            model.put("productID", productID);
            context.render(ModelPaths.AddProduct, model);
        } catch (ConnectionNotFoundException e) {
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.AddProduct, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddProduct, model);
        } catch (NotManagerException e) {
            model.put("Not_Manager_Exception", e);
            context.render(ModelPaths.AddProduct, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddProduct, model);
        } catch (StoreNotActiveException e) {
            model.put("Store_Not_Active_Exception", e);
            context.render(ModelPaths.AddProduct, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddProduct, model);
        } catch (NotInventoryManagementException e) {
            model.put("Not_Inventory_Management_Exception", e);
            context.render(ModelPaths.AddProduct, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddProduct, model);
        }
    };

    public Handler getAdmin = context -> {
        context.render(ModelPaths.Admin, returnModel(context));
    };

    public Handler getErrorLog = context -> {
        context.render(ModelPaths.ErrorLog, returnModel(context));
    };

    public Handler postErrorLog = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            Collection<String> error_log = marketService.errorLog(connectionID);
            model.put("error_log", error_log);
            context.render(ModelPaths.ErrorLog, model);
        } catch (ConnectionNotFoundException e) {
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.ErrorLog, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.ErrorLog, model);
        }
    };

    public Handler getEventLog = context -> {
       context.render(ModelPaths.EventLog, returnModel(context));
   };

    public Handler postEventLog = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            Collection<String> event_log = marketService.eventLog(connectionID);
            model.put("event_log", event_log);
            context.render(ModelPaths.EventLog, model);
        } catch (ConnectionNotFoundException e) {
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.EventLog, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.EventLog, model);
        }
    };

    public Handler getSearch = context -> {
        context.render(ModelPaths.Search, returnModel(context));
    };

    public Handler postLogout = context -> {
        try {
            String connectionID = Request.getStringFormParam(context, "logoutID");
//            Subscriber subscriber = context.attribute("subscriber");
//            if(subscriber != null)
//                subscriber.setObserver(null);
            marketService.logout(connectionID);
            context.sessionAttribute("current_user", null);
            context.sessionAttribute("notifications", null);
            context.sessionAttribute("logout", "true");
            context.redirect(WebSitesPaths.Login);
        } catch (ConnectionNotFoundException e) {
            Map<String, Object> model = returnModel(context);
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.ErrorPageNotFound, model);
        }
    };

    public Handler getDeleteProduct = context -> {
        context.render(ModelPaths.DeleteProduct, returnModel(context));
    };

    public Handler postDeleteProduct = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            int storeID = Request.getIntegerFormParam(context, "store_id");
            int productId = Request.getIntegerFormParam(context, "product_id");
            marketService.deleteProductFromStore(connectionID, storeID, productId);
            model.put("Successful_Show", true);
            model.put("productID", productId);
            context.render(ModelPaths.DeleteProduct, model);
        } catch (ConnectionNotFoundException e) {
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteProduct, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteProduct, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.DeleteProduct, model);
        }
        catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteProduct, model);
        }
        catch (NotInventoryManagementException e) {
            model.put("Not_Inventory_Management_Exception", e);
            context.render(ModelPaths.DeleteProduct, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.DeleteProduct, model);
        }
    };

    public Handler getUpdateProduct = context -> {
        context.render(ModelPaths.UpdateProduct, returnModel(context));
    };

    public Handler postUpdateProduct = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            int storeID = Request.getIntegerFormParam(context, "store_id");
            int productId = Request.getIntegerFormParam(context, "product_id");
            String category = Request.getStringFormParam(context, "category");
            String description = Request.getStringFormParam(context, "description");
            int finalAmount = Request.getIntegerFormParam(context, "final_amount");
            double price = Request.getDoubleFormParam(context, "price");
            marketService.updateProductDetails(connectionID, storeID, productId, category, description, finalAmount, price);
            model.put("Successful_Show", true);
            model.put("productID", productId);
            context.render(ModelPaths.UpdateProduct, model);
        } catch (ConnectionNotFoundException e) {
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (IllegalNameException e) {
            model.put("Illegal_Name_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (IllegalPriceException e) {
            model.put("Illegal_Price_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (InsufficientInventory e) {
            model.put("Insufficient_Inventory", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (NotInventoryManagementException e) {
            model.put("Not_Inventory_Management_Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.UpdateProduct, model);
        }
    };

    public Handler getMarketInfo = context -> {
        context.render(ModelPaths.MarketInfo, returnModel(context));
    };

    public Handler postMarketInfo = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            Collection<String> marketInfo = marketService.MarketInfo(connectionID);
            model.put("Market_Info", marketInfo);
            context.render(ModelPaths.MarketInfo, model);
        } catch (StoreNotActiveException e) {
            model.put("Store_Not_Active_Exception", e);
            context.render(ModelPaths.MarketInfo, model);
        }  catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.MarketInfo, model);
        }
    };



    public Handler getSearchByName = context -> {
        context.render(ModelPaths.SearchByName, returnModel(context));
    };

    public Handler postSearchByName = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String id = Request.getStringFormParam(context, "connectionID");
            String name = Request.getStringFormParam(context, "name");
            Collection products = marketService.searchProductByName(name);
            if (products.isEmpty())
                model.put("products", "No Results Were Found");
            else model.put("products", products);
            context.render(ModelPaths.SearchByName, model);
        } catch (StoreNotActiveException e) {
            model.put("Store_Not_Active_Exception", e);
            context.render(ModelPaths.SearchByName, model);
        }catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.SearchByName, model);
        }
    };

    public Handler getSearchByCategory = context -> {
        context.render(ModelPaths.SearchByCategory, returnModel(context));
    };

    public Handler postSearchByCategory = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String id = Request.getStringFormParam(context, "connectionID");
            String name = Request.getStringFormParam(context, "category");
            Collection products = marketService.searchProductByCategory(name);
            if (products.isEmpty())
                model.put("products", "No Results Were Found");
            else model.put("products", products);
            context.render(ModelPaths.SearchByCategory, model);
        } catch (StoreNotActiveException e) {
            model.put("Store_Not_Active_Exception", e);
            context.render(ModelPaths.SearchByCategory, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.SearchByCategory, model);
        }
    };

    public Handler getSearchByKeyword = context -> {
        context.render(ModelPaths.SearchByKeyword, returnModel(context));
    };

    public Handler postSearchByKeyword = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String id = Request.getStringFormParam(context, "connectionID");
            String name = Request.getStringFormParam(context, "product_keyword");
            Collection products = marketService.searchProductByKeyWord(name);
            if (products.isEmpty())
                model.put("products", "No Results Were Found");
            else model.put("products", products);
            context.render(ModelPaths.SearchByKeyword, model);
        } catch (StoreNotActiveException e) {
            model.put("Store_Not_Active_Exception", e);
            context.render(ModelPaths.SearchByKeyword, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.SearchByKeyword, model);
        }
    };

    public Handler getFilterByPrice = context -> {
        context.render(ModelPaths.FilterByPrice, returnModel(context));
    };

    public Handler postFilterByPrice = context -> {
        context.render(ModelPaths.FilterByPrice, returnModel(context));
    };

/*    public Handler getFilterByCategory = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String id = Request.getStringFromParam(context, "connectionID");
            String name = Request.getStringFromParam(context, "category");
*//*            getSearchByCategory(id,)
            Collection products = marketService.filterByCategory(name);*//*
            if (products.isEmpty())
                model.put("products", "No Results Were Found");
            else model.put("products", products);
            context.render(ModelPaths.SearchByCategory, model);
        } catch (StoreNotActiveException e) {
            model.put("Store_Not_Active_Exception", e);
            context.render(ModelPaths.SearchByCategory, model);
        }
    };*/

    public Handler postFilterByCategory = context -> {
        context.render(ModelPaths.FilterByPrice, returnModel(context));
    };

    public Handler getAppointStoreRoles = context -> {
        context.render(ModelPaths.AppointStoreRole, returnModel(context));
    };

    public Handler postAppointStoreRoles = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String username = Request.getStringFormParam(context, "user_name");
            String role = Request.getStringFormParam(context, "role_droplist");
            if (role.equals("manager")) {
                marketService.appointStoreManager(connectionId, store_id, username);
                model.put("appointment", true);
            }

            if (role.equals("owner")) {
                marketService.appointStoreOwner(connectionId, store_id, username);
                model.put("appointment", true);
            }
            model.put("role", role);
            context.render(ModelPaths.AppointStoreRole, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
        catch (AlreadyOwnerException e) {
            model.put("Already_Owner_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        } catch (AlreadyManagerException e) {
            model.put("Already_Manager_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        } catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
        catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
        catch (IllegalCircularityException e) {
            model.put("Illegal_Circularity_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        } catch (UserCantAppointHimself e) {
            model.put("User_Cant_Appoint_Himself_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AppointStoreRole, model);
        }
    };

    public Handler getInfoAboutSubscribers = context -> {
        context.render(ModelPaths.InfoAboutSubscribers, returnModel(context));
    };

    public Handler postInfoAboutSubscribers = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            Collection<String> subscribers=marketService.infoAboutSubscribers(connectionId);
            model.put("subscribers", subscribers);
            context.render(ModelPaths.InfoAboutSubscribers, model);
        }

        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.InfoAboutSubscribers, model);
        }

        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.InfoAboutSubscribers, model);
        }

        catch (IOException e) {
            model.put("IOException", e);
            context.render(ModelPaths.InfoAboutSubscribers, model);
        }

        catch (NotAdminException e) {
            model.put("Not_Admin_Exception", e);
            context.render(ModelPaths.InfoAboutSubscribers, model);
        }

    };

    public Handler getPurhcaseHistory = context -> {
        context.render(ModelPaths.PurchaseHistory, returnModel(context));
    };

    public Handler postPurhcaseHistory = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<String> purchaseHistory=marketService.purchaseHistory(connectionId, store_id);
            model.put("purchase_history", purchaseHistory);
            model.put("store_id", store_id);
            context.render(ModelPaths.PurchaseHistoryAdmin, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.PurchaseHistory, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.PurchaseHistory, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.PurchaseHistory, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.PurchaseHistory, model);
        }
        catch (NotManagerException e) {
            model.put("Not_Manager_Exception", e);
            context.render(ModelPaths.PurchaseHistory, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PurchaseHistory, model);
        }
    };

    public Handler getPurhcaseHistoryAdmin = context -> {
        context.render(ModelPaths.PurchaseHistoryAdmin, returnModel(context));
    };


/*    public Handler postShowPurhcaseHistoryAdmin = context -> {
        context.render(ModelPaths.PurchaseHistoryAdmin, returnModel(context));
    };*/

    public Handler getAddInventoryManagementPermission = context -> {
        context.render(ModelPaths.AddInventoryManagementPermission, returnModel(context));
    };

    public Handler postAddInventoryManagementPermission = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            String userName = Request.getStringFormParam(context, "user_name");
            marketService.addInventoryManagementPermission(connectionId,storeId, userName);
            model.put("success_added", true);
            model.put("user_name", userName);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }
        catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }
        catch (AlreadyOwnerException e) {
            model.put("Already_Owner_Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }

        catch (AlreadyManagerException e) {
            model.put("Already_Manager_Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }

        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }
      catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddInventoryManagementPermission, model);
        }
    };

    public Handler getDeleteInventoryManagementPermission = context -> {
        context.render(ModelPaths.DeleteInventoryManagementPermission, returnModel(context));
    };

    public Handler postDeleteInventoryManagementPermission = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            String userName = Request.getStringFormParam(context, "user_name");
            marketService.deleteInventoryManagementPermission(connectionId,storeId, userName);
            model.put("successfully_deleted", true);
            model.put("user_name", userName);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }
        catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }
        catch (AlreadyOwnerException e) {
            model.put("Already_Owner_Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }

        catch (AlreadyManagerException e) {
            model.put("Already_Manager_Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }

        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }


      catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.DeleteInventoryManagementPermission, model);
        }
    };

    public Handler getAddStorePolicyManagementPermission = context -> {
        context.render(ModelPaths.AddStorePolicyManagementPermission, returnModel(context));
    };

    public Handler postAddStorePolicyManagementPermission = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            String userName = Request.getStringFormParam(context, "user_name");
            marketService.addStorePolicyManagementPermission(connectionId,storeId, userName);
            model.put("success_added", true);
            model.put("user_name", userName);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }
        catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }
        catch (AlreadyOwnerException e) {
            model.put("Already_Owner_Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }

        catch (AlreadyManagerException e) {
            model.put("Already_Manager_Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }

        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }
      catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddStorePolicyManagementPermission, model);
        }
    };

    public Handler getDeleteStorePolicyManagementPermission = context -> {
        context.render(ModelPaths.DeleteStorePolicyManagementPermission, returnModel(context));
    };

    public Handler postDeleteStorePolicyManagementPermission = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            String userName = Request.getStringFormParam(context, "user_name");
            marketService.deleteStorePolicyManagementPermission(connectionId,storeId, userName);
            model.put("successfully_deleted", true);
            model.put("user_name", userName);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }
        catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }
        catch (AlreadyOwnerException e) {
            model.put("Already_Owner_Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }

        catch (AlreadyManagerException e) {
            model.put("Already_Manager_Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }

        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }
      catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.DeleteStorePolicyManagementPermission, model);
        }
    };

    public Handler getResetStoreDiscounts = context -> {
        context.render(ModelPaths.ResetStoreDiscounts, returnModel(context));
    };

    public Handler postResetStoreDiscounts = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            marketService.resetStoreDiscounts(connectionId,storeId);
            model.put("successfully_deleted", true);
            model.put("store_id", storeId);
            context.render(ModelPaths.ResetStoreDiscounts, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.ResetStoreDiscounts, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.ResetStoreDiscounts, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.ResetStoreDiscounts, model);
        }

        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.ResetStoreDiscounts, model);
        }

        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.ResetStoreDiscounts, model);
        }
    };

    public Handler getResetStorePolicy = context -> {
        context.render(ModelPaths.ResetStorePolicy, returnModel(context));
    };

    public Handler postResetStorePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            marketService.resetStorePolicy(connectionId,storeId);
            model.put("successfully_deleted", true);
            model.put("store_id", storeId);
            context.render(ModelPaths.ResetStorePolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.ResetStorePolicy, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.ResetStorePolicy, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.ResetStorePolicy, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.ResetStorePolicy, model);
        }
    };
    public Handler getAddBidPermission = context -> {
        context.render(ModelPaths.AddBidPermission, returnModel(context));
    };

    public Handler postAddBidPermission = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            String managerName = Request.getStringFormParam(context, "user_name");
            marketService.addBidPermission(connectionId,storeId, managerName);
            model.put("success_added", true);
            model.put("user_name", managerName);
            context.render(ModelPaths.AddBidPermission, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
        catch (AlreadyOwnerException e) {
            model.put("Already_Owner_Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
        catch (AlreadyManagerException e) {
            model.put("Already_Manager_Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
        catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
//        catch (NotManagerException e) {
//            model.put("Not_Manager_Exception", e);
//            context.render(ModelPaths.AddBidPermission, model);
//        }
//        catch (UserCantAppointHimself e) {
//            model.put("User_Cant_Appoint_Himself", e);
//            context.render(ModelPaths.AddBidPermission, model);
//        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddBidPermission, model);
        }
    };

    public Handler getDeleteBidPermission = context -> {
        context.render(ModelPaths.DeleteBidPermission, returnModel(context));
    };


    public Handler postDeleteBidPermission = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int storeId= Request.getIntegerFormParam(context,"store_id");
            String managerName = Request.getStringFormParam(context, "user_name");
            marketService.deleteBidPermission(connectionId,storeId, managerName);
            model.put("successfully_deleted", true);
            model.put("user_name", managerName);
            context.render(ModelPaths.DeleteBidPermission, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.DeleteBidPermission, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteBidPermission, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.DeleteBidPermission, model);
        }
        catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.DeleteBidPermission, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.DeleteBidPermission, model);
        }
//        catch (NotManagerException e) {
//            model.put("Not_Manager_Exception", e);
//            context.render(ModelPaths.DeleteBidPermission, model);
//        }
//        catch (UserCantAppointHimself e) {
//            model.put("User_Cant_Appoint_Himself", e);
//            context.render(ModelPaths.DeleteBidPermission, model);
//        }

        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.DeleteBidPermission, model);
        }
    };



    public Handler getMembershipCancellation = context -> {
        context.render(ModelPaths.MembershipCancellation, returnModel(context));
    };

    public Handler serveGetTotalVisitorsByAdminPerDay = ctx -> {
        Map<String, Object> model = WebView.initModel(ctx);
        ctx.render(ModelPaths.GETTOTALVISITORSPERDAYBYADMIN, model);
    };

    public Handler handleGetTotalVisitorsByAdminPerDayPost = ctx -> {
        Map<String, Object> model = WebView.initModel(ctx);
        try{
            model.put("visitors", marketService.getTotalVisitorsByAdminPerDay(Request.getDate(ctx)));
            ctx.render(ModelPaths.GETTOTALVISITORSPERDAYBYADMIN, model);
        }catch (Exception e) {
            model.put("failed", true);
            ctx.render(ModelPaths.HomePage, model);
        }
    };


    public Handler postMembershipCancellation = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            String userName = Request.getStringFormParam(context, "user_name");
            marketService.membershipCancellation(connectionId, userName);
            model.put("success_cancellation", true);
            model.put("user_name", userName);
            context.render(ModelPaths.MembershipCancellation, model);
        }
        catch (NotAdminException e) {
            model.put("Not_Admin_Exception", e);
            context.render(ModelPaths.MembershipCancellation, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.MembershipCancellation, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.MembershipCancellation, model);
        }
        catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.MembershipCancellation, model);
        }
        catch (SubscriberHasRolesException e) {
            model.put("Subscriber_Has_Roles_Exception", e);
            context.render(ModelPaths.MembershipCancellation, model);
        }
      catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };


    public Handler postPurhcaseHistoryAdmin = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
                Collection<String> purchaseHistory=marketService.adminPurchaseHistory(connectionId, store_id);
            model.put("purchase_history", purchaseHistory);
            model.put("store_id", store_id);
            context.render(ModelPaths.PurchaseHistoryAdmin, model);
        }
        catch (NotAdminException e) {
            model.put("Not_Admin_Exception", e);
            context.render(ModelPaths.PurchaseHistoryAdmin, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.PurchaseHistoryAdmin, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PurchaseHistoryAdmin, model);
        }
    };

    public Handler postRemoveAppointment = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String username = Request.getStringFormParam(context, "user_name");
            String role = Request.getStringFormParam(context, "role_droplist");
            if (role.equals("manager")) {
                marketService.removeManager(connectionId, store_id, username);
                model.put("appointment", true);
            }
            if (role.equals("owner")) {
                marketService.removeOwner(connectionId, store_id, username);
                model.put("appointment", true);
            }
            model.put("role", role);
            context.render(ModelPaths.RemoveAppointment, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.RemoveAppointment, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.RemoveAppointment, model);

        }  catch (UserNameNotExistException e) {
            model.put("User_Name_Not_Exist_Exception", e);
            context.render(ModelPaths.RemoveAppointment, model);
        }
        catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.RemoveAppointment, model);
        }
        catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.RemoveAppointment, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.RemoveAppointment, model);
        }
        catch (NotOwnerByException e) {
            model.put("Not_Owner_By_Exception", e);
            context.render(ModelPaths.RemoveAppointment, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.RemoveAppointment, model);
        }
    };

    public Handler getStoreRoles = context -> {
        context.render(ModelPaths.StoreRoles, returnModel(context));
    };

    public Handler postStoreRoles = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionID = Request.getStringFormParam(context, "connectionID");
            int storeId = Request.getIntegerFormParam(context, "store_id");
            Collection<String> cart = marketService.storeRoles(connectionID, storeId);
            model.put("store_roles", cart);
            model.put("store_id", storeId);
            context.render(ModelPaths.StoreRoles, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.StoreRoles, model);
        }
        catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.StoreRoles, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.StoreRoles, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.StoreRoles, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.StoreRoles, model);
        }
    };

    public Handler getCloseStore = context -> {
        context.render(ModelPaths.CloseStore, returnModel(context));
    };

    public Handler postCloseStore = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionID = Request.getStringFormParam(context, "connectionID");
            int storeId = Request.getIntegerFormParam(context, "store_id");
            marketService.closeStore(connectionID, storeId);
            model.put("storeID", storeId);
            context.render(ModelPaths.CloseStore, model);
        } catch (ConnectionNotFoundException e) {
            model.put("ConnectionID_Not_Found_Exception", e);
            context.render(ModelPaths.CloseStore, model);
        }
        catch (StoreNotActiveException e) {
            model.put("Store_Not_Active_Exception", e);
            context.render(ModelPaths.CloseStore, model);
        }
        catch (NotFounderException e) {
            model.put("Not_Founder_Exception", e);
            context.render(ModelPaths.CloseStore, model);
        }
        catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.CloseStore, model);
        }
        catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.CloseStore, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.CloseStore, model);
        }
    };

    public Handler postShowPurchaseHistoryAdmin = context -> {
        context.render(ModelPaths.PurchaseHistoryAdmin, returnModel(context));
    };

    public Handler postShowPurchaseCart = context -> {
        context.render(ModelPaths.PurchaseCart, returnModel(context));
    };

    public Handler getPurchaseCart = context -> {
        context.render(ModelPaths.PurchaseCart, returnModel(context));
    };

    public Handler postShowCartPrice = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionID = Request.getStringFormParam(context, "connectionID");
            double price=marketService.getCartPrice(connectionID);
            model.put("cart_price", price);
        }
/*        catch (EmptyCartException e) {
            model.put("Empty_Cart_Exception", e);
            context.render(ModelPaths.ShowCart, model);
        }
        catch(ConnectionNotFoundException e){
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.ShowCart, model);
        }*/
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.ShowCart, model);
        }
    };

    public Handler postPurchaseCart = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionID = Request.getStringFormParam(context, "connectionID");
            String cardNumber = Request.getStringFormParam(context, "card_number");
            int month = Request.getIntegerFormParam(context, "month");
            int year = Request.getIntegerFormParam(context, "year");
            String holder = Request.getStringFormParam(context, "holder");
            String ccv = Request.getStringFormParam(context, "ccv");
            String id = Request.getStringFormParam(context, "id");
            String name = Request.getStringFormParam(context, "name");
            String address = Request.getStringFormParam(context, "address");
            String city = Request.getStringFormParam(context, "city");
            String country = Request.getStringFormParam(context, "country");
            int zipCode = Request.getIntegerFormParam(context, "zip_code");
            marketService.purchaseCart(connectionID, cardNumber, month, year, holder, ccv, id, name, address, city, country, zipCode);
                model.put("Successful_Purchase", true);
            context.render(ModelPaths.PurchaseCart, model);
        }catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.PurchaseCart, model);
        }catch (InsufficientInventory e) {
            model.put("Insufficient_Inventory", e);
            context.render(ModelPaths.PurchaseCart, model);
        }
        catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.PurchaseCart, model);
        }
        catch (BagNotValidPolicyException e) {
            model.put("Bag_Not_Valid_Policy_Exception", e);
            context.render(ModelPaths.PurchaseCart, model);
        }
        catch (IllegalDiscountException e) {
            model.put("Illegal_Discount_Exception", e);
            context.render(ModelPaths.PurchaseCart, model);
        }
        catch (PaymentException e) {
            model.put("Payment_Exception", e);
            context.render(ModelPaths.PurchaseCart, model);
        }
        catch (EmptyCartException e) {
            model.put("Empty_Cart_Exception", e);
            context.render(ModelPaths.PurchaseCart, model);
        }
        catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PurchaseCart, model);
        }
    };

    public Handler getShowStoreDiscount = context -> {
        context.render(ModelPaths.ShowStoreDiscount, returnModel(context));
    };

    public Handler getShowStorePolicy = context -> {
        context.render(ModelPaths.ShowStorePolicy, returnModel(context));
    };

    public Handler postShowStorePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection <String> policies = marketService.getStorePoliciesID(connectionId,store_id);
            model.put("policies", policies);
            context.render(ModelPaths.ShowStorePolicy, model);
        }catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.ShowStorePolicy, model);
        }catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.ShowStorePolicy, model);
        }catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.ShowStorePolicy, model);
        }catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.ShowStorePolicy, model);
        }catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.ShowStorePolicy, model);
        }
    };

    public Handler postShowStoreDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection <String> discounts = marketService.getStoreDiscountsID(connectionId,store_id);
            model.put("discounts",discounts);
            context.render(ModelPaths.ShowStoreDiscount, model);
        }catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.ShowStoreDiscount, model);
        }catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.ShowStoreDiscount, model);
        }catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.ShowStoreDiscount, model);
        }catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.ShowStoreDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.ShowStoreDiscount, model);
        }
    };

    public Handler postAndPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> policies = Request.getIntegerMultipleParam(context, "policies");
            int final_policy = marketService.andPolicy(connectionId, store_id, policies);
            model.put("policy", final_policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        }catch (PolicyNotFoundException e) {
            model.put("Policy_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }catch (PolicyAlreadyExistsException e) {
            model.put("Policy_Already_Exists_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postOrPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> policies = Request.getIntegerMultipleParam(context, "policies");
            int final_policy = marketService.orPolicy(connectionId, store_id, policies);
            model.put("policy", final_policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        }catch (PolicyNotFoundException e) {
            model.put("Policy_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }catch (PolicyAlreadyExistsException e) {
            model.put("Policy_Already_Exists_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postXorPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> policies = Request.getIntegerMultipleParam(context, "policies");
            int final_policy = marketService.xorPolicy(connectionId, store_id, policies);
            model.put("policy", final_policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        }catch (PolicyNotFoundException e) {
            model.put("Policy_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }catch (PolicyAlreadyExistsException e) {
            model.put("Policy_Already_Exists_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getMaxDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreDiscountsID(connectionId, store_id);
            model.put("discounts", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.MaxDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler postMaxDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> discounts = Request.getIntegerMultipleParam(context, "discounts");
            int discount = marketService.addMaxDiscount(connectionId, store_id, discounts);
            model.put("discount", discount);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (DiscountAlreadyExistsException e) {
            model.put("Discount_Already_Exists_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (IllegalDiscountException e) {
            model.put("Illegal_Discount_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (CantRemoveProductException e) {
            model.put("Cant_Remove_Product_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (DiscountNotFoundException e) {
            model.put("Discount_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (NotCompoundException e) {
            model.put("Not_Compound_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (CantRemoveDiscountException e) {
            model.put("Cant_Remove_Discount_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler getAddPolicy = context -> {
        context.render(ModelPaths.AddPolicy, returnModel(context));
    };

    public Handler getAddDiscount = context -> {
         context.render(ModelPaths.AddDiscount, returnModel(context));
    };

    public Handler getComplexPolicies = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStorePoliciesID(connectionId, store_id);
            model.put("policies", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.ComplexPolicies, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getTimePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreProductsByName(connectionId, store_id);
            model.put("products", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.TimePolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postTimePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> products = Request.getIntegerMultipleParam(context, "products");
            String start_time = Request.getStringFormParam(context, "start_time");
            String end_time = Request.getStringFormParam(context, "end_time");
            int policy = marketService.addTimePolicy(connectionId,store_id,products,start_time,end_time);
            model.put("policy", policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalTimeRangeException e) {
            model.put("Illegal_Time_Range_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getQuantityPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreProductsByName(connectionId, store_id);
            model.put("products", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.QuantityPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postQuantityPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> products = Request.getIntegerMultipleParam(context, "products");
            int min_quantity = Request.getIntegerFormParam(context, "min_quantity");
            int max_quantity = Request.getIntegerFormParam(context, "max_quantity");
            int policy = marketService.addAmountLimitationPolicy(connectionId,store_id,products,min_quantity,max_quantity);
            model.put("policy", policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalRangeException e) {
            model.put("Illegal_Range_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getMinimalCartPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            marketService.hasPermissions(connectionId, store_id);
            model.put("store_id", store_id);
            context.render(ModelPaths.MinimalCartPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postMinimalCartPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            int min_price = Request.getIntegerFormParam(context, "min_price");
            int policy = marketService.addMinimalCartPricePolicy(connectionId,store_id,min_price);
            model.put("policy", policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getMaximalCartPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            marketService.hasPermissions(connectionId, store_id);
            model.put("store_id", store_id);
            context.render(ModelPaths.MaximalCartPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postMaximalCartPolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            int max_price = Request.getIntegerFormParam(context, "max_price");
            int policy = marketService.addMaximalCartPricePolicy(connectionId,store_id,max_price);
            model.put("policy", policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getAssignConditionalDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> policies = marketService.getPoliciesOfAllStoresExceptOne(store_id);
            Collection<String> discounts = marketService.getDiscountsOfAllStoresExceptOne(store_id);
            model.put("policies", policies);
            model.put("discounts", discounts);
            marketService.hasPermissions(connectionId, store_id);
            model.put("store_id", store_id);
            context.render(ModelPaths.AssignConditionalDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler postAssignConditionalDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            int policy_id = Request.getIntegerFromStringParam(context, "policies");
            int discount_id = Request.getIntegerFromStringParam(context, "discounts");
            int discount = marketService.assignConditionalDiscount(connectionId,policy_id,store_id,discount_id);
            model.put("discount", discount);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler getAddConditionalDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreProductsByName(connectionId, store_id);
            Collection<String> policies = marketService.getPoliciesOfAllStoresExceptOne(store_id);
            model.put("products", products);
            model.put("policies", policies);
            model.put("store_id", store_id);
            context.render(ModelPaths.AddConditionalDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler postAddConditionalDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> products = Request.getIntegerMultipleParam(context, "products");
            Integer policy_id = Request.getIntegerFromStringParam(context, "policies");
            int percent = Request.getIntegerFormParam(context, "percent");
            int discount = marketService.addConditionalDiscount(connectionId,policy_id,store_id,percent,products);
            model.put("discount", discount);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler getCartRangePricePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            marketService.hasPermissions(connectionId, store_id);
            model.put("store_id", store_id);
            context.render(ModelPaths.CartRangePricePolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postCartRangePricePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            int min_price = Request.getIntegerFormParam(context, "min_price");
            int max_price = Request.getIntegerFormParam(context, "max_price");
            int policy = marketService.addCartRangePricePolicy(connectionId,store_id,min_price,max_price);
            model.put("policy", policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getForbiddenDatePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreProductsByName(connectionId, store_id);
            model.put("products", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.ForbiddenDatePolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler postForbiddenDatePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> products = Request.getIntegerMultipleParam(context, "products");
            String[] date = Request.getStringFormParam(context, "date").split("-");
            int policy = marketService.addForbiddenDatePolicy(connectionId,store_id,products,
                    Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
            model.put("policy", policy);
            context.render(ModelPaths.AddPolicy, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }  catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddPolicy, model);
        }
    };

    public Handler getProductDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreProductsByName(connectionId, store_id);
            model.put("products", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.ProductDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler postProductDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            Collection<Integer> products = Request.getIntegerMultipleParam(context, "products");
            int percent = Request.getIntegerFormParam(context, "discount");
            int discount = marketService.addSimpleDiscountByProducts(connectionId,store_id,products,percent);
            model.put("discount", discount);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalDiscountException e) {
            model.put("Illegal_Discount_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (DiscountAlreadyExistsException e) {
            model.put("Discount_Already_Exists_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler getCategoryDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> categories = marketService.getStoreProductsByCategory(connectionId, store_id);
            model.put("categories", categories);
            model.put("store_id", store_id);
            context.render(ModelPaths.CategoryDiscount, model);
        } catch(ConnectionNotFoundException e){
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch(StoreNotFoundException e){
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch(NotOwnerException e){
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch(UserNotSubscriberException e){
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler postCategoryDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String category = Request.getStringFormParam(context, "categories");
            int percent = Request.getIntegerFormParam(context, "discount");
            int discount = marketService.addSimpleDiscountByCategory(connectionId,store_id,category.substring(17),percent);
            model.put("discount", discount);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalDiscountException e) {
            model.put("Illegal_Discount_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (DiscountAlreadyExistsException e) {
            model.put("Discount_Already_Exists_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler getStoreDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            marketService.hasPermissions(connectionId,store_id);
            model.put("store_id", store_id);
            context.render(ModelPaths.StoreDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler postStoreDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try{
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            int percent = Request.getIntegerFormParam(context, "discount");
            int discount = marketService.addSimpleDiscountByStore(connectionId,store_id,percent);
            model.put("discount", discount);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ConnectionNotFoundException e) {
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotOwnerException e) {
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (StoreNotFoundException e) {
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (UserNotSubscriberException e) {
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (ProductNotFoundException e) {
            model.put("Product_Not_Found_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalDiscountException e) {
            model.put("Illegal_Discount_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (IllegalPermissionsAccess e) {
            model.put("Illegal_Permissions_Access", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (NotStorePolicyException e) {
            model.put("Not_Store_Policy_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (DiscountAlreadyExistsException e) {
            model.put("Discount_Already_Exists_Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.AddDiscount, model);
        }
    };

    public Handler getPreDeleteDiscount = context -> {
        context.render(ModelPaths.PreDeleteDiscount, returnModel(context));
    };

    public Handler getDeleteDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreDiscountsID(connectionId, store_id);
            model.put("discounts", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.DeleteDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreDeleteDiscount, model);
        }
    };

    public Handler postDeleteDiscount = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String discount = Request.getStringFormParam(context, "discounts");
            int discount_id = Integer.parseInt((discount.split("id: ")[1]).split(",")[0]);
            marketService.deleteDiscountFromStore(connectionId, store_id, discount_id);
            model.put("Successful_Delete", true);
            context.render(ModelPaths.PreDeleteDiscount, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreDeleteDiscount, model);
        }
    };

    public Handler getPreDeletePolicy = context -> {
        context.render(ModelPaths.PreDeletePolicy, returnModel(context));
    };

    public Handler getDeletePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStorePoliciesID(connectionId, store_id);
            model.put("policies", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.DeletePolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreDeletePolicy, model);
        }
    };

    public Handler postDeletePolicy = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String policy = Request.getStringFormParam(context, "policies");
            int policy_id = Integer.parseInt((policy.split("id: ")[1]).split(",")[0]);
            marketService.deletePolicyFromStore(connectionId,store_id,policy_id);
            model.put("Successful_Delete", true);
            context.render(ModelPaths.PreDeletePolicy, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreDeletePolicy, model);
        }
    };

    public Handler getRemoveAppointment = context -> {
        context.render(ModelPaths.RemoveAppointment, returnModel(context));
    };

    public Handler getPreBid = context -> {
        context.render(ModelPaths.PreBid, returnModel(context));
    };

    public Handler getShowBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> bids = marketService.getBidsByStore(connectionId, store_id);
            model.put("bids", bids);
            model.put("store_id", store_id);
            context.render(ModelPaths.ShowBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler getRejectBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> bids = marketService.getNotApprovedYetBids(connectionId, store_id);
            model.put("bids", bids);
            model.put("store_id", store_id);
            context.render(ModelPaths.RejectBid, model);
        } catch(ConnectionNotFoundException e){
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(StoreNotFoundException e){
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(NotOwnerException e){
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(UserNotSubscriberException e){
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler postRejectBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String bid = Request.getStringFormParam(context, "bids");
            int bid_id = Integer.parseInt((bid.split("id: ")[1]).split(",")[0]);
            marketService.rejectBid(connectionId, store_id, bid_id);
            model.put("Action", "Successful Reject");
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler getAddCounterBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> bids = marketService.getBidsForAddCounteredString(connectionId, store_id);
            model.put("bids", bids);
            model.put("store_id", store_id);
            context.render(ModelPaths.AddCounterBid, model);
        } catch(ConnectionNotFoundException e){
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(StoreNotFoundException e){
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(NotOwnerException e){
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(UserNotSubscriberException e){
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler postAddCounterBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String bid = Request.getStringFormParam(context, "bids");
            int bid_id = Integer.parseInt((bid.split("id: ")[1]).split(",")[0]);
            int price = Request.getIntegerFormParam(context, "price");
            int amount = Request.getIntegerFormParam(context, "amount");
            marketService.addCounteredBid(connectionId, store_id,bid_id,amount,price);
            model.put("bid", bid);
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler getApproveBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> bids = marketService.getNotApprovedYetBids(connectionId, store_id);
            model.put("bids", bids);
            model.put("store_id", store_id);
            context.render(ModelPaths.ApproveBid, model);
        } catch(ConnectionNotFoundException e){
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(StoreNotFoundException e){
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(NotOwnerException e){
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(UserNotSubscriberException e){
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler postApproveBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String bid = Request.getStringFormParam(context, "bids");
            int bid_id = Integer.parseInt((bid.split("id: ")[1]).split(",")[0]);
            marketService.approveBid(connectionId, store_id, bid_id);
            model.put("Action", "Successful Approve");
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler getAddBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringQueryParam(context, "connectionID");
            int store_id = Request.getIntegerQueryParam(context, "store_id");
            Collection<String> products = marketService.getStoreProductsNameNoOwner(connectionId, store_id);
            model.put("products", products);
            model.put("store_id", store_id);
            context.render(ModelPaths.AddBid, model);
        } catch(ConnectionNotFoundException e){
            model.put("Connection_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(StoreNotFoundException e){
            model.put("Store_Not_Found_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(NotOwnerException e){
            model.put("Not_Owner_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch(UserNotSubscriberException e){
            model.put("User_Not_Subscriber_Exception", e);
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Handler postAddBid = context -> {
        Map<String, Object> model = returnModel(context);
        try {
            String connectionId = Request.getStringFormParam(context, "connectionID");
            int store_id = Request.getIntegerFormParam(context, "store_id");
            String product = Request.getStringFormParam(context, "products");
            int product_id = Integer.parseInt((product.split("id: ")[1]).split(",")[0]);
            int price = Request.getIntegerFormParam(context, "price");
            int amount = Request.getIntegerFormParam(context, "amount");
            int bid = marketService.addBid(connectionId, store_id, product_id,price,amount);
            model.put("bid", bid);
            context.render(ModelPaths.PreBid, model);
        } catch (Exception e) {
            model.put("Exception", e);
            context.render(ModelPaths.PreBid, model);
        }
    };

    public Market(Service marketService) throws Exception {
        this.marketService = marketService;
    }

    public Market(int flag) throws Exception {
        DatabaseFetcher db = new DatabaseFetcher();
        String adminName = "Admin";
        String admPsw = "123";
        Map<String, User> users = new HashMap<>();
        Map<String, Subscriber> subscribers = db.getSubscribers();
        Map<Integer, Store> stores = db.getStores();
        Map<String, User> admins = new HashMap<>();
        // initialize the system with db
        UserValidation userValidation;
        if(Repo.isDBEmpty())
        {
            userValidation = new UserValidation();
            userValidation.register(adminName, admPsw);
            Repo.persist(userValidation);
        }
        else
            userValidation = db.getValidation();
        //initiate system with admin.
//        userValidation.register(adminName, admPsw);
        Subscriber admin = subscribers.get(adminName);
        if(admin == null)
        {
            admin = new Subscriber(adminName);
            Repo.persist(admin);
        }
        else
            Repo.merge(admin);
        admins.put(adminName, admin);
        subscribers.put(adminName, admin);
        System system = new System(new DeliveryAdapterImpl(), new PaymentAdapterImpl(), userValidation, users, subscribers, stores, admins);
        marketService = new MarketService(system);
        try {
            marketService.registerAdmin(adminName, admPsw);
        }catch (Exception e)
        {

        }
        if (flag==1)
            scenario1();
        if (flag==2)
            scenario2();
        if (flag==3)
            scenario3();
        if (flag==4)
            scenario4();
    }

    public void scenario1() throws Exception {
        String conId1= marketService.entrance();
        marketService.register("u1", "123");
        marketService.register("u2", "123");
        marketService.register("u3", "123");
        marketService.login(conId1, "u1", "123");
        int newstore1= marketService.openStore(conId1, "s1");
        marketService.appointStoreOwner(conId1, newstore1, "u2");
        marketService.logout(conId1);
        marketService.login(conId1, "u2", "123");
        marketService.appointStoreOwner(conId1, newstore1, "u3");
        marketService.logout(conId1);
        marketService.login(conId1, "u3", "123");
        try{
            marketService.removeOwner(conId1, newstore1, "u2");
        }
        catch (NotOwnerByException e){
            java.lang.System.out.println(e.toString());
        }
        marketService.logout(conId1);
        marketService.login(conId1, "u1", "123");
        marketService.removeOwner(conId1, newstore1, "u2");
        marketService.logout(conId1);
    }

    public void scenario2() throws Exception {
        String conId1= marketService.entrance();
        marketService.register("u1", "123");
        marketService.register("u2", "123");
        marketService.register("u3", "123");
        marketService.login(conId1, "u1", "123");
        int newstore1= marketService.openStore(conId1, "s1");
        marketService.appointStoreOwner(conId1, newstore1, "u2");
        marketService.logout(conId1);
        marketService.login(conId1, "u3", "123");
        int newstore2= marketService.openStore(conId1, "s2");
        marketService.appointStoreOwner(conId1, newstore2, "u1");
        marketService.appointStoreOwner(conId1, newstore2, "u2");
        marketService.logout(conId1);
        marketService.login(conId1, "u1", "123");
        try{
            marketService.removeOwner(conId1, newstore2, "u2");
        }
        catch (NotOwnerByException e){
            java.lang.System.out.println(e.toString());
        }

    }

    public void scenario3() throws Exception {
        String conId1= marketService.entrance();
        marketService.register("u1", "123");
        marketService.register("u2", "123");
        marketService.login(conId1, "u1", "123");
        int newstore1= marketService.openStore(conId1, "s1");
        marketService.logout(conId1);
        marketService.login(conId1, "Admin", "123");
        marketService.membershipCancellation(conId1, "u2");
        try {
            marketService.membershipCancellation(conId1, "u1");
        }
        catch (SubscriberHasRolesException e){
            java.lang.System.out.println(e.toString());
        }
    }

    public void scenario4() throws Exception {
        try{
            String u1 = "U1", u2 = "U2", u3 = "U3", u4 = "U4", u5 = "U5", u6 = "U6";
            String password = "123";
            marketService.registerAdmin(u1, password);
            marketService.register(u2, password);
            marketService.register(u3, password);
            marketService.register(u4, password);
            marketService.register(u5, password);
            marketService.register(u6, password);
            String u1Conn = marketService.entrance();
            marketService.login(u1Conn, u1, password);
            String u2Conn = marketService.entrance();
            marketService.login(u2Conn, u2, password);
            String u3Conn = marketService.entrance();
            marketService.login(u3Conn, u3, password);
            String u4Conn = marketService.entrance();
            marketService.login(u4Conn, u4, password);
            String u5Conn = marketService.entrance();
            marketService.login(u5Conn, u5, password);
            String u6Conn = marketService.entrance();
            marketService.login(u6Conn, u6, password);
            int s1Store = marketService.openStore(u2Conn, "s1");
            marketService.addProductToStore(u2Conn, s1Store, "Bamba", "Snacks", "Baked Peanut Snack", 20, 30);
            marketService.appointStoreManager(u2Conn,s1Store, u3);
            marketService.addInventoryManagementPermission(u2Conn,s1Store, u3);
            marketService.appointStoreOwner(u2Conn,s1Store, u4);
            marketService.appointStoreOwner(u2Conn,s1Store, u5);
            marketService.logout(u2Conn);
        }
         catch (Exception e){

         }
    }



}
