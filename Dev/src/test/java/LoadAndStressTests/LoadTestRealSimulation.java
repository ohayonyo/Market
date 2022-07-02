package LoadAndStressTests;

        import DAL.RepoMock;
        import Exceptions.ProductNotFoundException;
        import OutResources.DeliveryAdapterTimerMock;
        import OutResources.PaymentAdapterTimerMock;
        import Security.UserValidation;
        import Service.MarketService;
        import Store.Store;
        import System.System;
        import User.Subscriber;
        import User.User;
        import org.mockito.MockitoAnnotations;
        import org.testng.annotations.AfterClass;
        import org.testng.annotations.BeforeClass;
        import org.testng.annotations.Test;

        import java.util.Map;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.atomic.AtomicInteger;
        import static org.testng.AssertJUnit.assertTrue;

public class LoadTestRealSimulation {


    private MarketService service;
    private final String userName = "Admin";
    private final String password = "123";
    private final ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Store> stores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> systemAdmins = new ConcurrentHashMap<>();

    private final AtomicInteger subscriberId = new AtomicInteger(1);

    private final UserValidation userValidation = new UserValidation();
    private PaymentAdapterTimerMock paymentAdapter;
    private DeliveryAdapterTimerMock deliveryAdapter;
    private long start, end;
    private System system;
    private Map<Integer,Integer> productIdsPerStore=new ConcurrentHashMap<>();

    private final AtomicInteger index = new AtomicInteger(1);

    private int max = 500;



    @BeforeClass
    public void beforeClass() {
        RepoMock.enable();
    }

    @BeforeClass
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        deliveryAdapter = new DeliveryAdapterTimerMock();
        paymentAdapter = new PaymentAdapterTimerMock();
        systemAdmins.put(userName, new Subscriber());
        system = new System(deliveryAdapter, paymentAdapter, userValidation, users, subscribers, stores, systemAdmins);
        String adminIdConn = system.entrance();
        system.registerAdmin(userName, password);
        service = new MarketService(system);
        service.login(adminIdConn, userName, password);
        String conn;

        for(int i = 1; i <= 30; i++){
            conn = service.entrance();
            service.register("s" + i, "123");
            service.login(conn, "s" + i, "123");
            int store = service.openStore(conn, "s" + i);
            Integer pID=service.addProductToStore(conn, store, "banana", "fruit", "tasty", 500, 5.5);
            productIdsPerStore.put(i,pID);
            service.logout(conn);
        }

        for(int i = 31; i <= 100; i++){
            conn = service.entrance();
            service.register("s" + i, "123");
        }

        start = java.lang.System.nanoTime();
    }


    @Test(threadPoolSize = 10, invocationCount = 100, timeOut = 2000)
    public void test() throws Exception {
        String conn = service.entrance();
        int subscriberId = this.subscriberId.getAndIncrement();
        if(subscriberId < 70)
            service.login(conn, "s" + subscriberId, "123");
        if(subscriberId < 10)
        {
            service.appointStoreOwner(conn,  subscriberId, "s" + (subscriberId + 40));
        }
        else if(subscriberId < 30) {
            service.addProductToStore(conn, subscriberId, "apple" + subscriberId, "fruit", "tasty", 500, 5.5);
        }
        else if(subscriberId < 20) {
            try { service.updateShoppingBag(conn, subscriberId - 30, subscriberId, 5); }
            catch (ProductNotFoundException e){ service.updateShoppingBag(conn, subscriberId - 30, subscriberId, 5); }
            service.purchaseCart(conn, "1", 1, 2022, "1", "1", "1", "1", "1", "1", "1", 1);
        }
    }

    @AfterClass
    public void tearDown() {
        java.lang.System.out.println(paymentAdapter.getTime());
        java.lang.System.out.println(deliveryAdapter.getTime());
        end = java.lang.System.nanoTime();
        java.lang.System.out.println((end - start) / 1000000);
    }
}




