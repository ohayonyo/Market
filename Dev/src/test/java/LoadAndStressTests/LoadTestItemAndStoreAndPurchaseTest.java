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

public class LoadTestItemAndStoreAndPurchaseTest {


    private MarketService service;
    private final String userName = "Admin";
    private final String password = "123";
    private final ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Store> stores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> systemAdmins = new ConcurrentHashMap<>();

    private final AtomicInteger subscriberId = new AtomicInteger(1);

    private UserValidation userValidation ;
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
        userValidation = new UserValidation();
        systemAdmins.put(userName, new Subscriber());
        System system = new System(deliveryAdapter, paymentAdapter, userValidation, users, subscribers, stores, systemAdmins);
        String adminIdConn = system.entrance();
        system.registerAdmin(userName, password);
        service = new MarketService(system);
        service.login(adminIdConn, userName, password);
        deliveryAdapter.connect();
        paymentAdapter.connect();
        start = java.lang.System.nanoTime();
    }

    @Test(threadPoolSize = 1000, invocationCount = 1000, timeOut = 200000)
    public void test() throws Exception {
        int i = subscriberId.getAndIncrement();
        String conn = service.entrance();
        service.register("s" + i, "123");
        service.login(conn, "s" + i, "123");
        paymentAdapter.setFake(true);
        deliveryAdapter.setFake(true);
        if (i % 10 == 0) {
            int store = service.openStore(conn, "s" + i);
            int product1=1;
            int product2=1;
            for (int j = 0; j < 500; j++) {
                product1=service.addProductToStore(conn, store, "apple" + j, "fruit", "tasty", 10000, 5.5);
                product2= service.addProductToStore(conn, store, "banana" + j, "fruit", "tasty", 10000, 5.5);
            }

            if (i == 0)
                max = 1000;
            else
                max = 500;
            for (int x = 1; x <= max; x++) {
                String conn3 = service.entrance(); //guest buying the products
                service.updateShoppingBag(conn3, store, product1, 1);
                service.updateShoppingBag(conn3, store, product2, 1);
                service.purchaseCart(conn3, "1", 1, 2023, "1", "1", "1", "1", "1", "1", "1", 1);
            }
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