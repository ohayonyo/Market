import Service.Service;

public class InitState1 {
    @SuppressWarnings("unused")
    public static void run(Service marketService) throws Exception  {
        String u1 = "U1", u2 = "U2", u3 = "U3", u4 = "U4";
        String password = "123";
        marketService.registerAdmin(u1, password);
        marketService.register(u2, password);
        marketService.register(u3, password);
        marketService.register(u4, password);
        String u1Conn = marketService.entrance();
        marketService.login(u1Conn, u1, password);
        String u2Conn = marketService.entrance();
        marketService.login(u2Conn, u2, password);
        String u3Conn = marketService.entrance();
        marketService.login(u3Conn, u3, password);
        String u4Conn = marketService.entrance();
        marketService.login(u4Conn, u4, password);
        int usStore = marketService.openStore(u2Conn, "s");
        marketService.addProductToStore(u2Conn, usStore, "Bamba", "Snacks", "Baked Peanut Snack", 20, 30);
        marketService.appointStoreManager(u2Conn,usStore, u3);
        marketService.addInventoryManagementPermission(u2Conn,usStore, u3);
        marketService.logout(u2Conn);
        marketService.logout(u1Conn);
        marketService.logout(u3Conn);
        marketService.logout(u4Conn);
    }

}
