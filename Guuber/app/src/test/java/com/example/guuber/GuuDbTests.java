package com.example.guuber;

//import static org.junit.Assert.*;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GuuDbTests {
    private GuuDb db = new GuuDb();


    @Test
    public void TestUserSetup() {
        db.setUpUser("123@123.ca","Luke","Atme","LukeAtme","780 780 7807","rider");
        db.findUser("123@123.ca");
    }

}
