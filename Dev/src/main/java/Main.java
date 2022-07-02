import DAL.DatabaseFetcher;
import DAL.Repo;
import Exceptions.*;
import GUI.WebSitesPaths;
import Notifications.Notification;
import Notifications.Observer;
import OutResources.DeliveryAdapter;
import OutResources.PaymentAdapter;
import Security.UserValidation;
import Service.MarketService;
import Service.Service;
import System.System;
import Store.Store;
import User.Subscriber;
import User.User;
import User.Visitors;
import io.javalin.Javalin;

import GUI.*;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsContext;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {

    private static class WsObserver implements Observer {

        private WsContext ctx;

        public WsObserver(WsContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void notify(Notification notification) {
            if(ctx.session.isOpen())
                ctx.send(notification.toString());
        }
    }

    public static Market market;

    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure("Dev/src/log4j.properties");
        Configuration cfg = new Configuration();
        String configPath = "Dev/Configuration/configurations.properties";
        if(args.length > 1)
            configPath = args[1];
        try (InputStream input = new FileInputStream(configPath)) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            cfg.adminName = prop.getProperty("system.admin.username");
            cfg.adminPassword = prop.getProperty("system.admin.password");
            cfg.port = Integer.parseInt(prop.getProperty("port"));
            cfg.sslPort = Integer.parseInt(prop.getProperty("sslPort"));
            cfg.initFileAddress = prop.getProperty("stateFileAddress");
            cfg.startupScript = prop.getProperty("startupScript");
            cfg.paymentSystem = prop.getProperty("paymentAdapter");
            cfg.deliverySystem = prop.getProperty("deliveryAdapter");
            Repo.setPersistence_unit(prop.getProperty("persistence.unit"));
        }catch (FileNotFoundException e) {
            Logger.getLogger(MarketService.class).error
            ("initialization of the system made with non-exist properties file");
            java.lang.System.out.println("initialization of the system made with non-exist properties file");
        } catch (IOException e) {
            Logger.getLogger(MarketService.class).error
            (e.getMessage());
            java.lang.System.out.println(e.getMessage());
        }

        int flag = 0;
        if(Repo.isDBEmpty())
        {
            java.lang.System.out.println("Choose the number of script you want to run\n1/2/3/4");
            Scanner scan= new Scanner(java.lang.System.in);
            flag = scan.nextInt();
            cfg.initFileAddress = cfg.initFileAddress.substring(0,27) + flag + cfg.initFileAddress.substring(27);
            cfg.startupScript = cfg.startupScript + flag;
        }
        run(cfg, flag);

        //init a local http server
        Javalin app = Javalin.create(config -> {
            config.server(() -> {
                Server server = new Server();
                ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
                sslConnector.setPort(555);
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(91);
                server.setConnectors(new Connector[]{sslConnector, connector});
                return server;
            });
            config.addStaticFiles("/images", Location.CLASSPATH);
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
        }
        ).start(HerokuUtil.getHerokuAssignedPort());

        app.ws("/Notifications", ws -> {
            ws.onMessage(ctx -> {
                User user = market.getMarketService().getSystemUser(ctx.message());
                Subscriber subscriber = (Subscriber) user;
                ctx.attribute("subscriber", subscriber);
                Observer wsObserver = new WsObserver(ctx);
                subscriber.setObserver(wsObserver);
            });
            ws.onClose(ctx -> {
                Subscriber subscriber = ctx.attribute("subscriber");
                subscriber.setObserver(null);
            });
        });

        app.ws("/visitors", ws -> {
            ws.onClose(ctx -> {
                Subscriber subscriber = ctx.attribute("subscriber");
                subscriber.setAdminObserver(null);
            });
            ws.onMessage(ctx -> {
                Subscriber subscriber = (Subscriber) market.getMarketService().getSystemUser(ctx.message());
                ctx.attribute("subscriber", subscriber);
                Observer wsObserver = new WsObserver(ctx);
                subscriber.setAdminObserver(wsObserver);
            });
        });

        app.routes(() -> {
            get(WebSitesPaths.HomePage, market.getHomePage);
            post(WebSitesPaths.HomePage, market.postHomePage);

            get(WebSitesPaths.Login, market.getLogin);
            post(WebSitesPaths.Login, market.postLogin);

            get(WebSitesPaths.Register, market.getRegister);
            post(WebSitesPaths.Register, market.postRegister);

            get(WebSitesPaths.UpdateCart, market.getUpdateCart);
            post(WebSitesPaths.UpdateCart, market.postUpdateCart);

            get(WebSitesPaths.ShowCart, market.getShowCart);
            post(WebSitesPaths.ShowCart, market.postShowCart);

            get(WebSitesPaths.OpenStore, market.getOpenStore);
            post(WebSitesPaths.OpenStore, market.postOpenStore);

            get(WebSitesPaths.CloseStore, market.getCloseStore);
            post(WebSitesPaths.CloseStore, market.postCloseStore);

            get(WebSitesPaths.AddProduct, market.getAddProduct);
            post(WebSitesPaths.AddProduct, market.postAddProduct);

            get(WebSitesPaths.Admin, market.getAdmin);

            get(WebSitesPaths.ErrorLog, market.getErrorLog);
            post(WebSitesPaths.ErrorLog, market.postErrorLog);

            get(WebSitesPaths.EventLog, market.getEventLog);
            post(WebSitesPaths.EventLog, market.postEventLog);

            get(WebSitesPaths.PurchaseHistoryAdmin, market.getPurhcaseHistoryAdmin);
            post(WebSitesPaths.PurchaseHistoryAdmin, market.postPurhcaseHistoryAdmin);

            post(WebSitesPaths.Logout, market.postLogout);

            get(WebSitesPaths.Search, market.getSearch);

            get(WebSitesPaths.DeleteProduct, market.getDeleteProduct);
            post(WebSitesPaths.DeleteProduct, market.postDeleteProduct);

            get(WebSitesPaths.UpdateProduct, market.getUpdateProduct);
            post(WebSitesPaths.UpdateProduct, market.postUpdateProduct);

            get(WebSitesPaths.MarketInfo, market.getMarketInfo);
            post(WebSitesPaths.MarketInfo, market.postMarketInfo);

            get(WebSitesPaths.SearchByName, market.getSearchByName);
            post(WebSitesPaths.SearchByName, market.postSearchByName);

            get(WebSitesPaths.SearchByCategory, market.getSearchByCategory);
            post(WebSitesPaths.SearchByCategory, market.postSearchByCategory);

            get(WebSitesPaths.SearchByKeyword, market.getSearchByKeyword);
            post(WebSitesPaths.SearchByKeyword, market.postSearchByKeyword);

            get(WebSitesPaths.FilterByPrice, market.getFilterByPrice);
            post(WebSitesPaths.FilterByPrice, market.postFilterByPrice);

/*            get(WebSitesPaths.FilterByCategory, market.getFilterByCategory);
            post(WebSitesPaths.FilterByCategory, market.postFilterByCategory);*/

            get(WebSitesPaths.AppointStoreRoles, market.getAppointStoreRoles);
            post(WebSitesPaths.AppointStoreRoles, market.postAppointStoreRoles);

            post(WebSitesPaths.ShowPurchaseCart, market.postShowPurchaseCart);

            post(WebSitesPaths.ShowPurchaseHistoryAdmin, market.postShowPurchaseHistoryAdmin);


            post(WebSitesPaths.ShowCartPrice, market.postShowCartPrice);



            //post(WebSitesPaths.ShowPurchaseHistoryAdmin, market.postShowPurhcaseHistoryAdmin);

            get(WebSitesPaths.PurchaseCart, market.getPurchaseCart);
            post(WebSitesPaths.PurchaseCart, market.postPurchaseCart);

            get(WebSitesPaths.ShowStoreDiscount, market.getShowStoreDiscount);
            post(WebSitesPaths.ShowStoreDiscount, market.postShowStoreDiscount);

            get(WebSitesPaths.ShowStorePolicy, market.getShowStorePolicy);
            post(WebSitesPaths.ShowStorePolicy, market.postShowStorePolicy);

            get(WebSitesPaths.ComplexPolicies, market.getComplexPolicies);
            post(WebSitesPaths.AndPolicy, market.postAndPolicy);
            post(WebSitesPaths.OrPolicy, market.postOrPolicy);
            post(WebSitesPaths.XorPolicy, market.postXorPolicy);

            get(WebSitesPaths.AddPolicy, market.getAddPolicy);
            get(WebSitesPaths.TimePolicy, market.getTimePolicy);
            post(WebSitesPaths.TimePolicy, market.postTimePolicy);
            get(WebSitesPaths.QuantityPolicy, market.getQuantityPolicy);
            post(WebSitesPaths.QuantityPolicy, market.postQuantityPolicy);
            get(WebSitesPaths.MinimalCartPolicy, market.getMinimalCartPolicy);
            post(WebSitesPaths.MinimalCartPolicy, market.postMinimalCartPolicy);
            get(WebSitesPaths.MaximalCartPolicy, market.getMaximalCartPolicy);
            post(WebSitesPaths.MaximalCartPolicy, market.postMaximalCartPolicy);
            get(WebSitesPaths.CartRangePricePolicy, market.getCartRangePricePolicy);
            post(WebSitesPaths.CartRangePricePolicy, market.postCartRangePricePolicy);
            get(WebSitesPaths.ForbiddenDatePolicy, market.getForbiddenDatePolicy);
            post(WebSitesPaths.ForbiddenDatePolicy, market.postForbiddenDatePolicy);
            get(WebSitesPaths.AddConditionalDiscount, market.getAddConditionalDiscount);
            post(WebSitesPaths.AddConditionalDiscount, market.postAddConditionalDiscount);
            get(WebSitesPaths.AssignConditionalDiscount, market.getAssignConditionalDiscount);
            post(WebSitesPaths.AssignConditionalDiscount, market.postAssignConditionalDiscount);
            get(WebSitesPaths.PreDeletePolicy, market.getPreDeletePolicy);
            get(WebSitesPaths.DeletePolicy, market.getDeletePolicy);
            post(WebSitesPaths.DeletePolicy, market.postDeletePolicy);

            get(WebSitesPaths.AddDiscount, market.getAddDiscount);
            get(WebSitesPaths.CategoryDiscount, market.getCategoryDiscount);
            post(WebSitesPaths.CategoryDiscount, market.postCategoryDiscount);
            get(WebSitesPaths.MaxDiscount, market.getMaxDiscount);
            post(WebSitesPaths.MaxDiscount, market.postMaxDiscount);
            get(WebSitesPaths.StoreDiscount, market.getStoreDiscount);
            post(WebSitesPaths.StoreDiscount, market.postStoreDiscount);
            get(WebSitesPaths.ProductDiscount, market.getProductDiscount);
            post(WebSitesPaths.ProductDiscount, market.postProductDiscount);
            get(WebSitesPaths.PreDeleteDiscount, market.getPreDeleteDiscount);
            get(WebSitesPaths.DeleteDiscount, market.getDeleteDiscount);
            post(WebSitesPaths.DeleteDiscount, market.postDeleteDiscount);

            get(WebSitesPaths.RemoveAppointment, market.getRemoveAppointment);
            post(WebSitesPaths.RemoveAppointment, market.postRemoveAppointment);

            get(WebSitesPaths.StoreRoles, market.getStoreRoles);
            post(WebSitesPaths.StoreRoles, market.postStoreRoles);

            get(WebSitesPaths.PurchaseHistory, market.getPurhcaseHistory);
            post(WebSitesPaths.PurchaseHistory, market.postPurhcaseHistory);

            get(WebSitesPaths.InfoAboutSubscribers, market.getInfoAboutSubscribers);
            post(WebSitesPaths.InfoAboutSubscribers, market.postInfoAboutSubscribers);

            get(WebSitesPaths.MembershipCancellation, market.getMembershipCancellation);
            post(WebSitesPaths.MembershipCancellation, market.postMembershipCancellation);

            get(WebSitesPaths.AddInventoryManagementPermission, market.getAddInventoryManagementPermission);
            post(WebSitesPaths.AddInventoryManagementPermission, market.postAddInventoryManagementPermission);

            get(WebSitesPaths.DeleteInventoryManagementPermission, market.getDeleteInventoryManagementPermission);
            post(WebSitesPaths.DeleteInventoryManagementPermission, market.postDeleteInventoryManagementPermission);

            get(WebSitesPaths.AddStorePolicyManagementPermission, market.getAddStorePolicyManagementPermission);
            post(WebSitesPaths.AddStorePolicyManagementPermission, market.postAddStorePolicyManagementPermission);

            get(WebSitesPaths.DeleteStorePolicyManagementPermission, market.getDeleteStorePolicyManagementPermission);
            post(WebSitesPaths.DeleteStorePolicyManagementPermission, market.postDeleteStorePolicyManagementPermission);

            get(WebSitesPaths.ResetStoreDiscounts, market.getResetStoreDiscounts);
            post(WebSitesPaths.ResetStoreDiscounts, market.postResetStoreDiscounts);

            get(WebSitesPaths.ResetStorePolicy, market.getResetStorePolicy);
            post(WebSitesPaths.ResetStorePolicy, market.postResetStorePolicy);

            post(WebSitesPaths.GETTOTALVISITORSBYADMINPERDAY, market.handleGetTotalVisitorsByAdminPerDayPost);
            get(WebSitesPaths.GETTOTALVISITORSBYADMINPERDAY, market.serveGetTotalVisitorsByAdminPerDay);

            get(WebSitesPaths.AddBidPermission, market.getAddBidPermission);
            post(WebSitesPaths.AddBidPermission, market.postAddBidPermission);

            get(WebSitesPaths.DeleteBidPermission, market.getDeleteBidPermission);
            post(WebSitesPaths.DeleteBidPermission, market.postDeleteBidPermission);

            get(WebSitesPaths.PreBid, market.getPreBid);
            get(WebSitesPaths.AddBid, market.getAddBid);
            post(WebSitesPaths.AddBid, market.postAddBid);
            get(WebSitesPaths.ApproveBid, market.getApproveBid);
            post(WebSitesPaths.ApproveBid, market.postApproveBid);
            get(WebSitesPaths.RejectBid, market.getRejectBid);
            post(WebSitesPaths.RejectBid, market.postRejectBid);
            get(WebSitesPaths.AddCounterBid, market.getAddCounterBid);
            post(WebSitesPaths.AddCounterBid, market.postAddCounterBid);
            get(WebSitesPaths.ShowBid, market.getShowBid);

        });
    }

    public static void run(Configuration cfg, int flag) throws Exception {

        UserValidation userValidation;
        Visitors visitors;
        Map<String, User> users = new HashMap<>();
        Map<String, Subscriber> subscribers = new HashMap<>();
        Map<Integer, Store> stores = new HashMap<>();
        Map<String, User> admins = new HashMap<>();
        Subscriber admin = new Subscriber(cfg.adminName);
        subscribers.put(cfg.adminName, admin);
        admins.put(cfg.adminName, admin);
        PaymentAdapter paymentSystem = null;
        DeliveryAdapter deliverySystem = null;
        System system;
        Service marketService;

        try {
            Class<?> cls = Class.forName(cfg.paymentSystem, true, ClassLoader.getSystemClassLoader());
            paymentSystem = (PaymentAdapter) cls.getConstructor().newInstance();
            cls = Class.forName(cfg.deliverySystem, true, ClassLoader.getSystemClassLoader());
            deliverySystem = (DeliveryAdapter) cls.getConstructor().newInstance();

        } catch (ClassNotFoundException e) {
            Logger.getLogger(MarketService.class).error
            ("paymentSystem or deliverySystem in config.properties are wrong. paymentSystem: " + cfg.paymentSystem + ", deliverySystem: " + cfg.deliverySystem);
            java.lang.System.out.println("paymentSystem or deliverySystem in config.properties are wrong. paymentSystem: " + cfg.paymentSystem + ", deliverySystem: " + cfg.deliverySystem);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Logger.getLogger(MarketService.class).error
                    (e.getMessage());
            java.lang.System.out.println(e.getMessage());
        }

        // work around for the system initialization
        if (flag != 0)
        {
            userValidation = new UserValidation();
            try {
                userValidation.register(cfg.adminName, cfg.adminPassword);
            } catch (UserExistsException e) {
                try {
                    userValidation.validateUser(cfg.adminName, cfg.adminPassword);
                } catch (UserNotExistException userNotExistException) {
                    Logger.getLogger(MarketService.class).error
                    ("Wrong name for admin, name: " + cfg.adminName);
                } catch (WrongPasswordException wrongPasswordException) {
                    Logger.getLogger(MarketService.class).error
                    ("Wrong password for admin, name: " + cfg.adminName + ", password: ******");
                }
            }
            Repo.persist(admin);
            system = new System(deliverySystem, paymentSystem, userValidation, users, subscribers, stores, admins);
            marketService = new MarketService(system);
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            try {
                compiler.run(null, null, null, cfg.initFileAddress);
            } catch (NullPointerException e) {
                Logger.getLogger(MarketService.class).error
                ("State file is not exist. state file: " + cfg.initFileAddress);
                java.lang.System.out.println("State file is not exist. state file: " + cfg.initFileAddress);
            }
            try {
                Class<?> cls = Class.forName(cfg.startupScript, true, ClassLoader.getSystemClassLoader());
                Method method = cls.getMethod("run", Service.class);
                method.invoke(null, marketService);
            } catch (ClassNotFoundException e) {
                Logger.getLogger(MarketService.class).error
                ("Class for startupScript is not exist. startupScript: " + cfg.startupScript);
                java.lang.System.out.println("Class for startupScript is not exist. startupScript: " + cfg.startupScript);
            } catch (NoSuchMethodException e) {
                Logger.getLogger(MarketService.class).error
                ("method for startupScript is not exist.");
                java.lang.System.out.println("method for startupScript is not exist.");
            } catch (InvocationTargetException | IllegalAccessException e) {
                Logger.getLogger(MarketService.class).error(e.getMessage());
                java.lang.System.out.println(e.getMessage());
            }
        }

        else
        {
            DatabaseFetcher db = new DatabaseFetcher();
            userValidation = Repo.getValidation();
            visitors = Repo.getVisitors();

            subscribers = db.getSubscribers();
            stores = db.getStores();

            system = new System(deliverySystem, paymentSystem, userValidation, users, subscribers, stores, admins);
            system.setVisitors_in_system(visitors);
            marketService = new MarketService(system);
        }
        market = new Market(marketService);
    }

    private static SslContextFactory getSslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreType("PKCS12");
        sslContextFactory.setKeyStorePath(Main.class.getResource("/keystore/localhost.p12").toExternalForm());
        sslContextFactory.setKeyStorePassword("password");
        return sslContextFactory;
    }
}
