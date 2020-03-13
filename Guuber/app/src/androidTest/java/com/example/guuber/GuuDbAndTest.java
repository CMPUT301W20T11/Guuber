package com.example.guuber;

import org.junit.Test;

public class GuuDbAndTest {
    private GuuDb mockDb(){
//        FirebaseApp.initializeApp();
        return new GuuDb();
    }

    @Test
    public void TestUserSetup() {
        GuuDb gdb = mockDb();
        gdb.setUpUser("123@123.ca","Luke","Atme","LukeAtme","780 780 7807","rider");
        gdb.findUser("123@123.ca");

    }
}
