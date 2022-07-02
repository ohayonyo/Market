package LoadAndStressTests;

import DAL.RepoMock;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.testng.AssertJUnit.assertTrue;

public class LoadTestNewStoreAndAddItems {


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
        System system = new System(deliveryAdapter, paymentAdapter, userValidation, users, subscribers, stores, systemAdmins);
        String adminIdConn = system.entrance();
        system.registerAdmin(userName, password);
        service = new MarketService(system);
        service.login(adminIdConn, userName, password);
        deliveryAdapter.connect();
        paymentAdapter.connect();
        for(int i = 1; i <= 1000; i++) {
            service.register("s" + i, "123");
        }
        start = java.lang.System.nanoTime();
    }

    @Test(threadPoolSize = 100, invocationCount = 1000, timeOut = 15000)
    public void test() throws Exception {
        String conn = service.entrance();
        int id = subscriberId.getAndIncrement();
        service.login(conn, "s" + id, "123");
        int store = service.openStore(conn, "s" + id);
        service.addProductToStore(conn, store, "apple" + id, "fruit", "tasty", 500, 5.5);
        service.addProductToStore(conn, store, "banana" + (id + 1), "fruit", "tasty", 500, 5.5);
    }

    @AfterClass
    public void tearDown() {
        java.lang.System.out.println(paymentAdapter.getTime());
        java.lang.System.out.println(deliveryAdapter.getTime());
        end = java.lang.System.nanoTime();
        java.lang.System.out.println((end - start) / 1000000);
    }
}

